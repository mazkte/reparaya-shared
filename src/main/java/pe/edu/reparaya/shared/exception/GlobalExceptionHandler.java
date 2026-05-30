package pe.edu.reparaya.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Manejador global de excepciones para todos los microservicios.
 * Garantiza que ningún stack trace llegue al cliente (OWASP A10:2025).
 * El detalle del error se registra internamente con Slf4j.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

 // ── Excepciones de dominio ────────────────────────────────

 @ExceptionHandler(ReparaYaException.RecursoNoEncontradoException.class)
 public ResponseEntity<ErrorResponse> handleNotFound(ReparaYaException ex) {
  log.warn("Recurso no encontrado: {}", ex.getMessage());
  return ResponseEntity
    .status(ex.getStatus())
    .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage(),
      ex.getStatus().value()));
 }

 @ExceptionHandler({
   ReparaYaException.EstadoInvalidoException.class,
   ReparaYaException.CapacidadExcedidaException.class,
   ReparaYaException.ContratoVencidoException.class,
   ReparaYaException.DuplicadoException.class
 })
 public ResponseEntity<ErrorResponse> handleConflict(ReparaYaException ex) {
  log.warn("Conflicto de negocio: {}", ex.getMessage());
  return ResponseEntity
    .status(ex.getStatus())
    .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage(),
      ex.getStatus().value()));
 }

 @ExceptionHandler(ReparaYaException.AccesoDenegadoException.class)
 public ResponseEntity<ErrorResponse> handleForbidden(ReparaYaException ex) {
  log.warn("Acceso denegado: {}", ex.getMessage());
  return ResponseEntity
    .status(HttpStatus.FORBIDDEN)
    .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage(),
      HttpStatus.FORBIDDEN.value()));
 }

 // ── Errores de validación (@Valid) ────────────────────────

 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
  List<String> details = ex.getBindingResult().getFieldErrors()
    .stream()
    .map(FieldError::getDefaultMessage)
    .toList();
  log.warn("Error de validación: {}", details);
  return ResponseEntity
    .status(HttpStatus.BAD_REQUEST)
    .body(ErrorResponse.withDetails(
      "VALIDACION_FALLIDA",
      "Los datos enviados no son válidos",
      HttpStatus.BAD_REQUEST.value(),
      details
    ));
 }

 // ── Acceso denegado por Spring Security ──────────────────

 @ExceptionHandler(AccessDeniedException.class)
 public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
  log.warn("Acceso denegado por Spring Security");
  return ResponseEntity
    .status(HttpStatus.FORBIDDEN)
    .body(ErrorResponse.of("ACCESO_DENEGADO",
      "No tiene permisos para realizar esta operación",
      HttpStatus.FORBIDDEN.value()));
 }

 // ── Cualquier excepción no controlada ────────────────────

 @ExceptionHandler(Exception.class)
 public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
  // Loguear el detalle internamente — NUNCA enviarlo al cliente
  log.error("Error interno no controlado: {}", ex.getMessage(), ex);
  return ResponseEntity
    .status(HttpStatus.INTERNAL_SERVER_ERROR)
    .body(ErrorResponse.of(
      "ERROR_INTERNO",
      "Ocurrió un error interno. Por favor intente nuevamente.",
      HttpStatus.INTERNAL_SERVER_ERROR.value()
    ));
 }
}
