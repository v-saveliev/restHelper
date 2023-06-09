package general.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;

@Component
@Data
public class ConfigLoader {
    @Value("${config.path}")
    private String pathConfig;
    private JsonObject jsonConfig;

    @PostConstruct
    private void init() throws Exception {
        readConfig();
    }

    public String getProperty(String name) {
        return jsonConfig.get(name) != null ? jsonConfig.get(name).getAsString() : "";
    }

    public String getConfigAsString() {
        String json = jsonConfig.toString();
        json = json.replaceAll("\\{", "{\n");
        json = json.replaceAll("\",", "\",\n");
        json = json.replaceAll("\"}", "\"\n}");
        return json;
    }

    public void readConfig() throws Exception {
        jsonConfig = JsonParser.parseReader(new FileReader(pathConfig)).getAsJsonObject();
    }

    public boolean isWorkDirExists() {
        File workDir = new File(getProperty("workDir"));
        return workDir.exists() && workDir.isDirectory();
    }

}
