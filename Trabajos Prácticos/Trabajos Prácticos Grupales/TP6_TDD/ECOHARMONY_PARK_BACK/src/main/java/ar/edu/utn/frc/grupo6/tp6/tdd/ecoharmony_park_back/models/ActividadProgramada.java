package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.config.LocalDateTimeConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ActividadesProgramadas")
public class ActividadProgramada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idActividadProgramada")
    private Long id;

    @Column(name = "fechaHoraInicio", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fechaHoraFin", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime fechaHoraFin;

    @Column(name = "cupoDisponible", nullable = false)
    private Integer cupoDisponible;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idActividad", nullable = false)
    private Actividad actividad;
}
