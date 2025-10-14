package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.controllers;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.ActividadProgramada;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services.ActividadProgramadaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Actividades Programadas", description = "Consulta de horarios y disponibilidad de actividades")
public class ActividadProgramadaController {

    @Autowired
    private ActividadProgramadaService actividadProgramadaService;

    @Operation(
            summary = "Obtener actividades programadas disponibles",
            description = "Devuelve todas las actividades programadas disponibles para una actividad específica. " +
                    "Solo retorna actividades cuya fecha y hora de inicio sean posteriores al momento actual, " +
                    "con información de horarios, cupos disponibles y detalles de la actividad."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades programadas disponibles obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron actividades programadas para el ID especificado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/actividades-programadas/{idActividad}")
    public ResponseEntity<List<ActividadProgramada>> obtenerActividadesProgramadasDisponibles(
            @Parameter(description = "ID de la actividad para la cual se desean consultar los horarios programados",
                    example = "1", required = true)
            @PathVariable Long idActividad) {
        LocalDateTime fechaActual = LocalDateTime.now();
        List<ActividadProgramada> todasLasActividades = actividadProgramadaService.obtenerTodas();

        // Filtrar por actividad e ID y por fecha disponible
        List<ActividadProgramada> actividadesDisponibles = todasLasActividades.stream()
                .filter(ap -> ap.getActividad().getId().equals(idActividad))
                .filter(ap -> ap.getFechaHoraInicio().isAfter(fechaActual))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(actividadesDisponibles);
    }
}
