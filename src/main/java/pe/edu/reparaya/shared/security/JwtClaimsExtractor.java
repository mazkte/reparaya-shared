package pe.edu.reparaya.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Extrae información del JWT de Keycloak desde el SecurityContext.
 * Centraliza la lógica de lectura de claims para todos los microservicios.
 */
@Component
public class JwtClaimsExtractor {

    /**
     * Retorna el subject (keycloakId) del usuario autenticado.
     */
    public Optional<String> getSubject() {
        return getJwt().map(Jwt::getSubject);
    }

    /**
     * Retorna el UUID del usuario autenticado.
     */
    public Optional<UUID> getUserId() {
        return getSubject().map(UUID::fromString);
    }

    /**
     * Retorna el email del usuario autenticado.
     */
    public Optional<String> getEmail() {
        return getJwt().map(jwt -> jwt.getClaimAsString("email"));
    }

    /**
     * Retorna los roles del realm asignados al usuario.
     * Keycloak los expone en el claim "realm_access.roles".
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles() {
        return getJwt()
                .map(jwt -> {
                    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                    if (realmAccess == null) return List.<String>of();
                    Object roles = realmAccess.get("roles");
                    if (roles instanceof Collection<?> col) {
                        return col.stream()
                                .filter(r -> r instanceof String)
                                .map(r -> (String) r)
                                .toList();
                    }
                    return List.<String>of();
                })
                .orElse(List.of());
    }

    /**
     * Verifica si el usuario autenticado tiene un rol específico.
     */
    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    /**
     * Verifica si el usuario es ROLE_AUTORIDAD.
     */
    public boolean isAutoridad() { return hasRole("ROLE_AUTORIDAD"); }

    /**
     * Verifica si el usuario es ROLE_SUPERVISOR.
     */
    public boolean isSupervisor() { return hasRole("ROLE_SUPERVISOR"); }

    /**
     * Verifica si el usuario es ROLE_EMPRESA.
     */
    public boolean isEmpresa() { return hasRole("ROLE_EMPRESA"); }

    /**
     * Verifica si el usuario es ROLE_ADMIN.
     */
    public boolean isAdmin() { return hasRole("ROLE_ADMIN"); }

    /**
     * Retorna el claim "empresa_id" — atributo personalizado en Keycloak
     * usado para identificar a qué empresa pertenece el coordinador.
     */
    public Optional<UUID> getEmpresaId() {
        return getJwt()
                .map(jwt -> jwt.getClaimAsString("empresa_id"))
                .filter(id -> id != null && !id.isBlank())
                .map(UUID::fromString);
    }

    // ── Privado ───────────────────────────────────────────────

    private Optional<Jwt> getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return Optional.of(jwtAuth.getToken());
        }
        return Optional.empty();
    }
}
