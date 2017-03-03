package ITSK.OIM.WEB.MDM;

import ITSK.OIM.WEB.GEN.RegAuthLegacyService;
import com.sun.net.ssl.internal.ssl.Provider;
import java.net.URL;
import java.net.URLStreamHandler;
import java.security.Security;
import java.util.HashMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import ru.CryptoPro.JCP.JCP;
import sun.net.www.protocol.https.Handler;

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

    HashMap<String, Object> findUserParams(HashMap params, String folderID, final RegAuthLegacyContract port, String CAOIDemail, String email, ResponseITSKCASoap response) throws Exception {
        HashMap<String, Object> resultFindUserCA;
        //Поск пользователя УЦ
        if (params.get("CAUSERID") != null && !params.get("CAUSERID").toString().trim().isEmpty()) {
            //Поск пользователя УЦ по UserID CA
            final String userId = params.get("CAUSERID").toString().trim();
            resultFindUserCA = findUserCA(folderID, "UserId", userId, 8, port, response);
        } else {
            //Поск пользователя УЦ по Email
            resultFindUserCA = findUserCA(folderID, "OID." + CAOIDemail, email.trim(), 8, port, response);
        }
        return resultFindUserCA;
    }

    public RegAuthLegacyContract initializeCA(HashMap params, ResponseITSKCASoap response) throws Exception {
        RegAuthLegacyContract port = null;
        try {
            //char[] charPwd = Params.get("PasswordKeyStoreJCP").toString().toCharArray();
            char[] charPwd = new char[]{'Q', 'w', 'e', 'r', 't', 'y', '1', '2', '3'};
            // JSSE System Properties
            Security.addProvider(new JCP());
            Security.addProvider(new Provider());
            System.setProperty("javax.net.ssl.keyStoreType", "HDImageStore");
            System.setProperty("javax.net.ssl.trustStoreType", "HDImageStore");
            Security.setProperty("ssl.SocketFactory.provider", "ru.CryptoPro.ssl.SSLSocketFactoryImpl");
            Security.setProperty("ssl.ServerSocketFactory.provider", "ru.CryptoPro.ssl.SSLServerSocketFactoryImpl");
            Security.setProperty("ssl.KeyManagerFactory.algorithm", "GostX509");
            Security.setProperty("ssl.TrustManagerFactory.algorithm", "GostX509");
            System.setProperty("javax.net.ssl.keyStore", "C:/MDM.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "Qwerty123");
            System.setProperty("javax.net.ssl.trustStore", "C:/MDM.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "Qwerty123");
            System.setProperty("java.protocol.handler.pkgs", "sun.net.www.protocol");
            System.setProperty("UseSunHttpHandler", "true");
            System.setProperty("javax.xml.ws.spi.Provider", "com.sun.xml.internal.ws.spi.ProviderImpl");
            if (params.get("CASoapServiceWSDLurl") != null) {
                //Использование wsdl url из параметра IT Resource
                //URL newEndpoint = new URL("file:/C:/Users/Administrator/Desktop/Certs20160531/RegAuthLegacyService.wsdl");
                //URL newEndpoint = new URL("https://spb99-t-ca02/RA/RegAuthLegacyService.svc?singleWsdl");
                URLStreamHandler handler = new Handler();
                URL newEndpoint = new URL(null, params.get("CASoapServiceWSDLurl").toString(), handler);
                QName qname = new QName("http://cryptopro.ru/pki/registration/service/2010/03", "RegAuthLegacyService");
                RegAuthLegacyService service = new RegAuthLegacyService(newEndpoint, qname);
                port = service.getRegAuthLegacyServiceEndpoint();
            } else {
                //Использование фиксированного wsdl url из кода
                RegAuthLegacyService service = new RegAuthLegacyService();
                port = service.getRegAuthLegacyServiceEndpoint();
            }
            //Устанавливаем таймаут соединения
            //((BindingProvider)port).getRequestContext().put("com.sun.xml.ws.connect.timeout", 60000);
            //Устанавливаем таймаут запроса
            //((BindingProvider)port).getRequestContext().put("com.sun.xml.ws.request.timeout", 30000);
            return port;
        } catch (Exception e) {
            logger.setErrorLog(ITSKCASoap.getStackTrace(e), response, this.getClass());
            return null;
        }
    }
    
}
