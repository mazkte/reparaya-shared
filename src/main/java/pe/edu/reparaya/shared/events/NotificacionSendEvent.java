package pe.edu.reparaya.shared.events;

import pe.edu.reparaya.shared.events.types.ChannelType;
import pe.edu.reparaya.shared.events.types.EventType;

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
  EventType tipo,
  ChannelType canal,
  String destinatario,
  String mensaje,
  Instant timestamp

) {
 public NotificacionSendEvent {
  if (eventId == null) eventId = UUID.randomUUID();
  if (timestamp == null) timestamp = Instant.now();
 }

 public static NotificacionSendEvent whatsapp(UUID reporteId, EventType tipo,
                                              String telefono, String mensaje) {
  return new NotificacionSendEvent(
    UUID.randomUUID(), reporteId, tipo,
    ChannelType.WHATSAPP, telefono, mensaje, Instant.now()
  );
 }

 public static NotificacionSendEvent email(UUID reporteId, EventType tipo,
                                           String email, String mensaje) {
  return new NotificacionSendEvent(
    UUID.randomUUID(), reporteId, tipo,
    ChannelType.EMAIL, email, mensaje, Instant.now()
  );
 }
}
