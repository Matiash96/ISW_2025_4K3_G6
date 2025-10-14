package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.ActividadProgramadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActividadProgramadaService {

    @Autowired
    private ActividadProgramadaRepository actividadProgramadaRepository;

    public List<ActividadProgramada> obtenerTodas() {
        return actividadProgramadaRepository.findAll();
    }

    public Optional<ActividadProgramada> obtenerPorId(Long id) {
        return actividadProgramadaRepository.findById(id);
    }

    public ActividadProgramada guardar(ActividadProgramada actividadProgramada) {
        return actividadProgramadaRepository.save(actividadProgramada);
    }

    public void eliminar(Long id) {
        actividadProgramadaRepository.deleteById(id);
    }

    public List<ActividadProgramada> obtenerActividadesProgramadasDisponiblesPorActividad(Long idActividad, LocalDateTime fechaActual) {
        List<ActividadProgramada> todasLasActividadesProgramadas = actividadProgramadaRepository.findAll();

        return todasLasActividadesProgramadas.stream()
                .filter(ap -> ap.getActividad().getId().equals(idActividad))
                .filter(ap -> ap.getFechaHoraInicio().isAfter(fechaActual))
                .collect(Collectors.toList());
    }
}
