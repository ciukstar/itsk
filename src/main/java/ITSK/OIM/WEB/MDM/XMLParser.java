package ITSK.OIM.WEB.MDM;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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

    public Either<? extends Throwable, List<User>> extractUsers(final String xml, List<String> filter) {

        try {
            XPath xp = XPathFactory.newInstance().newXPath();
            String userId = xp.compile("//*[local-name()='data']/*[local-name()='row']/@UserId").evaluate(new InputSource(new StringReader(xml)));
            String userStatus = xp.compile("//*[local-name()='data']/*[local-name()='row']/@Status").evaluate(new InputSource(new StringReader(xml)));
            System.out.format("(%s,%s)", userId, userStatus);
            
            return Either.right(asList(new User(userId, User.Status.valueOf(userStatus))));
        } catch (XPathExpressionException ex) {
            return Either.left(ex);
        }
    }

    public Either<? extends Throwable, List<List<String>>> parseXML(final String xml, List<String> filter) {
        try {

            List<List<String>> result = new ArrayList<>();
            List<String> listStrElems = new ArrayList<>();
            InputSource streamXML = new InputSource(new StringReader(xml));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(streamXML);
            NodeList nodes = document.getElementsByTagName("rs:data");
            Element element1 = (Element) nodes.item(0);
            NodeList movieList = element1.getElementsByTagName("z:row");
            Element element2 = null;

            String strElems = "";
            for (int i = 0; i < movieList.getLength(); i++) {
                element2 = (Element) movieList.item(i);
                for (int j = 0; j < filter.size(); j++) {
                    if (j + 1 != filter.size()) {
                        strElems = strElems + element2.getAttributes().getNamedItem(filter.get(j)).getNodeValue() + "||";
                    } else {
                        strElems = strElems + element2.getAttributes().getNamedItem(filter.get(j)).getNodeValue();
                    }
                }
                listStrElems.add(strElems);
                strElems = "";
            }

            List<String> resultStr = new ArrayList<>();
            for (int i = 0; i < listStrElems.size(); i++) {
                resultStr = Arrays.asList(listStrElems.get(i).split("\\|\\|"));
                result.add(i, resultStr);
            }
            return Either.right(result);
        } catch (IOException | DOMException | SAXException | ParserConfigurationException e) {
            return Either.left(e);
        }
    }

}
