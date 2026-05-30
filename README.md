# reparaya-shared

Librería compartida entre los microservicios de ReparaYa.
Publicada en **GitHub Packages** — `mazkte/reparaya-shared`.

## Contenido

```
src/main/java/pe/edu/reparaya/shared/
├── events/
│   ├── ReporteCreatedEvent.java        ← topic: report.created
│   ├── ReporteAssignedEvent.java       ← topic: report.assigned
│   ├── ReporteStatusChangedEvent.java  ← topic: report.status.changed
│   └── NotificacionSendEvent.java      ← topic: notification.send
├── exception/
│   ├── ReparaYaException.java          ← excepción base + tipos de dominio
│   ├── ErrorResponse.java              ← DTO de error estandarizado
│   └── GlobalExceptionHandler.java     ← @RestControllerAdvice global
├── security/
│   ├── SecurityConfig.java             ← OAuth2 Resource Server + CORS
│   └── JwtClaimsExtractor.java         ← extrae roles/claims del JWT Keycloak
├── config/
│   └── KafkaConfig.java                ← producer/consumer con SASL/SSL Upstash
└── util/
    └── PageResponse.java               ← respuesta paginada genérica
```

---

## Publicar una nueva versión

```bash
# Asegúrate de tener configurado el settings.xml (ver abajo)
mvn deploy
```

O simplemente haz push a `main` — el workflow de GitHub Actions publica automáticamente.

---

## Consumir desde un microservicio

### 1. Configurar `settings.xml` de Maven

Copia `settings.xml.example` a `~/.m2/settings.xml` y completa tus datos:

```xml
<server>
    <id>github</id>
    <username>mazkte</username>
    <password>ghp_TU_TOKEN_AQUI</password>  <!-- Personal Access Token con read:packages -->
</server>
```

Crea el token en: https://github.com/settings/tokens
Permisos necesarios: `read:packages`

### 2. Agregar dependencia en el `pom.xml` del microservicio

```xml
<!-- Repositorio de GitHub Packages -->
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/mazkte/reparaya-shared</url>
    </repository>
</repositories>

<!-- Dependencia -->
<dependencies>
    <dependency>
        <groupId>pe.edu.reparaya</groupId>
        <artifactId>reparaya-shared</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### 3. Importar en Spring Boot

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "pe.edu.reparaya.company",   // paquete del microservicio
    "pe.edu.reparaya.shared"     // para cargar SecurityConfig, GlobalExceptionHandler
})
public class CompanyServiceApplication { ... }
```

---

## Publicación automática con GitHub Actions

El workflow `.github/workflows/publish.yml` publica automáticamente cuando:
- Se hace push a `main`
- Se crea un tag `v*` (ej: `v1.0.1`)
- Se dispara manualmente desde GitHub Actions

---

## Versiones

| Versión | Cambios |
|---|---|
| 1.0.0 | Release inicial — eventos, excepciones, seguridad, Kafka |
