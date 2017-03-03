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
    private AppLogger logger;
    @Mock
    private XMLParser parser;
    
    @InjectMocks
    private ITSKCASoap sample;

    @Test
    public void shouldReturnAnEmptyResponse() throws Exception {
        final RegAuthLegacyContract port = mock(RegAuthLegacyContract.class);
        final HashMap params = new HashMap();
        final ResponseITSKCASoap response = new ResponseITSKCASoap();
        final String email = "ciukstar@yahoo.com";
        final String CAOIDemail = "1.2.840.113549.1.9.1";
        
        when(uc.initializeCA(params, response)).thenReturn(port);
        
        ResponseITSKCASoap result = sample.createUser(email, "ciukstar", "Sergiu Starciuc", params);

        final String expectedLog = "Error: Not Found CA Users, filter- " + CAOIDemail + "->" + email;
        
        assertTrue(result.isEmpty());
        assertThat(result.getLog(), is(expectedLog));
        
    }
    
}
