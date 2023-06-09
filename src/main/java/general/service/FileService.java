package general.service;

import general.utils.ConfigLoader;
import general.utils.XmlUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        } catch (Exception e) {
        } finally {
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

    public String saveZipFromMapContent(List<Map<String, byte[]>> listOfMapContent, String zipName) throws Exception {

        int fileCount = 0;
        String fileName = configLoader.getProperty("workDir") + File.separator + zipName;
        String ext = ".zip";
        File outputFile = new File(fileName + ext);
        while (outputFile.exists()) {
            fileCount++;
            outputFile = new File(fileName + "_" + fileCount + ext);
        }

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (Map<String, byte[]> contentMap : listOfMapContent) {
            for (String contentName : contentMap.keySet()) {

                ZipEntry zipEntry = null;
                byte[] content = contentMap.get(contentName);
                zipEntry = new ZipEntry(contentName);
                zip.putNextEntry(zipEntry);
                zip.write(content);
            }
        }

        zip.close();
        outputStream.close();

        return outputFile.getAbsolutePath();
    }
}
