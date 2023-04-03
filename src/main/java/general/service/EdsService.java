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
        updateEdsHost();

    }

    public void updateEdsHost() {
        host = configLoader.getProperty("edsHost");
    }

    public HttpResponse<String> getTicket(GetTicketRequest getTicketRequest) throws Exception {
        if (getTicketRequest.getTransactionType() == null || getTicketRequest.getTransactionType().equals(""))
            throw new Exception("empty transaction type.");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(host + requestTicketRoute))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(getTicketRequest)))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    public JsonArray getTicketBodies(GetTicketRequest getTicketRequest) throws Exception {

        HttpResponse<String> response = getTicket(getTicketRequest);
        if (response.statusCode() != 200)
            throw new Exception("request execution error. Status code: " + response.statusCode()
                    + "\n body: " + response.body());

        JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray bodies = responseBody.getAsJsonArray("bodys");
        if (bodies.size() == 0)
            throw new Exception("empty \"bodys\" Json element. Full response body: " + responseBody);

        return bodies;
    }

    public Map<String, byte[]> getTicketBodiesAsByteArrays(GetTicketRequest getTicketRequest) throws Exception {

        Map<String, byte[]> result = new HashMap<>();

        JsonArray bodies = this.getTicketBodies(getTicketRequest);
        for (JsonElement body : bodies) {

            byte[] content;

            JsonObject bodyObject = body.getAsJsonObject();
            int bodyType = bodyObject.get("type").getAsInt();

            if (bodyType == -1 || bodyType >= 0) {
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
