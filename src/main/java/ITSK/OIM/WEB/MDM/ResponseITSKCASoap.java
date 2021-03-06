package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author birillo
 */
public class ResponseITSKCASoap implements Response {
    
    private final Map<String, Object> propertyMap;
    private String log = "";
    private Outcome type;

    ResponseITSKCASoap(Map<String, Object> props, String log, Outcome type) {
        this.propertyMap = props;
        this.log = log;
        this.type = type;
    }

    ResponseITSKCASoap() {
        this.propertyMap = new HashMap();
    }

    @Override
    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String getResult() {
        return type.name();
    }

    public void setResult(String result) {
        this.type = Outcome.valueOf(result);
    }

    public void setOutcome(String value) {
        setResult(value);
    }
    
    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(HashMap parametrMap) {
        this.propertyMap.clear();
        this.propertyMap.putAll(parametrMap);
    }

    public void addEntry(String key, Object value) {
        this.propertyMap.put(key, value);
    }
    
    public boolean isEmpty() {
        return propertyMap.isEmpty();
    }

    ResponseITSKCASoap appendLog(String msg) {
        this.log = this.log + msg;
        return this;
    }

    public void addEntries(HashMap<String, Object> entries) {
        this.propertyMap.putAll(entries);
    }

    @Override
    public Map<String, Object> getProps() {
        return propertyMap;
    }

    @Override
    public Outcome getOutcome() {
        return Outcome.valueOf(getResult());
    }
}
