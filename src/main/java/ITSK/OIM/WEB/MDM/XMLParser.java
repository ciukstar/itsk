package ITSK.OIM.WEB.MDM;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author sergiu
 */
public class XMLParser {

    private final LogFormater logger;

    public XMLParser(LogFormater logger) {
        this.logger = logger;
    }

    public Pair<? extends Throwable, List<List<String>>> parseXML(String XMLString, List<String> Element) {
        try {
            List<String> resultStr = new ArrayList<>();
            List<List<String>> result = new ArrayList<>();
            List<String> listStrElems = new ArrayList<>();
            InputSource streamXML = new InputSource(new StringReader(XMLString));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String strElems = "";
            Document document = builder.parse(streamXML);
            NodeList nodes = document.getElementsByTagName("rs:data");
            Element element1 = (Element) nodes.item(0);
            NodeList movieList = element1.getElementsByTagName("z:row");
            Element element2 = null;
            for (int i = 0; i < movieList.getLength(); i++) {
                element2 = (Element) movieList.item(i);
                for (int j = 0; j < Element.size(); j++) {
                    if (j + 1 != Element.size()) {
                        strElems = strElems + element2.getAttributes().getNamedItem(Element.get(j)).getNodeValue().toString() + "||";
                    } else {
                        strElems = strElems + element2.getAttributes().getNamedItem(Element.get(j)).getNodeValue().toString();
                    }
                }
                listStrElems.add(strElems);
                strElems = "";
            }
            for (int i = 0; i < listStrElems.size(); i++) {
                resultStr = Arrays.asList(listStrElems.get(i).split("\\|\\|"));
                result.add(i, resultStr);
            }
            return Pair.right(result);
        } catch (IOException | DOMException | SAXException | ParserConfigurationException e) {
            return Pair.left(e);
        }
    }

}
