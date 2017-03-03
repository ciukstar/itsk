package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import javax.xml.ws.Holder;

/**
 *
 * @author sergiu
 */
public class UC {

    private final AppLogger logger;

    public UC(AppLogger logger) {
        this.logger = logger;
    }
    
    public HashMap findUserCA(String folderID, String condFild, String condValue, int condOperator, RegAuthLegacyContract port, ResponseITSKCASoap response) throws Exception {
        Holder<String> getUserRecordListResult = new Holder<>();
        Holder<Integer> resultCount = new Holder<>();
        Holder<Integer> totalRowCount = new Holder<>();
        HashMap result = new HashMap();
        try {
            port.getUserRecordList(folderID, "", "", Boolean.TRUE, condFild, condValue, condOperator, 1, 100, Boolean.TRUE, getUserRecordListResult, resultCount, totalRowCount);
            result.put("resultCount", resultCount.value);
            result.put("getUserRecordListResult", getUserRecordListResult.value);
        } catch (Exception e) {
            String ss = ITSKCASoap.getStackTrace(e);
            logger.setErrorLog(ss, response, this.getClass());
            return result;
        }
        return result;
    }
    
}
