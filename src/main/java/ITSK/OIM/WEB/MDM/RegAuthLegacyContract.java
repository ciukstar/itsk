package ITSK.OIM.WEB.MDM;

import javax.xml.ws.Holder;

/**
 *
 * @author sergiu
 */
public interface RegAuthLegacyContract {

    public String submitAndAcceptRegRequest(String folderID, String resultSignRequestCABase64, String Email, String keyPhrase, String description, String managerComment, Boolean FALSE);

    public String getRegRequestRecord(String resultSubmitAndAcceptRegRequest, String string);

    public void createTokenForUser(String userID, Holder<String> webLogin, Holder<String> webPassword);

    public void getCertificateRecordList(String folderID, String v, String string, Boolean TRUE, String userId, String CAuserID, int i, int i0, int i1, Boolean TRUE0, Holder<String> certificateRecordListResult, Holder<Integer> resultCount, Holder<Integer> totalRowCount);

    public String submitAndAcceptRevRequest(String resultSignRequestCABase64, String суид, Boolean TRUE);

    public void getUserRecordList(String folderID, String string, String string0, Boolean TRUE, String condFild, String condValue, int condOperator, int i, int i0, Boolean TRUE0, Holder<String> userRecordListResult, Holder<Integer> resultCount, Holder<Integer> totalRowCount);

}
