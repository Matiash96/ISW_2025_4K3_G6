package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions;

public class VisitanteDuplicadoException extends RuntimeException {
    public VisitanteDuplicadoException() {
        super("El visitante ya está inscrito en esta actividad programada o hay DNIs duplicados en la inscripción");
    }

    public VisitanteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}

