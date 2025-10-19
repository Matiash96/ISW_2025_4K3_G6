package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Talla;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories.TallaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TallaService {

    @Autowired
    private TallaRepository tallaRepository;

    public List<Talla> obtenerTodas() {
        return tallaRepository.findAll();
    }

    public Optional<Talla> obtenerPorId(Long id) {
        return tallaRepository.findById(id);
    }

    public Talla guardar(Talla talla) {
        return tallaRepository.save(talla);
    }

    public void eliminar(Long id) {
        tallaRepository.deleteById(id);
    }
}
