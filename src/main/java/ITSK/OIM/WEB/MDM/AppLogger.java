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

    String logError(String msg, Class<?> clazz, Date time) {
        final String format = "<%s> <%s> <MDM> <ERROR> %s%n";
        final String entry = String.format(format, time.toString(), clazz.toString(), getShortStackTrace(msg, 20));
        System.out.println(msg);
        NotificationUtils.notifyAboutAnyExeption(msg);
        Logger.getLogger(clazz.getName()).log(Level.SEVERE, msg);
        return entry;
    }

    String logError(String msg, Class<?> clazz) {
        return logError(msg, clazz, new Date());
    }

    String log(String msg, Class<?> clazz, Date time) {
        final String format = "<%s> <%s> <MDM> <LOG> %s%n";
        final String entry = String.format(format, time.toString(), clazz.toString(), msg);
        System.out.println(entry);
        Logger.getLogger(clazz.getName()).finest(entry);
        return entry;
    }

    String log(String msg, Class<?> clazz) {
        return log(msg, clazz, new Date());
    }

}
