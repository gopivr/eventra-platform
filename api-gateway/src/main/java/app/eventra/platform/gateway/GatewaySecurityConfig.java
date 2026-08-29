package app.eventra.platform.gateway;

import java.security.Principal;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class GatewaySecurityConfig {
  @Bean
  SecurityWebFilterChain security(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            auth ->
                auth.pathMatchers("/actuator/health")
                    .permitAll()
                    .pathMatchers(
                        HttpMethod.GET,
                        "/v1/events/**",
                        "/v1/venues/**",
                        "/v1/providers/**",
                        "/v1/services/**",
                        "/v1/packages/**",
                        "/v1/reviews/**")
                    .permitAll()
                    .pathMatchers(HttpMethod.POST, "/v1/pricing/quotes", "/v1/payments/webhooks/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(resource -> resource.jwt(jwt -> {}))
        .build();
  }

  @Bean
  KeyResolver authenticatedSubjectKeyResolver() {
    return exchange ->
        exchange
            .getPrincipal()
            .map(Principal::getName)
            .switchIfEmpty(Mono.just("anonymous:" + clientAddress(exchange)));
  }

  private String clientAddress(org.springframework.web.server.ServerWebExchange exchange) {
    var address = exchange.getRequest().getRemoteAddress();
    return address == null ? "unknown" : address.getAddress().getHostAddress();
  }
}
