package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para crear y responder inscripciones de visitantes a actividades programadas")
public class InscripcionDTO {

    @Schema(description = "Fecha y hora de la inscripción (debe estar dentro del horario de la actividad)",
            example = "2025-10-15T10:30:00", required = true)
    private LocalDateTime fechaHoraInscripcion;

    @Schema(description = "ID de la actividad programada a la cual se desea inscribir",
            example = "1", required = true)
    private Long actividadProgramadaId;

    @Schema(description = "Indica si los visitantes aceptan los términos y condiciones",
            example = "true", required = true)
    private Boolean aceptanTerminosYCondiciones;

    @Schema(description = "Lista de visitantes que se inscriben a la actividad", required = true)
    private List<VisitanteDTO> participantes;
}
