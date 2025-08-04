package com.josue.pedidos_ms.infrastructure.controller;

import com.josue.pedidos_ms.shared.dto.ResultadoCargaResponse;
import com.josue.pedidos_ms.shared.error.BusinessValidationException;
import com.josue.pedidos_ms.shared.error.CsvValidationException;
import com.josue.pedidos_ms.shared.logging.BaseLogger;
import com.josue.pedidos_ms.shared.logging.LogEvents;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.UUID;

/**
 * Manejador global de excepciones para proporcionar respuestas HTTP
 * consistentes.
 * Centraliza el manejo de errores y evita duplicación de código en los
 * controladores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseLogger {

    /**
     * Maneja errores de formato CSV inválido.
     * HTTP 400 BAD_REQUEST
     */
    @ExceptionHandler(CsvValidationException.class)
    public ResponseEntity<ResultadoCargaResponse> handleCsvValidationError(CsvValidationException ex) {
        logWarn(LogEvents.ERROR_LECTURA_CSV,
                "Error de validación CSV: {}", ex.getMessage());

        ResultadoCargaResponse response = new ResultadoCargaResponse(
                0,
                0,
                List.of(new ResultadoCargaResponse.ErrorDetalle(0, "", ex.getMessage(), "CSV_INVALIDO")),
                UUID.randomUUID().toString(),
                0L);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Maneja errores de validación de negocio.
     * HTTP 422 UNPROCESSABLE_ENTITY
     */
    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ResultadoCargaResponse> handleBusinessValidationError(BusinessValidationException ex) {
        logWarn(LogEvents.ERROR_VALIDACION,
                "Error de validación de negocio: {}", ex.getMessage());

        ResultadoCargaResponse response = new ResultadoCargaResponse(
                0,
                0,
                List.of(new ResultadoCargaResponse.ErrorDetalle(0, "", ex.getMessage(), "VALIDACION_NEGOCIO")),
                UUID.randomUUID().toString(),
                0L);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }

    /**
     * Maneja errores de tamaño de archivo excedido.
     * HTTP 413 PAYLOAD_TOO_LARGE
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResultadoCargaResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        logWarn(LogEvents.ERROR_LECTURA_CSV,
                "Archivo demasiado grande: {}", ex.getMessage());

        ResultadoCargaResponse response = new ResultadoCargaResponse(
                0,
                0,
                List.of(new ResultadoCargaResponse.ErrorDetalle(0, "", "El archivo excede el tamaño máximo permitido",
                        "ARCHIVO_GRANDE")),
                UUID.randomUUID().toString(),
                0L);

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(response);
    }

    /**
     * Maneja errores inesperados del sistema.
     * HTTP 500 INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultadoCargaResponse> handleUnexpectedError(Exception ex) {
        logError(LogEvents.ERROR_SISTEMA,
                "Error inesperado del sistema", ex);

        ResultadoCargaResponse response = new ResultadoCargaResponse(
                0,
                0,
                List.of(new ResultadoCargaResponse.ErrorDetalle(0, "", "Error inesperado en el servidor",
                        "ERROR_INTERNO")),
                UUID.randomUUID().toString(),
                0L);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
