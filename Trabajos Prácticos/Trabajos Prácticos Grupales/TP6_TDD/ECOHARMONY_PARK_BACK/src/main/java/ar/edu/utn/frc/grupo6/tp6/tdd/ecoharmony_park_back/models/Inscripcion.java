package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.config.LocalDateTimeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Inscripciones")
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idInscripcion")
    private Long id;

    @Column(name = "fechaHoraInscripcion", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime fechaHoraInscripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idActividadProgramada", nullable = false)
    private ActividadProgramada actividadSeleccionada;

    @Column(name = "aceptanTyC", nullable = false)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private Boolean aceptanTerminosYCondiciones;

    @ManyToMany
    @JoinTable(
        name = "InscripcionesXVisitantes",
        joinColumns = @JoinColumn(name = "idInscripcion"),
        inverseJoinColumns = @JoinColumn(name = "dni")
    )
    private List<Visitante> participantes;
}
