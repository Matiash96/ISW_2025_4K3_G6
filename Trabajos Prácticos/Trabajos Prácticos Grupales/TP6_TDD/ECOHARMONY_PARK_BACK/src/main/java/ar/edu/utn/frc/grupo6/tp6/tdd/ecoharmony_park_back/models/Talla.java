package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Tallas")
public class Talla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTalla")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
}
