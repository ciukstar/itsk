package ITSK.OIM.WEB.MDM;

import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sergiu
 */
public class ResponseBuilderTest {
    
    @Before
    public void setUp() {
    }

    @Test
    public void shouldBuildAnErrorRespose() {
        final String log1 = "SomeLoge";
        final String log2 = "AnotherLog";
        
        Response response = ResponseBuilder.instance()
                .noMoreProps()
                .addLog(log1)
                .addLog(log2).noMoreLogs()
                .buildErrorResponse();
        
        assertThat(response.getProps().size(), is( 0 ));
        assertThat(response.getOutcome(), is(Outcome.ERROR));

    }

    @Test
    public void shouldBuildASuccessRespose() {
        final String key1 = "SomeProp";
        final String value1 = "SomeValue";
        
        Response response = ResponseBuilder.instance()
                .addProp(key1, value1)
                .addProp("AnotherProp", "AnotherValue").noMoreProps()
                .addLog("SomeLoge")
                .addLog("AnotherLog").noMoreLogs()
                .buildSuccessResponse();
        
        assertTrue(response.getProps().containsKey(key1));
        assertThat(response.getProps().get(key1).toString(), is(value1));
        assertFalse(response.getLog().isEmpty());
        assertThat(response.getOutcome(), is(Outcome.SUCCESS));

    }
    
}
