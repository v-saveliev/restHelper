package general.service;

import general.utils.ConfigLoader;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@Component
@Data
public class FileService {

    @Autowired
    private ConfigLoader configLoader;
    private int count = 1;
    public String saveFileFromBase64(String base64) throws Exception {
        String fileName = configLoader.getProperty("workDir") + File.separator + "restContent";
        File outputFile = new File(fileName + count);
        while(outputFile.exists()) {
            count++;
            outputFile = new File(fileName + count);
        }

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(Base64.getDecoder().decode(base64));
        outputStream.close();

        return outputFile.getAbsolutePath();
    }
}
