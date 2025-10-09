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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private VisitanteRepository visitanteRepository;

    @Autowired
    private ActividadProgramadaRepository actividadProgramadaRepository;

    @Autowired
    private TallaRepository tallaRepository;

    public Inscripcion inscribirVisitantes(InscripcionDTO inscripcionDTO) {
        // 1. Validar que se aceptaron los términos y condiciones
        if (inscripcionDTO.getAceptanTerminosYCondiciones() == null ||
            !inscripcionDTO.getAceptanTerminosYCondiciones()) {
            throw new TerminosNoAceptadosException();
        }

        // 2. Buscar la actividad programada
        ActividadProgramada actividadProgramada = actividadProgramadaRepository
                .findById(inscripcionDTO.getIdActividadProgramada())
                .orElseThrow(() -> new RuntimeException("Actividad programada no encontrada"));

        // 3. Validar que hay cupo disponible
        if (actividadProgramada.getCupoDisponible() == null ||
            actividadProgramada.getCupoDisponible() <= 0) {
            throw new CupoInsuficienteException();
        }

        // 4. Validar que la inscripción se realiza dentro del horario de la actividad
        if (inscripcionDTO.getFechaHoraInscripcion() != null) {
            if (inscripcionDTO.getFechaHoraInscripcion().isBefore(actividadProgramada.getFechaHoraInicio()) ||
                inscripcionDTO.getFechaHoraInscripcion().isAfter(actividadProgramada.getFechaHoraFin())) {
                throw new HorarioNoDisponibleException();
            }
        }

        // 5. Procesar y guardar los visitantes
        List<Visitante> visitantes = new ArrayList<>();
        for (VisitanteDTO visitanteDTO : inscripcionDTO.getParticipantes()) {
            // Validar que si la actividad requiere vestimenta, el visitante proporcione la talla
            if (actividadProgramada.getActividad().getRequiereVestimenta()) {
                if (visitanteDTO.getIdTallaVestimenta() == null) {
                    throw new DatosIncompletosException();
                }
            }

            // Crear el visitante
            Talla talla = null;
            if (visitanteDTO.getIdTallaVestimenta() != null) {
                talla = tallaRepository.findById(visitanteDTO.getIdTallaVestimenta())
                        .orElse(null);
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
