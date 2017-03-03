package ITSK.OIM.WEB.MDM;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import su.jet.oim.utils.NotificationUtils;

/**
 *
 * @author sergiu
 */
public class AppLogger {
    
    String getShortStackTrace(String st, int line) {
        String[] split = st.split("\n");
        StringBuilder sb = new StringBuilder();
        if (split.length > line) {
            for (int i = 0; i < line; i++) {
                sb.append(split[i].trim());
                sb.append("\n");
            }
        } else {
            sb.append(st);
        }
        return sb.toString();
    }

    void setErrorLog(String logString, ResponseITSKCASoap response, Class<?> clazz) {
        //Utils.setProcessTaskNote(taskId, taskNote);
        //notifyAdminAboutError(logString);
        String ss = "";
        ss = getShortStackTrace(logString, 20);
        response.log = response.log + "<" + new Date() + "> " + "<" + clazz + "> " + "<MDM> " + "<ERROR> " + ss + "\n";
        //LogStr = LogStr + "<" + new Date() + "> " + "<MDM> " + "<ERROR> " + logString + "\n";
        System.out.println(logString);
        NotificationUtils.notifyAboutAnyExeption(logString);
        Logger.getLogger(clazz.getName()).log(Level.SEVERE, logString);
    }

    void setLog(String msg, ResponseITSKCASoap response, Class<?> clazz, Date time) {
        final String format = "<%s> <%s> <MDM> <LOG> %s%n";
        response.appendLog(String.format(format, time.toString(), clazz.toString(), msg));
        System.out.println(response.log);
        Logger.getLogger(clazz.getName()).finest(response.log);
    }
    void setLog(String msg, ResponseITSKCASoap response, Class<?> clazz) {
        setLog(msg, response, clazz, new Date());
    }

}
