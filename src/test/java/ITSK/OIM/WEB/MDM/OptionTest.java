package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sergiu
 */
public class OptionTest {

    @Before
    public void setUp() {
    }
    
    private final String value = "The Value";
    private final String defaultValue = "Default Value";

    @Test
    public void shouldReturnDefaultValue() {

        String res = Option.ofNullable((String) null).orElseGet(defaultValue);

        assertThat(res, is(defaultValue));
    }

    @Test
    public void shouldReturnGivenValue() {

        String res = Option.ofNullable(value).orElseGet(defaultValue);

        assertThat(res, is(value));
    }

}
