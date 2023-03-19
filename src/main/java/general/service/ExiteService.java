package general.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import general.dto.Authorization;
import general.dto.AuthorizationResponse;
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
    private String tokenRequest = "/Api/V1/Edo/Index/Authorize";
    Gson gson = new Gson();
    @Autowired
    Authorization authorization;

    public String getApiToken() throws Exception {


        String bodyJson = gson.toJson(authorization);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + tokenRequest))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString());


        ObjectMapper mapper = new ObjectMapper();
        AuthorizationResponse responseDto = mapper.readValue(response.body(), AuthorizationResponse.class);

        return responseDto.getVarToken();
    }
}
