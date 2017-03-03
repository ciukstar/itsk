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

    private final String logFormat = "<%s> <%s> <MDM> <LOG> %s%n";
    private final String errorFormat = "<%s> <%s> <MDM> <ERROR> %s%n";
    private final String msg = "The Message";
    private final String otherMessage = "Some other message";
    private final ResponseITSKCASoap response = new ResponseITSKCASoap();
    private final Class loggedClass = ITSKCASoap.class;
    private final Date time = new Date();


    @Test
    public void shouldFormatAndUpdateResponseErrorLog() {

        String entry = logger.logError(msg, loggedClass, time);

        String expected = String.format(errorFormat,
                time.toString(), loggedClass.toString(), logger.getShortStackTrace(msg, 20));

        assertThat(entry, is(expected));

    }

    @Test
    public void shouldFormatAndUpdateResponseLog() {

        String entry = logger.log(msg, loggedClass, time);
        
        final String expexted = String.format(logFormat, time.toString(), loggedClass.toString(), msg);

        assertThat(entry, is(expexted));
    }

}
