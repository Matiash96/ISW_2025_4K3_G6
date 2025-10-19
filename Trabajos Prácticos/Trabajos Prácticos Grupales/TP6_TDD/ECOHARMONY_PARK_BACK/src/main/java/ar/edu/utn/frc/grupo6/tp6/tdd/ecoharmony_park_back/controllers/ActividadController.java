package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.controllers;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Actividad;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services.ActividadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Actividades", description = "Gestión del catálogo de actividades del parque")
public class ActividadController {

    @Autowired
    private ActividadService actividadService;

    @Operation(
            summary = "Obtener todas las actividades",
            description = "Devuelve el catálogo completo de actividades disponibles en el parque (Tirolesa, Safari, Palestra, etc.) con información sobre si requieren vestimenta específica y sus términos y condiciones."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catálogo de actividades obtenido exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/actividades")
    public ResponseEntity<List<Actividad>> obtenerTodasLasActividades() {
        List<Actividad> actividades = actividadService.obtenerTodas(); // Usar el método existente
        return ResponseEntity.ok(actividades);
    }
}
