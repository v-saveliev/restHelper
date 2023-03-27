package general.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import general.dto.Authorization;
import general.dto.AuthorizationResponse;
import general.dto.GenerateTicketRequest;
import general.utils.ConfigLoader;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Data
public class ExiteService {
    private String token;
    @Value("${authorization.host}")
    private String host;
    private String requestToken = "/Api/V1/Edo/Index/Authorize";
    private String requestTicketGenerate = "/Api/V1/Edo/Ticket/Generate";
    Gson gson = new Gson();
    ConfigLoader configLoader;

    @Autowired
    public ExiteService(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        host = configLoader.getProperty("host");
    }

    public String getApiToken() throws Exception {
        Authorization authorization = new Authorization();
        authorization.setVarLogin(configLoader.getProperty("user"));
        authorization.setVarPassword(configLoader.getProperty("password"));
        String bodyJson = gson.toJson(authorization);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + requestToken))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());


        ObjectMapper mapper = new ObjectMapper();
        AuthorizationResponse responseDto = mapper.readValue(response.body(), AuthorizationResponse.class);

        if (response.statusCode() == 200)
            token = responseDto.getVarToken();

        return responseDto.getVarToken();
    }

    public String generateTicket(GenerateTicketRequest ticketRequest) throws Exception {
        ticketRequest.setVarToken(token);
        String bodyJson = gson.toJson(ticketRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + requestTicketGenerate))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());

        JsonObject responseBody = (JsonObject) JsonParser.parseString(response.body());
        if (response.statusCode() == 200){
            return responseBody.get("content").getAsString();
        } else if (responseBody.get("varMessage") != null) {
            return responseBody.get("varMessage").getAsString();
        }

        return "";
    }
}
