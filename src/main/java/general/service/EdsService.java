package general.service;

import com.google.gson.*;
import general.dto.GetTicketRequest;
import general.utils.ConfigLoader;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Data
public class EdsService {
    private String host;
    private String requestTicketRoute = "/getTicket";
    Gson gson = new Gson();
    ConfigLoader configLoader;

    @Autowired
    public EdsService(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        host = configLoader.getProperty("edsHost");
    }

    public HttpResponse<String> getTicket(String bodyJson) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + requestTicketRoute))
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    public JsonArray getTicketBodies(GetTicketRequest getTicketRequest) throws Exception {

        String bodyJson = gson.toJson(getTicketRequest);
        HttpResponse<String> response = getTicket(bodyJson);
        if (response.statusCode() != 200)
            return new JsonArray();

        JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
        return responseBody.getAsJsonArray("bodys");
    }

    public Map<String, byte[]> getTicketBodiesAsByteArrays(GetTicketRequest getTicketRequest) throws Exception {

        Map<String, byte[]> result = new HashMap<>();

        JsonArray bodies = this.getTicketBodies(getTicketRequest);
        for (JsonElement body : bodies) {

            byte[] content = null;
            ZipEntry zipEntry = null;
            int count = 0;

            JsonObject bodyObject = body.getAsJsonObject();
            int bodyType = bodyObject.get("type").getAsInt();

            if (bodyType == -1 || bodyType >= 0) {
                count++;
                content = Base64.getDecoder().decode(bodyObject.get("base64content").getAsString().replaceAll("\r\n", ""));
                String prefix = getTicketRequest.getTransactionType() + "_";
                String ext = bodyType == -1 ? ".xml" : ".bin";
                result.put(prefix + getTicketRequest.getDocUUID() + ext, content);
            }
        }

        return result;
    }

    public boolean getTicketPackageForMotp(GetTicketRequest getTicketRequest) throws Exception {
        FileOutputStream fos = new FileOutputStream("eds_service_content.zip");
        ZipOutputStream zip = new ZipOutputStream(fos);
        String bodyJson = null;
        String[] transTypes = {"01", "11"};

        for (String type : transTypes) {
            getTicketRequest.setTransactionType(type);
            bodyJson = gson.toJson(getTicketRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(host + requestTicketRoute))
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());

            byte[] content = null;
            ZipEntry zipEntry = null;
            int count = 0;
            if (response.statusCode() == 200) {
                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray bodys = responseBody.getAsJsonArray("bodys");
                for (JsonElement body : bodys) {
                    JsonObject bodyObject = body.getAsJsonObject();
                    int bodyType = bodyObject.get("type").getAsInt();
                    if (bodyType == -1 || bodyType >= 0) {
                        count++;
                        content = Base64.getDecoder().decode(bodyObject.get("base64content").getAsString().replaceAll("\r\n", ""));
                        String prefix = type.equals("01") ? "PR" : "POK";
                        String ext = bodyType == -1 ? ".xml" : ".bin";
                        zipEntry = new ZipEntry(prefix + getTicketRequest.getDocUUID() + ext);
                        zip.putNextEntry(zipEntry);
                        zip.write(content);
                    }

                }

            }
        }
        zip.close();
        fos.close();

        return true;
    }
}
