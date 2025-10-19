package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Actividad;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadService {

    @Autowired
    private ActividadRepository actividadRepository;

    public List<Actividad> obtenerTodas() {
        return actividadRepository.findAll();
    }

    public Optional<Actividad> obtenerPorId(Long id) {
        return actividadRepository.findById(id);
    }

    public Actividad guardar(Actividad actividad) {
        return actividadRepository.save(actividad);
    }

    public void eliminar(Long id) {
        actividadRepository.deleteById(id);
    }
}
