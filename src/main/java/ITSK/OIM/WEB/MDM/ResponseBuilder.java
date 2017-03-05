package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sergiu
 */
public class ResponseBuilder {
    public static PropStep instance() {
        return new Steps();
    }
    
    public interface PropStep {
        PropStep addProp(String key, Object value);
        PropStep addAllProps(Map<String, Object> props);
        LogStep noProps();
        LogStep noMoreProps();
    }
    
    public interface LogStep {
        LogStep addLog(String log);
        BuildStep noLogs();
        BuildStep noMoreLogs();
    }
    
    public interface BuildStep {
        Response buildErrorResponse();
        Response buildSuccessResponse();
    }
    
    private static class Steps implements PropStep, LogStep, BuildStep {

        private final Map<String, Object> props = new HashMap<>();
        private final StringBuilder logs = new StringBuilder();
        @Override
        public PropStep addProp(String key, Object value) {
            this.props.put(key, value);
            return this;
        }

        @Override
        public PropStep addAllProps(Map<String, Object> props) {
            this.props.putAll(props);
            return this;
        }

        @Override
        public LogStep noProps() {
            this.props.clear();
            return this;
        }

        @Override
        public LogStep noMoreProps() {
            return this;
        }

        @Override
        public LogStep addLog(String log) {
            this.logs.append(log);
            return this;
        }

        @Override
        public BuildStep noLogs() {
            this.logs.setLength(0);
            return this;
        }

        @Override
        public BuildStep noMoreLogs() {
            return this;
        }

        @Override
        public Response buildErrorResponse() {
            return new ResponseITSKCASoap(props, logs.toString(), Outcome.ERROR);
        }

        @Override
        public Response buildSuccessResponse() {
            return new ResponseITSKCASoap(props, logs.toString(), Outcome.SUCCESS);
        }
        
    }
}
