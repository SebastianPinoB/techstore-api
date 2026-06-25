package api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (Crucial para que Postman pueda hacer POST/PUT/DELETE)
            .csrf(csrf -> csrf.disable())
            
            // 2. Definir las reglas de los endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/productos/**").permitAll() // Deja pasar libre para probar SQS
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            );
            
            // 3. Si necesitas que el filtro JWT procese el token (sin bloquear los permitAll):
            // http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}