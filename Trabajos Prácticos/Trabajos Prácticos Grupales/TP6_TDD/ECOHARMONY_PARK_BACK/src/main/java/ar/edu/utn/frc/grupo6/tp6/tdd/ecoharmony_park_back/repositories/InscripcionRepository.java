package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.repositories;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Buscar todas las inscripciones de una actividad programada
    List<Inscripcion> findByActividadSeleccionada(ActividadProgramada actividadProgramada);

    // Verificar si un visitante (por DNI) ya está inscrito en una actividad programada específica
    @Query("SELECT i FROM Inscripcion i JOIN i.participantes v WHERE i.actividadSeleccionada.id = :actividadProgramadaId AND v.dni = :dni")
    List<Inscripcion> findByActividadProgramadaIdAndVisitanteDni(@Param("actividadProgramadaId") Long actividadProgramadaId, @Param("dni") Integer dni);
}
