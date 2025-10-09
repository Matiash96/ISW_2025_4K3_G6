package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
public class CupoInsuficienteException extends RuntimeException {
    public CupoInsuficienteException() {
        super();
    }

    public CupoInsuficienteException(String message) {
        super(message);
    }
}
