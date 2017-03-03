package ITSK.OIM.WEB.MDM;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author sergiu
 */
public class XMLParser {
    private final AppLogger logger;

    public XMLParser(AppLogger logger) {
        this.logger = logger;
    }

    public List<List<String>> parseXML(String XMLString, List<String> Element, ResponseITSKCASoap response) throws Exception {
        List<String> resultStr = new ArrayList<>();
        List<List<String>> result = new ArrayList<>();
        List<String> listStrElems = new ArrayList<>();
        InputSource streamXML = new InputSource(new StringReader(XMLString));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String strElems = "";
        try {
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
            return result;
        } catch (Exception e) {
            String ss = ITSKCASoap.getStackTrace(e);
            logger.setErrorLog(ss, response, this.getClass());
            //LOGGER.log(Level.SEVERE, "Error parser result find user CA:", e);
            return result;
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));
        }
    }
    
}
