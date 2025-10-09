package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;


import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.InscripcionDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.VisitanteDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.CupoInsuficienteException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.DatosIncompletosException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.HorarioNoDisponibleException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.TerminosNoAceptadosException;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Inscripcion;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Talla;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Visitante;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.ActividadProgramadaRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.InscripcionRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.TallaRepository;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.VisitanteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        // 2. Buscar la actividad programada
        ActividadProgramada actividadProgramada = actividadProgramadaRepository
                .findById(inscripcionDTO.getIdActividadProgramada())
                .orElseThrow(() -> new RuntimeException("Actividad programada no encontrada"));

        // 3. Validar que hay cupo disponible para todos los participantes
        if (actividadProgramada.getCupoDisponible() == null ||
            actividadProgramada.getCupoDisponible() <= inscripcionDTO.getParticipantes().size()) {
            throw new CupoInsuficienteException();
        }

        // 4. Validar que la inscripción se realiza antes del inicio de la actividad
        if (inscripcionDTO.getFechaHoraInscripcion() != null) {
            if (!inscripcionDTO.getFechaHoraInscripcion().isBefore(actividadProgramada.getFechaHoraInicio())) {
                throw new HorarioNoDisponibleException();
            }
        }

        // 5. Procesar y guardar los visitantes
        List<Visitante> visitantes = new ArrayList<>();
        for (VisitanteDTO visitanteDTO : inscripcionDTO.getParticipantes()) {
            // Validar datos básicos del visitante
            if (visitanteDTO.getNombre() == null || visitanteDTO.getNombre().trim().isEmpty() ||
                visitanteDTO.getDni() == null || visitanteDTO.getFechaNacimiento() == null) {
                throw new DatosIncompletosException();
            }

            // Validar que si la actividad requiere vestimenta, el visitante proporcione la talla
            if (actividadProgramada.getActividad().getRequiereVestimenta() && visitanteDTO.getIdTallaVestimenta() == null) {
                throw new DatosIncompletosException();
            }

            // Crear el visitante
            Talla talla = null;
            if (visitanteDTO.getIdTallaVestimenta() != null) {
                talla = tallaRepository.findById(visitanteDTO.getIdTallaVestimenta())
                        .orElseThrow(DatosIncompletosException::new);
            }

            Visitante visitante = new Visitante(
                    visitanteDTO.getNombre(),
                    visitanteDTO.getDni(),
                    visitanteDTO.getFechaNacimiento(),
                    talla
            );

            // Guardar el visitante
            Visitante visitanteGuardado = visitanteRepository.save(visitante);
            visitantes.add(visitanteGuardado);
        }

        // Actualizar el cupo disponible
        actividadProgramada.setCupoDisponible(actividadProgramada.getCupoDisponible() - visitantes.size());
        actividadProgramadaRepository.save(actividadProgramada);

        // 6. Crear y guardar la inscripción
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
