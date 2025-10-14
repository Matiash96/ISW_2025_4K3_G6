package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.controllers;

import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.InscripcionDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.data.VisitanteDTO;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Inscripcion;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.models.Visitante;
import ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.services.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Inscripciones", description = "Gestión de inscripciones de visitantes a actividades programadas")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Operation(
            summary = "Crear nueva inscripción",
            description = "Registra una nueva inscripción de uno o varios visitantes a una actividad programada. " +
                    "Valida automáticamente:\n" +
                    "• Cupo disponible en la actividad\n" +
                    "• Horario de inscripción dentro del rango permitido\n" +
                    "• Aceptación de términos y condiciones\n" +
                    "• Talla requerida para actividades que la necesiten\n" +
                    "• Datos completos de visitantes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Inscripción creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InscripcionDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación - Datos incompletos, términos no aceptados, talla requerida no proporcionada, etc.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Términos no aceptados",
                                            value = "{\"error\": \"Debe aceptar los términos y condiciones para realizar la inscripción\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Datos incompletos",
                                            value = "{\"error\": \"Faltan datos obligatorios del visitante o la talla es requerida para esta actividad\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto - Cupo insuficiente o horario no disponible",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Cupo insuficiente",
                                            value = "{\"error\": \"No hay cupo suficiente para la cantidad de visitantes solicitada\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Horario no disponible",
                                            value = "{\"error\": \"La inscripción debe realizarse dentro del horario de la actividad\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/inscripciones")
    public ResponseEntity<?> crearInscripcion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la inscripción incluyendo visitantes, actividad programada y aceptación de términos",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InscripcionDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Inscripción simple sin talla",
                                            description = "Ejemplo de inscripción para una actividad que no requiere talla específica",
                                            value = """
                                                    {
                                                      "fechaHoraInscripcion": "2025-10-15T10:30:00",
                                                      "actividadProgramadaId": 1,
                                                      "aceptanTerminosYCondiciones": true,
                                                      "participantes": [
                                                        {
                                                          "nombre": "Juan Pérez",
                                                          "dni": 12345678,
                                                          "edad": 30,
                                                          "tallaId": null
                                                        }
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Inscripción con talla requerida",
                                            description = "Ejemplo de inscripción para una actividad que requiere talla específica (ej: Tirolesa)",
                                            value = """
                                                    {
                                                      "fechaHoraInscripcion": "2025-10-15T14:00:00",
                                                      "actividadProgramadaId": 2,
                                                      "aceptanTerminosYCondiciones": true,
                                                      "participantes": [
                                                        {
                                                          "nombre": "María García",
                                                          "dni": 87654321,
                                                          "edad": 25,
                                                          "tallaId": 3
                                                        },
                                                        {
                                                          "nombre": "Carlos López",
                                                          "dni": 11223344,
                                                          "edad": 35,
                                                          "tallaId": 4
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody InscripcionDTO inscripcionDTO) {
        try {
            Inscripcion inscripcionCreada = inscripcionService.inscribirVisitantes(inscripcionDTO);

            // Convertir la inscripción a DTO para la respuesta
            InscripcionDTO respuesta = convertirInscripcionADTO(inscripcionCreada);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.TerminosNoAceptadosException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Debe aceptar los términos y condiciones para realizar la inscripción\"}");
        } catch (ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.DatosIncompletosException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Faltan datos obligatorios del visitante o la talla es requerida para esta actividad\"}");
        } catch (ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.CupoInsuficienteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"No hay cupo suficiente para la cantidad de visitantes solicitada\"}");
        } catch (ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions.HorarioNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"La inscripción debe realizarse dentro del horario de la actividad\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error interno del servidor\"}");
        }
    }

    private InscripcionDTO convertirInscripcionADTO(Inscripcion inscripcion) {
        List<VisitanteDTO> visitantesDTO = inscripcion.getParticipantes().stream()
                .map(this::convertirVisitanteADTO)
                .collect(Collectors.toList());

        return new InscripcionDTO(
                inscripcion.getFechaHoraInscripcion(),
                inscripcion.getActividadSeleccionada().getId(),
                inscripcion.getAceptanTerminosYCondiciones(),
                visitantesDTO
        );
    }

    private VisitanteDTO convertirVisitanteADTO(Visitante visitante) {
        return new VisitanteDTO(
                visitante.getNombre(),
                visitante.getDni(),
                visitante.getEdad(),
                visitante.getTallaVestimenta() != null ? visitante.getTallaVestimenta().getId() : null
        );
    }
}
