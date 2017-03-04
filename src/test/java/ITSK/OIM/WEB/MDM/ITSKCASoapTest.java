package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author sergiu
 */
@RunWith(MockitoJUnitRunner.class)
public class ITSKCASoapTest {

    @Before
    public void setUp() {
    }

    @Mock
    private CredLoader credLoader;
    @Mock
    private UC uc;
    @Mock
    private LogFormater formatter;
    @Mock
    private XMLParser parser;

    @InjectMocks
    private ITSKCASoap sample;

    final RegAuthLegacyContract thePort = mock(RegAuthLegacyContract.class);
    final Either<? extends Throwable, RegAuthLegacyContract> port = Either.right(thePort);
    final HashMap params = new HashMap();
    final ResponseITSKCASoap response = new ResponseITSKCASoap();
    final String email = "ciukstar@yahoo.com";
    final String folderID = "c5619331-7426-e611-80ed-00505681c485";
    final String CAOIDemail = "1.2.840.113549.1.9.1";

    @Test
    public void shouldGenerateAnEmptyResposeAndLogWhyTheUserWasNotFound() throws Exception {
        final HashMap<String, Object> userInfo = new HashMap<String, Object>() {
            {
                put("resultCount", 1);
            }
        };

        doReturn(port)
                .when(uc).initializeCA(params);
        doReturn(Either.right(userInfo))
                .when(uc).findUcUser(params, folderID, port.getRight(), CAOIDemail, email);

        ResponseITSKCASoap result = sample.createUser(email, "ciukstar", "Sergiu Starciuc", params);

        assertTrue(result.isEmpty());
        assertFalse(result.getLog().isEmpty());
    }

    @Test
    public void shouldReturnAnEmptyWhenUcUserNotFound() throws Exception {

        doReturn(port)
                .when(uc).initializeCA(params);
        doReturn(Either.left(new RuntimeException("Error")))
                .when(uc).findUcUser(params, folderID, thePort, CAOIDemail, email);

        ResponseITSKCASoap result = sample.createUser(email, "ciukstar", "Sergiu Starciuc", params);

        assertTrue(result.isEmpty());
        assertFalse(result.getLog().isEmpty());

    }

}
