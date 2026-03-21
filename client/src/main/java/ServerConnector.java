import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ServerConnector {
    private static final int TIMEOUT_MILLIS = 5000;

    private final HttpClient httpClient = HttpClient.newHttpClient();

//    public static void main(String[] args) throws IOException, InterruptedException {
//        if(args.length == 3) {
//            String host = args[0];
//            String portString = args[1];
//            String path = args[2];
//
//            var client = new ServerConnector();
//
//            try {
//                client.doGet(host, Integer.parseInt(portString), path);
//            } catch (URISyntaxException | NumberFormatException e) {
//                // Print usage if port is not an int or path is invalid
//                printUsage();
//            }
//        } else {
//            printUsage();
//        }
//    }

    private static void printUsage() {
        System.err.println("USAGE: java GetExample <host> <port> <path>");
    }

    public HttpResponse<String> doGet(String host, int port, String urlPath, String authToken) throws URISyntaxException, IOException, InterruptedException {
        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            HttpHeaders headers = httpResponse.headers();
            Optional<String> lengthHeader = headers.firstValue("Content-Length");

            System.out.printf("Received %s bytes%n", lengthHeader.orElse("unknown"));
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }
        System.out.println(httpResponse.body());
        return httpResponse;
    }

    public HttpResponse<String> doPost(String host, int port, String urlPath, String message, String authToken) throws URISyntaxException, IOException, InterruptedException {
        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            HttpHeaders headers = httpResponse.headers();
            Optional<String> lengthHeader = headers.firstValue("Content-Length");

            System.out.printf("Received %s bytes%n", lengthHeader.orElse("unknown"));
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }
        System.out.println(httpResponse.body());
        return httpResponse;
    }

    public HttpResponse<String> doPut(String host, int port, String urlPath, String message, String authToken) throws URISyntaxException, IOException, InterruptedException {
        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            HttpHeaders headers = httpResponse.headers();
            Optional<String> lengthHeader = headers.firstValue("Content-Length");

            System.out.printf("Received %s bytes%n", lengthHeader.orElse("unknown"));
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }
        System.out.println(httpResponse.body());
        return httpResponse;
    }

    public HttpResponse<String> doDelete(String host, int port, String urlPath) throws URISyntaxException, IOException, InterruptedException {
        String urlString = String.format("http://%s:%d%s", host, port, urlPath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_MILLIS))
                .DELETE()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            HttpHeaders headers = httpResponse.headers();
            Optional<String> lengthHeader = headers.firstValue("Content-Length");

            System.out.printf("Received %s bytes%n", lengthHeader.orElse("unknown"));
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }
        System.out.println(httpResponse.body());
        return httpResponse;
    }
}
