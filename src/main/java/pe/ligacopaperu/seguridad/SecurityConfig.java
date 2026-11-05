package pe.ligacopaperu.seguridad;

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

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/clubes", "/equipos", "/jugadores", "/cuerpo-tecnico", "/partidos", "/tabla",
                                "/noticias", "/noticias/**", "/estadisticas",
                                "/login", "/css/**", "/js/**", "/images/**", "/media/**", "/actuator/health", "/error",
                                "/robots.txt", "/sitemap.xml")
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
    ApplicationRunner bootstrapAdmin(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.password:}") String password) {
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
            if (users.findByUsernameIgnoreCase(normalizedUsername).isEmpty()) {
                UserAccount admin = new UserAccount();
                admin.setUsername(normalizedUsername);
                admin.setPasswordHash(passwordEncoder.encode(password));
                admin.setRole("ADMIN");
                users.save(admin);
                LOGGER.info("Se creó la cuenta administrativa inicial configurada por el entorno.");
            }
        };
    }
}
