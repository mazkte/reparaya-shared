package pe.edu.reparaya.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base del dominio ReparaYa.
 * Todas las excepciones de negocio heredan de esta clase.
 */
public class ReparaYaException extends RuntimeException {

 private final HttpStatus status;
 private final String errorCode;

 public ReparaYaException(String message, HttpStatus status, String errorCode) {
  super(message);
  this.status = status;
  this.errorCode = errorCode;
 }

 public HttpStatus getStatus() {
  return status;
 }

 public String getErrorCode() {
  return errorCode;
 }

 // ── Excepciones específicas del dominio ──────────────────

 public static class RecursoNoEncontradoException extends ReparaYaException {
  public RecursoNoEncontradoException(String recurso, Object id) {
   super("%s con id '%s' no encontrado".formatted(recurso, id),
     HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO");
  }
 }

 public static class EstadoInvalidoException extends ReparaYaException {
  public EstadoInvalidoException(String mensaje) {
   super(mensaje, HttpStatus.CONFLICT, "ESTADO_INVALIDO");
  }
 }

 public static class CapacidadExcedidaException extends ReparaYaException {
  public CapacidadExcedidaException(String empresa) {
   super("La empresa '%s' ha alcanzado su capacidad diaria máxima".formatted(empresa),
     HttpStatus.CONFLICT, "CAPACIDAD_EXCEDIDA");
  }
 }

 public static class ContratoVencidoException extends ReparaYaException {
  public ContratoVencidoException(String empresa) {
   super("La empresa '%s' no tiene contrato vigente".formatted(empresa),
     HttpStatus.CONFLICT, "CONTRATO_VENCIDO");
  }
 }

 public static class AccesoDenegadoException extends ReparaYaException {
  public AccesoDenegadoException() {
   super("No tiene permisos para realizar esta operación",
     HttpStatus.FORBIDDEN, "ACCESO_DENEGADO");
  }
 }

 public static class DuplicadoException extends ReparaYaException {
  public DuplicadoException(String mensaje) {
   super(mensaje, HttpStatus.CONFLICT, "DUPLICADO");
  }
 }
}
