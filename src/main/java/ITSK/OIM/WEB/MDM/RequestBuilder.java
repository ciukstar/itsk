package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class RequestBuilder {

    Request buildRegisterUserRequest(final UserAccountInfo account) {

        return new Request() {
            @Override
            public String contents() {
                //Сформировать запрос на регисрацию пользователя
                final String request = "<ProfileAttributesChange> \n"
                        + "<To> \n"
                        + "<Attribute Oid=\"" + account.getCaOIDUPN() + "\" Value=\"" + account.getADLogin() + "\" /> \n"
                        + "<Attribute Oid=\"" + account.getCAOIDemail() + "\" Value=\"" + account.getEmail() + "\" /> \n"
                        + "<Attribute Oid=\"" + account.getCaOIDCN() + "\" Value=\"" + account.getFio() + "\" /> \n"
                        + "</To> \n"
                        + "</ProfileAttributesChange> \n";
                return request;
            }
        };

    }
}
