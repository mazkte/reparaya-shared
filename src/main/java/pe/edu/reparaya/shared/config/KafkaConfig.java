package pe.edu.reparaya.shared.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Kafka para Upstash (con SASL/SSL).
 * Productores y consumidores usan JSON como formato de serialización.
 * <p>
 * Propiedades requeridas en application.yml de cada microservicio:
 * <p>
 * spring.kafka.bootstrap-servers: ${UPSTASH_KAFKA_BOOTSTRAP}
 * spring.kafka.properties.sasl.mechanism: SCRAM-SHA-256
 * spring.kafka.properties.security.protocol: SASL_SSL
 * spring.kafka.properties.sasl.jaas.config: >
 * org.apache.kafka.common.security.scram.ScramLoginModule required
 * username="${UPSTASH_KAFKA_USER}"
 * password="${UPSTASH_KAFKA_PASS}";
 */
@Configuration
public class KafkaConfig {

 @Value("${spring.kafka.bootstrap-servers}")
 private String bootstrapServers;

 @Value("${spring.kafka.properties.sasl.mechanism:SCRAM-SHA-256}")
 private String saslMechanism;

 @Value("${spring.kafka.properties.security.protocol:SASL_SSL}")
 private String securityProtocol;

 @Value("${spring.kafka.properties.sasl.jaas.config:}")
 private String saslJaasConfig;

 // ── Topics ────────────────────────────────────────────────

 public static final String TOPIC_REPORTE_CREATED = "report.created";
 public static final String TOPIC_REPORTE_ASSIGNED = "report.assigned";
 public static final String TOPIC_REPORTE_STATUS_CHANGED = "report.status.changed";
 public static final String TOPIC_NOTIFICATION_SEND = "notification.send";

 // ── Producer ──────────────────────────────────────────────

 @Bean
 public ProducerFactory<String, Object> producerFactory() {
  Map<String, Object> props = new HashMap<>(commonProps());
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
  props.put(ProducerConfig.ACKS_CONFIG, "all");            // máxima durabilidad
  props.put(ProducerConfig.RETRIES_CONFIG, 3);
  props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);  // no incluir tipo en headers
  return new DefaultKafkaProducerFactory<>(props);
 }

 @Bean
 public KafkaTemplate<String, Object> kafkaTemplate() {
  return new KafkaTemplate<>(producerFactory());
 }

 // ── Consumer ──────────────────────────────────────────────

 @Bean
 public ConsumerFactory<String, Object> consumerFactory() {
  Map<String, Object> props = new HashMap<>(commonProps());
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // commit manual
  props.put(JsonDeserializer.TRUSTED_PACKAGES, "pe.edu.reparaya.shared.events");
  props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
  props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.Map"); // deserializa como Map genérico
  return new DefaultKafkaConsumerFactory<>(props);
 }

 @Bean
 public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
  ConcurrentKafkaListenerContainerFactory<String, Object> factory =
    new ConcurrentKafkaListenerContainerFactory<>();
  factory.setConsumerFactory(consumerFactory());
  factory.getContainerProperties()
    .setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE);
  return factory;
 }

 // ── Propiedades comunes (SASL/SSL para Upstash) ───────────

 private Map<String, Object> commonProps() {
  Map<String, Object> props = new HashMap<>();
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

  if (!saslJaasConfig.isBlank()) {
   props.put("sasl.mechanism", saslMechanism);
   props.put("security.protocol", securityProtocol);
   props.put("sasl.jaas.config", saslJaasConfig);
  }
  return props;
 }
}
