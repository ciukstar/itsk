package ITSK.OIM.WEB.MDM;

import java.util.Date;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author sergiu
 */
@RunWith(MockitoJUnitRunner.class)
public class AppLoggerTest {
    
    @Before
    public void setUp() {
    }

    @InjectMocks
    private AppLogger logger;
        
    final String format = "<%s> <%s> <MDM> <LOG> %s%n";
    
    @Test
    public void shouldUppendTheLogMessageToResponseLog() {
        final String msg = "The Message";
        final ResponseITSKCASoap response = new ResponseITSKCASoap();
        final Class loggedClass = ITSKCASoap.class;
        final Date time = new Date();
        final String otherMessage = "Some other message";
        
        response.setLog(otherMessage);
        
        logger.setLog(msg, response, loggedClass, time);
        
        final String expected = otherMessage + String.format(format, time.toString(), loggedClass.toString(), msg);
        
        assertThat(response.getLog(), is(expected));
    }
    
    @Test
    public void shouldFormatAndUpdateResponseLog() {
        final String msg = "The Message";
        final ResponseITSKCASoap response = new ResponseITSKCASoap();
        final Class loggedClass = ITSKCASoap.class;
        final Date time = new Date();
        
        logger.setLog(msg, response, loggedClass, time);
        final String expexted = String.format(format, time.toString(), loggedClass.toString(), msg);
        
        assertThat(response.getLog(), is(expexted));
    }
    
}
