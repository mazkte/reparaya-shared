package pe.edu.reparaya.shared.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento publicado por report-service en cada cambio de estado.
 * <p>
 * Topic: report.status.changed
 * Consumidores: notification-service
 */
public record ReporteStatusChangedEvent(

  UUID eventId,
  String reporteId,
  String estadoAnterior,
  String estadoNuevo,
  String actor,              // Servicio o usuario que realizó el cambio
  String observacion,
  String ciudadanoPhone,
  Instant timestamp

) {
}
