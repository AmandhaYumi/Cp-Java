package br.com.mercadoexpress.mercadoexpress;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "management.health.mail.enabled=false")
@ActiveProfiles("test")
class TelasTests {
    @Autowired Environment environment;
    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1).build();

    private HttpResponse<String> get(String path, String credentials) throws Exception {
        URI uri = URI.create("http://localhost:" + environment.getProperty("local.server.port") + path);
        HttpRequest.Builder request = HttpRequest.newBuilder(uri).GET();
        if (credentials != null) {
            request.header("Authorization", "Basic " + Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void paginasPublicasUsamNovoNomeEEstilo() throws Exception {
        for (String path : List.of("/", "/login", "/register")) {
            HttpResponse<String> response = get(path, null);
            assertThat(response.statusCode()).as(path).isEqualTo(200);
            assertThat(response.body()).contains("Mercadinho", "/css/estilo.css")
                    .doesNotContain("Mercado Express", "/css/style.css");
        }
        HttpResponse<String> stylesheet = get("/css/estilo.css", null);
        assertThat(stylesheet.statusCode()).isEqualTo(200);
        assertThat(stylesheet.body()).contains("#fff5f8", "#a12f60", "Arial", "@media")
                .doesNotContain("fonts.googleapis.com", "linear-gradient", "box-shadow");
    }

    @Test
    void gerenteAbreTodasAsTelasRenomeadas() throws Exception {
        for (String path : List.of("/products", "/products/new", "/products/1/edit",
                "/cart", "/purchases", "/admin/dashboard")) {
            HttpResponse<String> response = get(path, "gerente@mercadoexpress.com:Gerente@123");
            assertThat(response.statusCode()).as(path).isEqualTo(200);
            assertThat(response.body()).contains("Mercadinho", "/css/estilo.css");
        }
    }

    @Test
    void clienteContinuaSemAcessoAoPainel() throws Exception {
        assertThat(get("/admin/dashboard", "cliente@mercadoexpress.com:Cliente@123").statusCode())
                .isEqualTo(403);
    }
}
