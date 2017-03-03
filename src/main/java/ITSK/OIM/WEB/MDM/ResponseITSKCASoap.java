package ITSK.OIM.WEB.MDM;

import java.util.HashMap;

/**
 *
 * @author birillo
 */
public class ResponseITSKCASoap {

    HashMap propertyMap;
    String log = "";
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

    public HashMap getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(HashMap parametrMap) {
        this.propertyMap = parametrMap;
    }

    public boolean isEmpty() {
        return propertyMap.isEmpty();
    }

    ResponseITSKCASoap appendLog(String msg) {
        this.log = this.log + msg;
        return this;
    }
}
