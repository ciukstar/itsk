package ITSK.OIM.WEB.MDM;

import java.util.HashMap;

/**
 *
 * @author birillo
 */
public class ResponseITSKCASoap {

    private final HashMap propertyMap;
    private String log = "";
    String result = "ERROR";

    public ResponseITSKCASoap() {
        this.propertyMap = new HashMap();
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setOutcome(String value) {
        setResult(value);
    }
    
    public HashMap getPropertyMap() {
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
}
