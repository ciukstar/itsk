package ITSK.OIM.WEB.MDM;

import static java.util.Arrays.asList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.mockito.Matchers;
import static org.mockito.Matchers.isA;
import org.xml.sax.SAXException;

/**
 *
 * @author sergiu
 */
public class XMLParserTest {

    @Before
    public void setUp() {
        userId = Pair.of("UserId", "100001");
        userStatus = Pair.of("Status", "A");
        filter = asList(userId.getLeft(), userStatus.getLeft());
        parser = new XMLParser();
    }
    
    private String prolog = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>";
    private Pair<String, String> userId;
    private Pair<String, String> userStatus;
    private List<String> filter;
    
    private XMLParser parser;
    

    @Ignore
    @Test
    public void bar() {

        String xml = String.format("%s%n<rs:data><z:row %s=\"%s\"></z:row></rs:data>",
                prolog,
                userId.getLeft(), userId.getRight());

        Either<? extends Throwable, List<List<String>>> res
                = parser.parseXML(xml, asList(userId.getLeft(), userStatus.getLeft()));

        assertFalse(res.isEmpty());
        assertFalse(res.getRight().isEmpty());
        assertEquals(userId.getRight(), res.getRight().get(0).get(0));
    }

    
    @Test
    public void baz() {

        String xml = String.format("%s%n<rs:data xmlns:rs=\"http://some.com/rs\"><z:row xmlns:z=\"http://other.com/z\" %s=\"%s\" %s=\"%s\"></z:row></rs:data>",
                prolog,
                userId.getLeft(), userId.getRight(),
                userStatus.getLeft(), userStatus.getRight());

        Either<? extends Throwable, List<User>> res
                = parser.extractUsers(xml, asList(userId.getLeft(), userStatus.getLeft()));

        System.out.println(res.getLeft());
        
        assertFalse(res.isEmpty());
        assertFalse(res.getRight().isEmpty());
        assertEquals(userId.getRight(), res.getRight().get(0).getId());
        assertEquals(userStatus.getRight(), res.getRight().get(0).getStatus().toString());
    }

    @Test
    public void foo() {

        String xml = String.format("%s%n<rs:data><z:row %s=\"%s\" %s=\"%s\"></z:row></rs:data>",
                prolog,
                userId.getLeft(), userId.getRight(),
                userStatus.getLeft(), userStatus.getRight());

        Either<? extends Throwable, List<List<String>>> res
                = parser.parseXML(xml, asList(userId.getLeft(), userStatus.getLeft()));

        assertFalse(res.isEmpty());
        assertFalse(res.getRight().isEmpty());
        assertEquals(userId.getRight(), res.getRight().get(0).get(0));
        assertEquals(userStatus.getRight(), res.getRight().get(0).get(1));
    }

    @Ignore
    @Test
    public void shouldReturnAnEmptyResult() {

        String xml = String.format("%s%n%s", prolog, "<rs:data><z:row></z:row></rs:data>");

        Either<? extends Throwable, List<List<String>>> res
                = parser.parseXML(xml, filter);

        assertEquals(res.getLeft(), isA(AssertionError.class));
        assertFalse(res.isEmpty());
        assertTrue(res.getRight().isEmpty());
    }

    @Ignore
    @Test
    public void shouldReturnAThrowableIfExceptionWhenParsing() {

        String xml = String.format("%s%n%s", prolog, "<rs:data>");

        Either<? extends Throwable, List<List<String>>> res = parser.parseXML(xml, filter);

        assertTrue(res.isEmpty());
        assertNotNull(res.getLeft());
    }

}
