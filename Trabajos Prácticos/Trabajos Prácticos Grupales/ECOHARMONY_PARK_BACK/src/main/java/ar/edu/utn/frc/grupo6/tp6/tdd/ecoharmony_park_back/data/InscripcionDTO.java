package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionDTO {
    private LocalDateTime fechaHoraInscripcion;
    private Long idActividadProgramada;
    private Boolean aceptanTerminosYCondiciones;
    private List<VisitanteDTO> participantes;
}
