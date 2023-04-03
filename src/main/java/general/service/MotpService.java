package general.service;

import com.google.gson.Gson;
import general.utils.ConfigLoader;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Data
public class MotpService {
    private String host;
    private String requestContainers = "/api/v1/motp/containers?type=upd";
    private String requestTicket = "/api/v1/motp/tickets?id=";
    Gson gson = new Gson();
    ConfigLoader configLoader;

    @Autowired
    public MotpService(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        updateMotpHost();
    }

    public void updateMotpHost() {
        host = configLoader.getProperty("motpHost");
    }

    public String sendCRPT(byte[] content) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + requestContainers))
                .setHeader("Content-Type","application/zip")
                .POST(HttpRequest.BodyPublishers.ofByteArray(content))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public String getTicket(String ticketId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + requestTicket + ticketId))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}