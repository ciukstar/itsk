package ITSK.OIM.WEB.MDM;

import java.util.Map;

/**
 *
 * @author sergiu
 */
public interface Response {
    Map<String, Object> getProps();
    String getLog();
    Outcome getOutcome();
    String getResult();
}
