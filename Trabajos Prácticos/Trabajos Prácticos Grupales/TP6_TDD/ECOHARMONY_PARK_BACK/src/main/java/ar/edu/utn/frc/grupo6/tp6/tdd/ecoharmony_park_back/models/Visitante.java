package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Visitantes")
public class Visitante {
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Id
    private Integer dni;

    @Column(name = "edad", nullable = false)
    private Integer edad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idTalla")
    private Talla tallaVestimenta;
}
