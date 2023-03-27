package general.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Component
@Data
public class ConfigLoader {
    @Value("${config.path}")
    private String pathConfig;
    private JsonObject jsonConfig;

    @PostConstruct
    private void init() throws FileNotFoundException {
        jsonConfig = JsonParser.parseReader(new FileReader(pathConfig)).getAsJsonObject();
    }

    public String getProperty(String name) {
        return jsonConfig.get(name) != null ? jsonConfig.get(name).getAsString() : "";
    }
}
