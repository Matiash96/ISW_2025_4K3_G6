package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;


import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.InscripcionDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.VisitanteDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.CupoInsuficienteException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.DatosIncompletosException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.HorarioNoDisponibleException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.HorarioInscripcionInvalidoException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.TerminosNoAceptadosException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.VisitanteDuplicadoException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Inscripcion;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Talla;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Visitante;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.ActividadProgramadaRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.InscripcionRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.TallaRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.VisitanteRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InscripcionService {
    private final InscripcionRepository inscripcionRepository;
    private final VisitanteRepository visitanteRepository;
    private final ActividadProgramadaRepository actividadProgramadaRepository;
    private final TallaRepository tallaRepository;


    public InscripcionService(InscripcionRepository inscripcionRepository, VisitanteRepository visitanteRepository, ActividadProgramadaRepository actividadProgramadaRepository, TallaRepository tallaRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.visitanteRepository = visitanteRepository;
        this.actividadProgramadaRepository = actividadProgramadaRepository;
        this.tallaRepository = tallaRepository;
    }


    public Inscripcion inscribirVisitantes(InscripcionDTO inscripcionDTO) {
        // 1. Validar que se aceptaron los términos y condiciones
        if (inscripcionDTO.getAceptanTerminosYCondiciones() == null ||
            !inscripcionDTO.getAceptanTerminosYCondiciones()) {
            throw new TerminosNoAceptadosException();
        }

        // Validar que hay participantes
        if (inscripcionDTO.getParticipantes() == null || inscripcionDTO.getParticipantes().isEmpty()) {
            throw new DatosIncompletosException();
        }

        // Validar que no hay DNIs duplicados en la misma inscripción
        Set<Integer> dnisUnicos = new HashSet<>();
        for (VisitanteDTO visitanteDTO : inscripcionDTO.getParticipantes()) {
            if (visitanteDTO.getDni() != null) {
                if (!dnisUnicos.add(visitanteDTO.getDni())) {
                    throw new VisitanteDuplicadoException("No se pueden inscribir visitantes con el mismo DNI en una misma inscripción");
                }
            }
        }

        // 2. Buscar la actividad programada
        ActividadProgramada actividadProgramada = actividadProgramadaRepository
                .findById(inscripcionDTO.getActividadProgramadaId())
                .orElseThrow(() -> new RuntimeException("Actividad programada no encontrada"));

        // Validar que los visitantes no estén ya inscritos en esta actividad programada
        for (VisitanteDTO visitanteDTO : inscripcionDTO.getParticipantes()) {
            if (visitanteDTO.getDni() != null) {
                List<Inscripcion> inscripcionesExistentes = inscripcionRepository
                        .findByActividadProgramadaIdAndVisitanteDni(
                                inscripcionDTO.getActividadProgramadaId(),
                                visitanteDTO.getDni()
                        );
                if (!inscripcionesExistentes.isEmpty()) {
                    throw new VisitanteDuplicadoException("El visitante con DNI " + visitanteDTO.getDni() + " ya está inscrito en esta actividad programada");
                }
            }
        }

        // 3. Validar que hay cupo disponible para todos los participantes
        if (actividadProgramada.getCupoDisponible() == null ||
            actividadProgramada.getCupoDisponible() < inscripcionDTO.getParticipantes().size()) {
            throw new CupoInsuficienteException("No hay la suficiente cantidad de cupos para participantes");
        }

        // 4. Validar que la inscripción se realiza fuera del horario de la actividad
        if (inscripcionDTO.getFechaHoraInscripcion() != null) {
            if (inscripcionDTO.getFechaHoraInscripcion().isAfter(actividadProgramada.getFechaHoraInicio())) {
                throw new HorarioNoDisponibleException();
            }
        }

        // 5. Validar que el horario de inscripción sea entre 9:00 y 18:00 y no sea lunes
        if (inscripcionDTO.getFechaHoraInscripcion() != null) {
            int hora = inscripcionDTO.getFechaHoraInscripcion().getHour();
            DayOfWeek diaDeLaSemana = inscripcionDTO.getFechaHoraInscripcion().getDayOfWeek();

            // Validar que no sea lunes
            if (diaDeLaSemana == DayOfWeek.MONDAY) {
                throw new HorarioInscripcionInvalidoException("No se permiten inscripciones los días lunes");
            }

            // Validar que la hora esté entre 9:00 y 18:00
            if (hora < 9 || hora >= 18) {
                throw new HorarioInscripcionInvalidoException("El horario de inscripción debe ser entre las 9:00 y las 18:00 horas");
            }
        }

        // 6. Procesar y guardar los visitantes
        List<Visitante> visitantes = new ArrayList<>();
        for (VisitanteDTO visitanteDTO : inscripcionDTO.getParticipantes()) {
            // Validar datos básicos del visitante
            if (visitanteDTO.getNombre() == null || visitanteDTO.getNombre().trim().isEmpty() ||
                visitanteDTO.getDni() == null || visitanteDTO.getEdad() == null) {
                throw new DatosIncompletosException();
            }

            // Validar que si la actividad requiere vestimenta, el visitante proporcione la talla
            if (actividadProgramada.getActividad().getRequiereVestimenta() && visitanteDTO.getTallaId() == null) {
                throw new DatosIncompletosException();
            }

            // Crear el visitante
            Talla talla = null;
            if (visitanteDTO.getTallaId() != null) {
                talla = tallaRepository.findById(visitanteDTO.getTallaId())
                        .orElseThrow(DatosIncompletosException::new);
            }

            Visitante visitante = new Visitante(
                    visitanteDTO.getNombre(),
                    visitanteDTO.getDni(),
                    visitanteDTO.getEdad(),
                    talla
            );

            // Guardar el visitante
            Visitante visitanteGuardado = visitanteRepository.save(visitante);
            visitantes.add(visitanteGuardado);
        }

        // Actualizar el cupo disponible
        actividadProgramada.setCupoDisponible(actividadProgramada.getCupoDisponible() - visitantes.size());
        actividadProgramadaRepository.save(actividadProgramada);

        // 7. Crear y guardar la inscripción
        Inscripcion inscripcion = new Inscripcion(
                null, // El ID se generará automáticamente
                inscripcionDTO.getFechaHoraInscripcion(),
                actividadProgramada,
                inscripcionDTO.getAceptanTerminosYCondiciones(),
                visitantes
        );

        return inscripcionRepository.save(inscripcion);
    }
}
