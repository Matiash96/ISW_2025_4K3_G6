package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO que representa los datos de un visitante para inscripciones")
public class VisitanteDTO {

    @Schema(description = "Nombre completo del visitante",
            example = "Juan Pérez", required = true)
    private String nombre;

    @Schema(description = "Documento Nacional de Identidad del visitante",
            example = "12345678", required = true)
    private Integer dni;

    @Schema(description = "Edad del visitante en años",
            example = "30", required = true)
    private Integer edad;

    @Schema(description = "ID de la talla de vestimenta (requerida solo para actividades que requieren equipamiento específico, puede ser null para otras actividades)",
            example = "4", required = false)
    private Long tallaId;
}
