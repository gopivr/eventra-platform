package app.eventra.platform.engagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "app.eventra.platform")
public class EngagementServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(EngagementServiceApplication.class, args);
  }
}
