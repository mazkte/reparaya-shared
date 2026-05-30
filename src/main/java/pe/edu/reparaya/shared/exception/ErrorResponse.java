package pe.edu.reparaya.shared.exception;

import java.time.Instant;
import java.util.List;

/**
 * Respuesta de error estandarizada — nunca expone stack traces al exterior.
 * OWASP A10:2025 — Mishandling of Exceptional Conditions.
 */
public record ErrorResponse(
  String errorCode,
  String message,
  int status,
  Instant timestamp,
  List<String> details     // Errores de validación (puede ser null)
) {
 public static ErrorResponse of(String errorCode, String message, int status) {
  return new ErrorResponse(errorCode, message, status, Instant.now(), null);
 }

 public static ErrorResponse withDetails(String errorCode, String message,
                                         int status, List<String> details) {
  return new ErrorResponse(errorCode, message, status, Instant.now(), details);
 }
}
