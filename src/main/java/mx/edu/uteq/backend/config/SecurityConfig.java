package mx.edu.uteq.backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests((authHttp) -> authHttp
            .requestMatchers(HttpMethod.GET, "/api/profesor/grupo/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/profesor/usuario/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/profesor/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/profesor").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/profesor/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/profesor/**").hasRole("ADMIN")
            .anyRequest().authenticated())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(resourceServer -> 
                resourceServer.jwt(jwt -> 
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    // Bean para la configuración de CORS
 @Bean
 CorsConfigurationSource corsConfigurationSource() {
 CorsConfiguration configuration = new CorsConfiguration();
 configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
 configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
 configuration.setAllowedHeaders(Arrays.asList("*"));
 configuration.setAllowCredentials(true);
 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
 source.registerCorsConfiguration("/**", configuration);
return source;
 }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}