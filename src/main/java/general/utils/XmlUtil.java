package general.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class XmlUtil {

    public static String find(String expression, InputStream inputStream) throws Exception {

        byte[] bytes = StreamUtils.copyToByteArray(inputStream);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument;
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            xmlDocument = builder.parse(is);
        } catch (SAXParseException e) {
            String res = new String(bytes, StandardCharsets.UTF_8);
            InputStream is = new ByteArrayInputStream(res.getBytes(Charset.forName("windows-1251")));
            xmlDocument = builder.parse(is);
        }
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (String) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.STRING);
    }

}
