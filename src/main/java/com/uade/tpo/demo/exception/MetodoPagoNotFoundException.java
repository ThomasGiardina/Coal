package com.uade.tpo.demo.exception;

public class MetodoPagoNotFoundException extends RuntimeException {
    public MetodoPagoNotFoundException(String message) {
        super(message);
    }
}