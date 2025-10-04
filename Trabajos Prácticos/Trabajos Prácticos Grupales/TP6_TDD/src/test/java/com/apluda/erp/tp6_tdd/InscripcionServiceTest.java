/*package com.apluda.erp.tp6_tdd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Tp6TddApplicationTests {

    @Test
    void contextLoads() {
    }

}*/

package com.ecoharmonypark.service;

import com.ecoharmonypark.model.Actividad;
import com.ecoharmonypark.model.Visitante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Tp6TddApplicationTests {
    
    private InscripcionService inscripcionService;
    private Actividad tirolesa;
    private Actividad jardineria;

    @BeforeEach
    void setUp() {
        inscripcionService = new InscripcionService();
        tirolesa = new Actividad("Tirolesa", 5, true, List.of("10:00", "12:00"));
        jardineria = new Actividad("Jardinería", 3, false, List.of("09:00", "11:00"));
    }

    // 1️⃣ Inscripción exitosa (pasa)
    @Test
    void inscripcionExitosa() {
        Visitante v = new Visitante("Ana", "12345678", 25, "M");
        boolean resultado = inscripcionService.inscribirse(tirolesa, "10:00", List.of(v), true);
        assertTrue(resultado);
    }

    // 2️⃣ Sin cupos disponibles (falla)
    @Test
    void fallaPorSinCupo() {
        tirolesa.setCuposDisponibles(0);
        Visitante v = new Visitante("Carlos", "87654321", 30, "L");
        boolean resultado = inscripcionService.inscribirse(tirolesa, "10:00", List.of(v), true);
        assertFalse(resultado);
    }

    // 3️⃣ Sin ingresar talle de vestimenta porque la actividad no lo requiere (pasa)
    @Test
    void inscripcionSinTalleCuandoNoSeRequiere() {
        Visitante v = new Visitante("Lucía", "99887755", 22, null);
        boolean resultado = inscripcionService.inscribirse(jardineria, "09:00", List.of(v), true);
        assertTrue(resultado);
    }

    // 4️⃣ Horario no disponible (falla)
    @Test
    void fallaPorHorarioInvalido() {
        Visitante v = new Visitante("Laura", "22334455", 28, "S");
        boolean resultado = inscripcionService.inscribirse(tirolesa, "08:00", List.of(v), true);
        assertFalse(resultado);
    }

    // 5️⃣ No aceptar términos y condiciones (falla)
    @Test
    void fallaPorNoAceptarTerminos() {
        Visitante v = new Visitante("Sofia", "33445566", 20, "M");
        boolean resultado = inscripcionService.inscribirse(tirolesa, "10:00", List.of(v), false);
        assertFalse(resultado);
    }

    // 6️⃣ Falta de talle cuando la actividad lo requiere (falla)
    @Test
    void fallaPorFaltaDeTalleCuandoSeRequiere() {
        Visitante v = new Visitante("Mateo", "99887766", 22, null);
        boolean resultado = inscripcionService.inscribirse(tirolesa, "10:00", List.of(v), true);
        assertFalse(resultado);
    }
}
