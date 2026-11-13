package pe.ligacopaperu.seguridad;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.google.cloud.firestore.Firestore;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/clubes", "/equipos", "/jugadores", "/cuerpo-tecnico", "/partidos", "/tabla",
                                "/noticias", "/noticias/**", "/estadisticas", "/politica-privacidad", "/terminos-uso",
                                "/login", "/css/**", "/js/**", "/images/**", "/media/**", "/actuator/health", "/error",
                                "/robots.txt", "/sitemap.xml", "/google9a360452cd0d0137.html", "/google*.html")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().denyAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @SuppressWarnings("null")
    ApplicationRunner bootstrapAdmin(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            Firestore firestore,
            @Value("${app.bootstrap-admin.username:admin}") String username,
            @Value("${app.bootstrap-admin.password:AdminCopaPeru2026!}") String password) {
        return args -> {
            if (username.isBlank() && password.isBlank()) {
                LOGGER.warn("No se creó un administrador inicial. Configure ADMIN_USERNAME y ADMIN_PASSWORD.");
                return;
            }
            if (username.isBlank() || password.isBlank()) {
                throw new IllegalStateException("ADMIN_USERNAME y ADMIN_PASSWORD deben configurarse juntos.");
            }
            if (password.length() < 12) {
                throw new IllegalStateException("ADMIN_PASSWORD debe tener al menos 12 caracteres.");
            }
            String normalizedUsername = username.trim();
            if (normalizedUsername.isEmpty()) {
                throw new IllegalStateException("ADMIN_USERNAME no puede estar vacío.");
            }
            UserAccount admin = users.findByUsernameIgnoreCase(normalizedUsername).orElseGet(() -> {
                UserAccount account = new UserAccount();
                account.setUsername(normalizedUsername);
                account.setRole("ADMIN");
                return account;
            });
            boolean isNew = admin.getId() == null;
            if (isNew || !passwordEncoder.matches(password, admin.getPassword())) {
                admin.setPasswordHash(passwordEncoder.encode(password));
                admin = users.save(admin);
                LOGGER.info(isNew
                        ? "Se creó la cuenta administrativa inicial configurada por el entorno."
                        : "Se actualizó la contraseña de la cuenta administrativa configurada por el entorno.");
            }
            try {
                Map<String, Object> doc = new LinkedHashMap<>();
                doc.put("id", admin.getId());
                doc.put("username", admin.getUsername());
                doc.put("passwordHash", admin.getPassword());
                doc.put("role", admin.getRole());
                doc.put("enabled", admin.isEnabled());
                doc.put("createdAt", admin.getCreatedAt() != null ? admin.getCreatedAt().toString() : null);
                firestore.collection("app_users").document(java.util.Objects.requireNonNull(String.valueOf(admin.getId()))).set(doc);
            } catch (Exception exception) {
                LOGGER.warn("No se pudo sincronizar la cuenta administrativa en Firestore.", exception);
            }
        };
    }
}
