package app.eventra.platform.catalog.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.eventra.platform.catalog.config.SecurityConfig;
import app.eventra.platform.catalog.repository.CatalogOutboxEventRepository;
import app.eventra.platform.catalog.repository.EventRepository;
import app.eventra.platform.catalog.service.CatalogOwnership;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
class EventSecurityIntegrationTest {
  @Autowired private MockMvc mvc;

  @MockBean private EventRepository events;
  @MockBean private CatalogOutboxEventRepository outbox;
  @MockBean private CatalogOwnership ownership;
  @MockBean private JwtDecoder decoder;

  @Test
  void discoveryIsPublic() throws Exception {
    when(events.discover()).thenReturn(List.of());
    mvc.perform(get("/v1/events")).andExpect(status().isOk());
  }

  @Test
  void eventCreationRequiresJwt() throws Exception {
    mvc.perform(post("/v1/events").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isUnauthorized());
  }
}
