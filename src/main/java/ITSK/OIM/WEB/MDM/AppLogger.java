package ITSK.OIM.WEB.MDM;

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
    
}
