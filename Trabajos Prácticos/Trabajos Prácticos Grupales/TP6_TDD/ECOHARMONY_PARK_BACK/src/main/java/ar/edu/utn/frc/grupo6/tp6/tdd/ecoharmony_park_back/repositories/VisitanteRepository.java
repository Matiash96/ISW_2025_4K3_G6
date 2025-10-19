package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitanteRepository extends JpaRepository<Visitante, Integer> {
    Optional<Visitante> findByDni(Integer dni);
}
