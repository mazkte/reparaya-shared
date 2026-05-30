package pe.edu.reparaya.shared.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento publicado cuando se requiere enviar una notificación.
 * <p>
 * Topic: notification.send
 * Consumidores: notification-service
 */
public record NotificacionSendEvent(

  UUID eventId,
  UUID reporteId,
  String tipo,
  // REPORTE_CREADO | EMPRESA_ASIGNADA | EN_PROGRESO | EJECUTADO | CERRADO | ALERTA_SIN_ASIGNAR
  String canal,              // WHATSAPP | EMAIL
  String destinatario,       // Número de teléfono o dirección email
  String mensaje,
  Instant timestamp

) {
 public NotificacionSendEvent {
  if (eventId == null) eventId = UUID.randomUUID();
  if (timestamp == null) timestamp = Instant.now();
 }

 public static NotificacionSendEvent whatsapp(UUID reporteId, String tipo,
                                              String telefono, String mensaje) {
  return new NotificacionSendEvent(
    UUID.randomUUID(), reporteId, tipo,
    "WHATSAPP", telefono, mensaje, Instant.now()
  );
 }

 public static NotificacionSendEvent email(UUID reporteId, String tipo,
                                           String email, String mensaje) {
  return new NotificacionSendEvent(
    UUID.randomUUID(), reporteId, tipo,
    "EMAIL", email, mensaje, Instant.now()
  );
 }
}
