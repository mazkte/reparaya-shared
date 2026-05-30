package pe.edu.reparaya.shared.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento publicado por worker-service cuando asigna una empresa a un reporte.
 * <p>
 * Topic: report.assigned
 * Consumidores: report-service, notification-service
 */
public record ReporteAssignedEvent(

  UUID eventId,
  UUID reporteId,
  UUID empresaId,
  String empresaNombre,
  String emailCoordinador,
  String whatsappCoordinador,
  String ciudadanoPhone,
  Instant timestamp

) {
 public ReporteAssignedEvent {
  if (eventId == null) eventId = UUID.randomUUID();
  if (timestamp == null) timestamp = Instant.now();
 }

 public static ReporteAssignedEvent of(UUID reporteId, UUID empresaId,
                                       String empresaNombre, String emailCoordinador,
                                       String whatsappCoordinador, String ciudadanoPhone) {
  return new ReporteAssignedEvent(
    UUID.randomUUID(), reporteId, empresaId,
    empresaNombre, emailCoordinador, whatsappCoordinador,
    ciudadanoPhone, Instant.now()
  );
 }
}
