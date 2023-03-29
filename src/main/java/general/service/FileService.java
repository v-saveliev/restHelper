package general.service;

import general.utils.ConfigLoader;
import general.utils.XmlUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        while (outputFile.exists()) {
            count++;
            outputFile = new File(fileName + count);
        }
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(Base64.getDecoder().decode(base64));
        outputStream.close();

        FileInputStream inputStream = new FileInputStream(outputFile.getAbsolutePath());
        try {
            String fileNameX = XmlUtil.find("//Файл/@ИдФайл", new ByteArrayInputStream(inputStream.readAllBytes()));
            File renamedFile = new File(outputFile.getParent() + File.separator + fileNameX + ".xml");
            outputFile = outputFile.renameTo(renamedFile) ? renamedFile : outputFile;
        } catch (Exception e) { } finally {
            inputStream.close();
        }

        return outputFile.getAbsolutePath();
    }

    public void signContent(String contentPath) throws Exception {
        String javaAppPath = configLoader.getProperty("signerJar");
        Process process = Runtime.getRuntime().exec("java -jar " + javaAppPath + " " + contentPath);
        process.waitFor();
    }

    public void updateConfig(String json) throws Exception {
        File outputFile = new File(configLoader.getPathConfig());
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        configLoader.readConfig();
    }
}
