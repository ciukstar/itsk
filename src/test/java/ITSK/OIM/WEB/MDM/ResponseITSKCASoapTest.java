package ITSK.OIM.WEB.MDM;

import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author sergiu
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseITSKCASoapTest {

    @Before
    public void setUp() {
    }

    @InjectMocks
    private ResponseITSKCASoap response;

    final String msg = "Some message";
    final String otherMsg = "Other message";
    
    @Test
    public void shouldAppendMultipleLogs() {

        response.appendLog(msg);
        response.appendLog(otherMsg);

        assertThat(response.getLog(), is(msg + otherMsg));
    }

    @Test
    public void shouldAppendFirstLog() {

        response.appendLog(msg);

        assertThat(response.getLog(), is(msg));
    }

}
