package ITSK.OIM.WEB.MDM;

import java.util.HashMap;
import javax.xml.ws.Holder;

/**
 *
 * @author sergiu
 */
public class UC {

    public HashMap findUserCA(String folderID, String condFild, String condValue, int condOperator, RegAuthLegacyContract port, ITSKCASoap itskcaSoap) throws Exception {
        Holder<String> getUserRecordListResult = new Holder<String>();
        Holder<Integer> resultCount = new Holder<Integer>();
        Holder<Integer> totalRowCount = new Holder<Integer>();
        //List<String> result =  new ArrayList<String>();
        HashMap result = new HashMap();
        try {
            port.getUserRecordList(folderID, "", "", Boolean.TRUE, condFild, condValue, condOperator, 1, 100, Boolean.TRUE, getUserRecordListResult, resultCount, totalRowCount);
            result.put("resultCount", resultCount.value);
            result.put("getUserRecordListResult", getUserRecordListResult.value);
        } catch (Exception e) {
            String ss = ITSKCASoap.getStackTrace(e);
            itskcaSoap.setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error find user CA:", e);
            return result;
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));
        }
        return result;
    }
    
}
