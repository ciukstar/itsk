package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class LogDump {
    
    public static LogDump instance(String log) {
        return new LogDump(log);
    }
    
    public static LogDump instance() {
        return new LogDump();
    }
    
    private final StringBuilder acc;

    private LogDump(String log) {
        this.acc = new StringBuilder(log);
    }
    
    private LogDump() {
        this("");
    }
    
    public void appendLog(String log, char... delims) {
        this.acc.append(log).append(delims);
    }
    
    public void appendLog(String log) {
        this.acc.append(log);
    }
    public String getLogs() {
        return acc.toString();
    }
    public String popLog() {
        String log = acc.toString();
        acc.setLength(0);
        return log;
    }
}
