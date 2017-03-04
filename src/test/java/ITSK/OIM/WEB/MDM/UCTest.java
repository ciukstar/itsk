package ITSK.OIM.WEB.MDM;

import ITSK.OIM.WEB.GEN.RegAuthLegacyService;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author sergiu
 */
@RunWith(MockitoJUnitRunner.class)
public class UCTest {
    
    @Before
    public void setUp() {
    }

    @Mock
    private RegAuthLegacyService service;
    
    @InjectMocks
    private UC uc;
    
    @Test
    public void testSomeMethod() {
        Pair<? extends Throwable, RegAuthLegacyContract> result = uc.initializeCA(new HashMap<String, Object>());
    }
    
}
