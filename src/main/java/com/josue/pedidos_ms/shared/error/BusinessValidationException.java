package com.josue.pedidos_ms.shared.error;

/**
 * Excepción lanzada cuando hay errores de validación de negocio en los datos.
 * Resulta en un HTTP 422 UNPROCESSABLE_ENTITY.
 */
public class BusinessValidationException extends RuntimeException {

  public BusinessValidationException(String message) {
    super(message);
  }

  public BusinessValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
