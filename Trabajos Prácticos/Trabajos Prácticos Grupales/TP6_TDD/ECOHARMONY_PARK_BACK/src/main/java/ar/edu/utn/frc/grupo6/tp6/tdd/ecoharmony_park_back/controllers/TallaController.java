package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.controllers;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Talla;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services.TallaService;
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
@Tag(name = "Tallas", description = "Gestión de tallas de vestimenta para actividades")
public class TallaController {

    @Autowired
    private TallaService tallaService;

    @Operation(
            summary = "Obtener todas las tallas",
            description = "Devuelve una lista completa de todas las tallas disponibles (XS, S, M, L, XL, XXL) para actividades que requieren vestimenta específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tallas obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/tallas")
    public ResponseEntity<List<Talla>> obtenerTodasLasTallas() {
        System.out.println("=== DEBUG: Endpoint /api/v1/tallas llamado ===");
        List<Talla> tallas = tallaService.obtenerTodas(); // Usar el método existente
        System.out.println("=== DEBUG: Cantidad de tallas encontradas: " + tallas.size() + " ===");
        for (Talla talla : tallas) {
            System.out.println("=== DEBUG: Talla ID: " + talla.getId() + ", Nombre: " + talla.getNombre() + " ===");
        }
        return ResponseEntity.ok(tallas);
    }
}
