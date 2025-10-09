package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Visitante {
    private String nombre;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dni;
    private Integer edad;
    private Talla tallaVestimenta;
}
