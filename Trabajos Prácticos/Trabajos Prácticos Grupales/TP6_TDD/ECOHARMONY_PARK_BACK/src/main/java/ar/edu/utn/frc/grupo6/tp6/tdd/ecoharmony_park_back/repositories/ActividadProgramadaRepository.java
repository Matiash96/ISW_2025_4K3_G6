package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActividadProgramadaRepository extends JpaRepository<ActividadProgramada, Long> {}