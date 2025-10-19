package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
public class HorarioInscripcionInvalidoException extends RuntimeException{
    public HorarioInscripcionInvalidoException() {
        super();
    }
    public HorarioInscripcionInvalidoException(String message) {
        super(message);
    }
}

