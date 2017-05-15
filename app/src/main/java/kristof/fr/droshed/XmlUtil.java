package kristof.fr.droshed;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by kristof
 * on 5/1/17.
 */

public class XmlUtil {

    public static void xmlParser() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder;
        try {
            documentBuilder = factory.newDocumentBuilder();
            final Document document = documentBuilder.parse(new File("test.xml"));
            System.out.println(document.getXmlVersion());
            System.out.println(document.getXmlEncoding());
            System.out.println(document.getXmlStandalone());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
