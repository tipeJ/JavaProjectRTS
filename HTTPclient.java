import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;


public class SimpleHttpClient {
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java httpClient <URL>");
            return;
        }

        try {
            String url = args[0];
            downloadFile(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String url) throws Exception {
        // Create HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
        
        // Parse the URL and get the filename from the URL
        URI uri = URI.create(url);
        String path = uri.getPath();
        String filename = Paths.get(path).getFileName().toString();
        if (filename.isEmpty()) {
            filename = "index.html";
        }

        // Create the HTTP GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(filename)));

        // Check for successful response (status code 200)
        if (response.statusCode() == 200) {
            System.out.println("HTTP 200 OK. File saved as " + filename);
        } else {
            System.err.println("Error: Received HTTP status code " + response.statusCode());
        }
    }
}
