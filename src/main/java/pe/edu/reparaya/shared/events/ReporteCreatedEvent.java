package pe.edu.reparaya.shared.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento publicado por bot-service cuando un ciudadano completa
 * el flujo conversacional de WhatsApp.
 * <p>
 * Topic: report.created
 * Consumidores: report-service, worker-service
 */
public record ReporteCreatedEvent(

  UUID eventId,           // UUID único para idempotencia en consumidores
  String phoneNumber,     // Número WhatsApp del ciudadano (formato E.164)
  String categoria,       // VIALIDAD | ALUMBRADO | AGUA_POTABLE | ALCANTARILLADO | OTRO
  Double latitud,
  Double longitud,
  String descripcion,
  String mediaUrl,        // URL temporal de la foto (puede ser null)
  Instant timestamp

) {
 // Constructor compacto con validaciones
 public ReporteCreatedEvent {
  if (eventId == null) eventId = UUID.randomUUID();
  if (timestamp == null) timestamp = Instant.now();
  if (phoneNumber == null || phoneNumber.isBlank())
   throw new IllegalArgumentException("phoneNumber es requerido");
  if (categoria == null || categoria.isBlank())
   throw new IllegalArgumentException("categoria es requerida");
 }

 // Factory method para facilitar la creación
 public static ReporteCreatedEvent of(String phoneNumber, String categoria,
                                      Double latitud, Double longitud,
                                      String descripcion, String mediaUrl) {
  return new ReporteCreatedEvent(
    UUID.randomUUID(), phoneNumber, categoria,
    latitud, longitud, descripcion, mediaUrl, Instant.now()
  );
 }
}
