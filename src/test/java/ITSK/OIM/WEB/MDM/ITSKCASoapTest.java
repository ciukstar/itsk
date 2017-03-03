package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
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

    @Test
    public void testSomeMethod() throws Exception {
        ITSKCASoap sample = new ITSKCASoap();
        
        ResponseITSKCASoap respose = sample.createUser("ciukstar@yahoo.com", "ciukstar", "Sergiu Starciuc", new HashMap(), mock(CredLoader.class));
    }
    
}
