package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
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
        final RegAuthLegacyContract response = mock(RegAuthLegacyContract.class);
        final HashMap params = new HashMap();
        
        when(uc.initializeCA(any(HashMap.class), any(ResponseITSKCASoap.class)))
                .thenReturn(response);
        
        ResponseITSKCASoap respose = sample.createUser("ciukstar@yahoo.com", "ciukstar", "Sergiu Starciuc", params);

        assertTrue(respose.isEmpty());
        
    }
    
}
