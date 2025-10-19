package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
public class TerminosNoAceptadosException extends RuntimeException {
    public TerminosNoAceptadosException() {
        super();
    }
    public TerminosNoAceptadosException(String message) {
        super(message);
    }
}
