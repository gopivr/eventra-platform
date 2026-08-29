package app.eventra.platform.gateway;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
  private static final String HEADER = "X-Correlation-ID";

  public Mono<Void> filter(
      org.springframework.web.server.ServerWebExchange exchange, GatewayFilterChain chain) {
    String supplied = exchange.getRequest().getHeaders().getFirst(HEADER);
    String correlationId =
        supplied == null || supplied.isBlank() ? UUID.randomUUID().toString() : supplied;
    var request =
        exchange
            .getRequest()
            .mutate()
            .headers(headers -> headers.set(HEADER, correlationId))
            .build();
    exchange.getResponse().getHeaders().set(HEADER, correlationId);
    return chain.filter(exchange.mutate().request(request).build());
  }

  public int getOrder() {
    return -100;
  }
}
