package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Actividades")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idActividad")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "requiereVestimenta", nullable = false)
    @Convert(converter = org.hibernate.type.YesNoConverter.class)
    private Boolean requiereVestimenta;

    @Column(name = "terminosYCondiciones", nullable = false)
    private String terminosYCondiciones;
}
