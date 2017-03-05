package ITSK.OIM.WEB.MDM;

import java.util.Map;

/**
 *
 * @author sergiu
 */
public class ResponseBuilder {
    public static Response successResponseBuilder(final Map<String, Object> props, final String log) {
        return new ResponseITSKCASoap(props, log, "SUCCESS");
    }
    
    public static Response errorResponseBuilder(final Map<String, Object> props, final String log) {
        return new ResponseITSKCASoap(props, log, "ERROR");
    }
}
