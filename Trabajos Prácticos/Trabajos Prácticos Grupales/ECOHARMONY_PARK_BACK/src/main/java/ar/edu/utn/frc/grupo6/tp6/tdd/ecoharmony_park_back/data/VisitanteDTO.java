package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitanteDTO {
    private String nombre;
    private Integer dni;
    private LocalDate fechaNacimiento;
    private Long idTallaVestimenta;
}
