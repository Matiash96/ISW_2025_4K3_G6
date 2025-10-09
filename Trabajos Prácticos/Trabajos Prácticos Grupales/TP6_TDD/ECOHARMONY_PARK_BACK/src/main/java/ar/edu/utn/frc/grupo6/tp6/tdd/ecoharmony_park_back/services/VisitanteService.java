package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Visitante;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.VisitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitanteService {

    @Autowired
    private VisitanteRepository visitanteRepository;

    public List<Visitante> obtenerTodos() {
        return visitanteRepository.findAll();
    }

    public Optional<Visitante> obtenerPorDni(Integer dni) {
        return visitanteRepository.findById(dni);
    }

    public Visitante guardar(Visitante visitante) {
        return visitanteRepository.save(visitante);
    }

    public void eliminar(Integer dni) {
        visitanteRepository.deleteById(dni);
    }
}
