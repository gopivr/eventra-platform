package app.eventra.platform.catalog.config;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  public SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**")
                    .permitAll()
                    .requestMatchers(GET, "/v1/events", "/v1/events/*")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {}))
        .build();
  }
}
