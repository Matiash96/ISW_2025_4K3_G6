package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.ActividadProgramadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
