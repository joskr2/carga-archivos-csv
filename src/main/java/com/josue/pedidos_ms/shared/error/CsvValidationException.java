package com.josue.pedidos_ms.shared.error;

/**
 * Excepción lanzada cuando el archivo CSV tiene un formato inválido o es
 * ilegible.
 * Resulta en un HTTP 400 BAD_REQUEST.
 */
public class CsvValidationException extends RuntimeException {

  public CsvValidationException(String message) {
    super(message);
  }

  public CsvValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
