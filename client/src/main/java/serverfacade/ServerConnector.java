package serverfacade;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;

public class ServerConnector {
    private static final int TIMEOUT_MILLIS = 5000;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Logger LOGGER = Logger.getLogger(ServerConnector.class.getName());

    public HttpResponse<String> doGet(String host, int port, String urlPath, String authToken)
            throws URISyntaxException, IOException, InterruptedException {

        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleHttp(httpResponse);
    }

    public HttpResponse<String> doPost(String host, int port, String urlPath, String message, String authToken)
            throws URISyntaxException, IOException, InterruptedException {

        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleHttp(httpResponse);
    }

    public HttpResponse<String> doPut(String host, int port, String urlPath, String message, String authToken)
            throws URISyntaxException, IOException, InterruptedException {

        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleHttp(httpResponse);
    }

    public HttpResponse<String> doDelete(String host, int port, String urlPath, String authToken)
            throws URISyntaxException, IOException, InterruptedException {

        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleHttp(httpResponse);
    }

    private HttpResponse<String> handleHttp(HttpResponse<String> httpResponse) {
        if (httpResponse.statusCode() == 200) {
            HttpHeaders headers = httpResponse.headers();
            Optional<String> lengthHeader = headers.firstValue("Content-Length");

            LOGGER.fine(String.format("Received %s bytes%n", lengthHeader.orElse("unknown")));
        } else {
            LOGGER.fine(String.format("Error: received status code " + httpResponse.statusCode()));
        }
        LOGGER.fine(httpResponse.body());
        return httpResponse;
    }
}
