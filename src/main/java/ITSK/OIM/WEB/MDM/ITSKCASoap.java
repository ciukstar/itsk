package ITSK.OIM.WEB.MDM;

import ITSK.OIM.WEB.GEN.RegAuthLegacyService;
import com.objsys.asn1j.runtime.Asn1BerDecodeBuffer;
import com.objsys.asn1j.runtime.Asn1BerEncodeBuffer;
import com.objsys.asn1j.runtime.Asn1Null;
import com.objsys.asn1j.runtime.Asn1ObjectIdentifier;
import com.objsys.asn1j.runtime.Asn1OctetString;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLStreamHandler;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.xml.sax.InputSource;
import ru.CryptoPro.JCP.JCP;
import sun.security.jca.Providers;
import org.w3c.dom.NodeList;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.CMSVersion;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.CertificateChoices;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.CertificateSet;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.ContentInfo;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.DigestAlgorithmIdentifier;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.DigestAlgorithmIdentifiers;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.EncapsulatedContentInfo;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.IssuerAndSerialNumber;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignatureAlgorithmIdentifier;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignatureValue;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignedData;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignerIdentifier;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignerInfo;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignerInfos;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.CertificateSerialNumber;
import ru.CryptoPro.JCP.params.OID;
import static su.jet.oim.utils.NotificationUtils.notifyAboutAnyExeption;

/**
 *
 * @author Birillo
 */
public class ITSKCASoap {

    private static final Logger LOGGER = Logger.getLogger(ITSKCASoap.class.getName());
    //private String LogStr = "";
    ResponseITSKCASoap objResponseITSKCASoap = new ResponseITSKCASoap();
    private final CredLoader credLoader;
    private final UC uc;

    public ITSKCASoap(CredLoader credLoader, UC uc) {
        this.credLoader = credLoader;
        this.uc = uc;
    }

    //private Provider xmlDSigProvider = null;
    /**
     * @param args the command line arguments //private Provider xmlDSigProvider
     * = null; / * @param args the command line arguments
     */

    /* public static void main(String[] args) throws Exception {
        // TODO code application logic here
       HashMap result = new HashMap();
       HashMap Params = new HashMap();
       ITSKCASoap test = new ITSKCASoap();
      //result = test.RevokeUser("1","",3,Params);
      result = test.createUser("1","2","3",Params);
    }*/
    public ResponseITSKCASoap createUser(String Email, String ADLogin, String FIO, HashMap Params) throws Exception {

        //System.getProperties().put("https.proxyHost","www-proxy.idc.myproxy.com");
        //System.getProperties().put("https.proxyPort", "80");
        //OperationResult result = null;
        HashMap result = new HashMap();
        HashMap OIDlist = new HashMap();
        RegAuthLegacyContract port = null;
        try {

            //String folderID = Params.get("CAFolderID").toString();
            String folderID = "c5619331-7426-e611-80ed-00505681c485";
            //char[] charPwd = Params.get("PasswordKeyStoreJCP").toString().toCharArray();
            char[] charPwd = new char[]{'Q', 'w', 'e', 'r', 't', 'y', '1', '2', '3'};
            //String CAOIDemail = Params.get("EMAIL").toString();
            String CAOIDemail = "1.2.840.113549.1.9.1";
            //String CAOIDemail = Params.get("UPN").toString();
            String caOIDUPN = "1.3.6.1.4.1.311.20.2.3";
            //String CAOIDemail = Params.get("CN").toString();
            String caOIDCN = "2.5.4.3";

            //Инициализировать подключение к УЦ
            port = InitializationCA(Params);

            Pair<PrivateKey, X509Certificate> cred = credLoader.loadCredentials(charPwd);

            //////////////////////////////////
            /*Properties pro = System.getProperties();
        
        for(String i : pro.stringPropertyNames())
        {
            System.out.println(i + " => " + pro.getProperty(i));
        }*/
            int j = 0;
            Holder<String> webLogin = new Holder<String>();
            Holder<String> webPassword = new Holder<String>();
            String RegRequest = "";
            String keyPhrase = "key";
            String description = "СУИД:Предоставление доступа в УЦ";
            String managerComment = "СУИД:Предоставление доступа в УЦ";
            webLogin.value = "";
            webPassword.value = "";
            String resultSubmitAndAcceptRegRequest = "";
            HashMap resultCreateTokenForUser = new HashMap();
            String resultgetRegRequestRecord = "";
            String resultSignRequestCABase64 = "";
            HashMap ResultFindUserCA = new HashMap();
            String CAuserID = "";
            //Email = "metelkin.AS@gazprom-neft.ru";
            List<List<String>> resultParseXML = new ArrayList<>();
            List<String> parseAttrs = new ArrayList<String>();
            parseAttrs.add("UserId");
            List<List<String>> UserList = new ArrayList<>();

            setLog("Begin find user " + Email + " in CA");

            //Поск пользователя УЦ
            if (Params.get("CAUSERID") != null && !Params.get("CAUSERID").toString().trim().isEmpty()) {
                //Поск пользователя УЦ по UserID CA
                CAuserID = Params.get("CAUSERID").toString().trim();
                ResultFindUserCA = uc.findUserCA(folderID, "UserId", CAuserID, 8, port, this);

            } else {
                //Поск пользователя УЦ по Email
                ResultFindUserCA = uc.findUserCA(folderID, "OID." + CAOIDemail, Email.trim(), 8, port, this);
            }

            if (ResultFindUserCA.isEmpty()) {
                setErrorLog("Error: Not Found CA Users, filter- " + CAOIDemail + "->" + Email);
                objResponseITSKCASoap.propertyMap = result;
                return objResponseITSKCASoap;
            } else if ((int) ResultFindUserCA.get("resultCount") > 0) {
                //Дополнитьльно для парсинга добавляем статус пользователя
                parseAttrs.add("Status");
                //Парсинг результата поиска пользователя УЦ
                resultParseXML = parseXML(ResultFindUserCA.get("getUserRecordListResult").toString(), parseAttrs);

                if (resultParseXML.size() > 0) {
                    UserList = resultParseXML;

                    setLog("Parsing result search user " + Email + " complite");

                    if (UserList.size() == 1) {
                        CAuserID = UserList.get(0).get(0);
                        result.put("UserId", CAuserID);
                        if (CAuserID.isEmpty()) {
                            setErrorLog("Error: Not parsing result find CA user");
                            objResponseITSKCASoap.propertyMap = result;
                            return objResponseITSKCASoap;
                        }

                        setLog("User found, User ID: " + CAuserID);

                        if (UserList.get(0).get(1).equals("A")) {
                            //Создать маркер временного доступа для пользователя
                            resultCreateTokenForUser = createTokenForUser(port, CAuserID, webLogin, webPassword);
                            if (!resultCreateTokenForUser.isEmpty()) {
                                setLog("Create Marker CA for user, User ID: " + CAuserID + " Complite");

                                objResponseITSKCASoap.result = "SUCCESS";
                                result.putAll(resultCreateTokenForUser);
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;

                            } else {
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;
                            }

                        } /*else if (UserList.get(0).get(1).equals("I"))
                  {
                      //Разблокировать пользователя УЦ
                        port.activateUser(CAuserID, Boolean.TRUE);
                        
                        //Создать маркер временного доступа для пользователя
                      resultCreateTokenForUser = objITSKCASoap.createTokenForUser(port,CAuserID,webLogin,webPassword);
                      if (!resultCreateTokenForUser.isEmpty()) 
                        {
                           setLog("<" + this.getClass() + "> " + " Create Marker CA for user, User ID: "+ CAuserID + " Complite");

                            result.put("RESULT","SUCCESS");
                            result.putAll(resultCreateTokenForUser);
                            result.put("LOGS",LogStr);
                            return result;
                        } else {
                            result.put("RESULT","ERROR");
                            result.put("LOGS",LogStr);
                            return result;
                      }
                      
                  }*/ else {
                            setErrorLog("Error: user" + Email + "is not active status");
                            objResponseITSKCASoap.propertyMap = result;
                            return objResponseITSKCASoap;
                        }

                    } else {
                        for (int i = 0; i < UserList.size(); i++) {
                            if (UserList.get(i).get(1).equals("A")) {
                                j = j + 1;
                                CAuserID = UserList.get(i).get(0);
                                result.put("UserId", CAuserID);
                            }

                        }
                        if (j == 1) {

                            setLog("Found one active user CA for user email: " + Email + "User ID:" + CAuserID);

                            //Создать маркер временного доступа для пользователя
                            resultCreateTokenForUser = createTokenForUser(port, CAuserID, webLogin, webPassword);
                            if (!resultCreateTokenForUser.isEmpty()) {
                                setLog("Create Marker CA for user, User ID: " + CAuserID + " Complite");
                                objResponseITSKCASoap.result = "SUCCESS";
                                result.putAll(resultCreateTokenForUser);
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;
                            } else {
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;
                            }

                        } else {
                            //Ошибка, найдено больше одного пользователя               
                            setErrorLog("Error: In CA found more than one active user for email" + Email);
                            objResponseITSKCASoap.propertyMap = result;
                            return objResponseITSKCASoap;
                        }

                    }

                }

            } else {
                setLog("User not found, Email: " + Email);

                //Сформировать запрос на регисрацию пользователя
                RegRequest
                        = "<ProfileAttributesChange> \n"
                        + "<To> \n"
                        + "<Attribute Oid=\"" + caOIDUPN + "\" Value=\"" + ADLogin + "\" /> \n"
                        + "<Attribute Oid=\"" + CAOIDemail + "\" Value=\"" + Email + "\" /> \n"
                        + "<Attribute Oid=\"" + caOIDCN + "\" Value=\"" + FIO + "\" /> \n"
                        + "</To> \n"
                        + "</ProfileAttributesChange> \n";

                setLog("Request for create user CA complite, Request: \n" + RegRequest);

                //Подписать запрос
                resultSignRequestCABase64 = signRequestCA(RegRequest, cred.getLeft(), cred.getRight());
                if (!resultSignRequestCABase64.isEmpty()) {
                    setLog("Request is signed");
                    //Выполнить запрос на регистрацию пользователя УЦ
                    resultSubmitAndAcceptRegRequest = port.submitAndAcceptRegRequest(folderID, resultSignRequestCABase64, Email, keyPhrase, description, managerComment, Boolean.FALSE);

                    //Сохранить результат submitAndAcceptRegRequest (номер запроса)
                    result.put("RegID", resultSubmitAndAcceptRegRequest);

                    //Получить описание запроса на регистрацию + получить UserID
                    resultgetRegRequestRecord = port.getRegRequestRecord(resultSubmitAndAcceptRegRequest, "");

                    //Парсинг результата поиска пользователя УЦ
                    resultParseXML = parseXML(resultgetRegRequestRecord, parseAttrs);
                    if (resultParseXML.size() == 1) {
                        CAuserID = resultParseXML.get(0).get(0);
                        result.put("UserId", CAuserID);

                        setLog("User register in CA, RegID: " + resultSubmitAndAcceptRegRequest + " ,UserID: " + CAuserID);

                        if (CAuserID.isEmpty()) {
                            setErrorLog("Error in the create new CA user, Not parsing result find userID for RegRequest");
                            objResponseITSKCASoap.propertyMap = result;
                            return objResponseITSKCASoap;
                        } else {
                            //Создать маркер временного доступа для пользователя
                            resultCreateTokenForUser = createTokenForUser(port, CAuserID, webLogin, webPassword);
                            if (!resultCreateTokenForUser.isEmpty()) {
                                setLog("Create Token for user: " + CAuserID + " Email: " + Email);

                                result.putAll(resultCreateTokenForUser);
                                objResponseITSKCASoap.result = "SUCCESS";
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;

                            } else {
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;
                            }
                        }
                    } else {
                        //Ошибка, найдено больше одного пользователя               
                        setErrorLog("Error: Parsing result found more than one CA user");
                        objResponseITSKCASoap.propertyMap = result;
                        return objResponseITSKCASoap;
                    }

                }
            }

        } catch (Exception e) {
            String ss = getStackTrace(e);
            setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error initialization CreateAccount", e);
            objResponseITSKCASoap.propertyMap = result;
            return objResponseITSKCASoap;
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));          

        }
        objResponseITSKCASoap.result = "SUCCESS";
        objResponseITSKCASoap.propertyMap = result;
        return objResponseITSKCASoap;
    }

    public HashMap createTokenForUser(
            RegAuthLegacyContract port,
            String userID,
            Holder<String> webLogin,
            Holder<String> webPassword
    ) throws RegAuthLegacyContractCreateTokenForUserErrorInfoFaultMessage {

        HashMap result = new HashMap();
        try {
            port.createTokenForUser(userID, webLogin, webPassword);
            result.put("webLogin", webLogin.value);
            result.put("webPassword", webPassword.value);
            return result;
        } catch (Exception e) {
            String ss = getStackTrace(e);
            setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error initialization CreateAccount", e);
            return result;
        }
    }

    public RegAuthLegacyContract InitializationCA(HashMap Params) throws Exception {

        RegAuthLegacyContract port = null;
        try {

            //char[] charPwd = Params.get("PasswordKeyStoreJCP").toString().toCharArray();
            char[] charPwd = new char[]{'Q', 'w', 'e', 'r', 't', 'y', '1', '2', '3'};

// JSSE System Properties
            Security.addProvider(new ru.CryptoPro.JCP.JCP());
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

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

            if (Params.get("CASoapServiceWSDLurl") != null) {
                //Использование wsdl url из параметра IT Resource
                //URL newEndpoint = new URL("file:/C:/Users/Administrator/Desktop/Certs20160531/RegAuthLegacyService.wsdl");
                //URL newEndpoint = new URL("https://spb99-t-ca02/RA/RegAuthLegacyService.svc?singleWsdl");

                URLStreamHandler handler = new sun.net.www.protocol.https.Handler();
                java.net.URL newEndpoint = new URL(null, Params.get("CASoapServiceWSDLurl").toString(), handler);
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
            String ss = getStackTrace(e);
            setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error initialization CreateAccount", e);
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));
            return null;
        }
    }

    public ResponseITSKCASoap RevokeUser(String Email, String UserID, int RevocationReason, HashMap Params) throws Exception {

        HashMap result = new HashMap();

        try {

            RegAuthLegacyContract port = null;

            //String folderID = Params.get("CAFolderID").toString();
            String folderID = "c5619331-7426-e611-80ed-00505681c485";
            //char[] charPwd = Params.get("PasswordKeyStoreJCP").toString().toCharArray();
            char[] charPwd = new char[]{'Q', 'w', 'e', 'r', 't', 'y', '1', '2', '3'};
            //String CAOIDemail = Params.get("EMAIL").toString();
            String CAOIDemail = "1.2.840.113549.1.9.1";
            //String CAOIDemail = Params.get("UPN").toString();
            String caOIDUPN = "1.3.6.1.4.1.311.20.2.3";
            //String CAOIDemail = Params.get("CN").toString();
            String caOIDCN = "2.5.4.3";

            int j = 0;
            int FlagFindEmail = 0;
            String revCertList = "";
            Holder<String> getCertificateRecordListResult = new Holder<String>();
            Holder<Integer> resultCount = new Holder<Integer>();
            Holder<Integer> totalRowCount = new Holder<Integer>();
            String resultSubmitAndAcceptRevReques = "";
            String resultSignRequestCABase64 = "";
            HashMap ResultFindUserCA = new HashMap();
            String CAuserID = "";
            //Email = "Andrianov.IA@gazprom-neft.ru";//"Moskvichev.IA@Gazprom-Neft.RU";
            List<List<String>> resultParseXML = new ArrayList<>();
            List<String> parseAttrsCert = new ArrayList<String>();
            List<String> parseAttrsUsr = new ArrayList<String>();
            parseAttrsCert.add("SerialNumber");
            parseAttrsCert.add("Thumbprint");
            parseAttrsUsr.add("UserId");
            String RevRequest = "";
            List<String> certStr = null;
            List<List<String>> certList = new ArrayList<>();
            List<List<String>> userList = new ArrayList<>();

//Инициализировать подключение к УЦ
            port = InitializationCA(Params);

            //Загрузить KeyStore
            KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
            keyStore.load(null, null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey("CA", charPwd);
            X509Certificate cert = (X509Certificate) keyStore.getCertificate("CA");

            if (!UserID.isEmpty()) {
                result.put("UserId", UserID);

                //Поск пользователя УЦ
                ResultFindUserCA = uc.findUserCA(folderID, "UserId", UserID, 8, port, this);
                if (ResultFindUserCA.isEmpty()) {
                    FlagFindEmail = 1;

                } else if ((int) ResultFindUserCA.get("resultCount") > 0) {
                    setLog("Find user " + UserID + " in CA");

                    if (ResultFindUserCA.get("resultCount").equals(1)) {
                        //Получить список сертификатов пользователя УЦ
                        port.getCertificateRecordList(folderID, "V", "", Boolean.TRUE, "UserId", UserID, 8, 1, 100, Boolean.TRUE, getCertificateRecordListResult, resultCount, totalRowCount);
                        if (resultCount.value > 0) {
                            setLog("complite find list of certificates CA, userID " + UserID);
                            //Парсинг результата поиска сертификатов пользователя УЦ
                            resultParseXML = parseXML(getCertificateRecordListResult.value, parseAttrsCert);

                            //Сформировать запрос на отзыв сертификатов пользователя
                            for (int i = 0; i < resultParseXML.size(); i++) {
                                RevRequest = "SN=" + resultParseXML.get(i).get(0) + ",TP=" + resultParseXML.get(i).get(1) + ",RR=" + RevocationReason + "";

                                //Подписать запрос
                                resultSignRequestCABase64 = signRequestCA(RevRequest, privateKey, cert);

                                //Отзыв сертификатов пользователя
                                resultSubmitAndAcceptRevReques = port.submitAndAcceptRevRequest(resultSignRequestCABase64, "СУИД", Boolean.TRUE);

                                //Сохраняем сертификаты на отзыв
                                revCertList = revCertList + "RevID: " + resultSubmitAndAcceptRevReques + ",CertSerialNum: " + resultParseXML.get(i).get(0) + ";";
                                setLog("Revoked certificate SR-" + resultParseXML.get(i).get(0) + " to user " + Email);

                            }

                            setLog("Revoke user certificates is complite, userID " + UserID + " in CA");
                            result.put("CertList", revCertList);
                        } else {
                            setLog("Not found active certificates in CA for user " + Email);
                        }

                        /*
                        //Парсинг результата поиска пользователя УЦ
                        parseAttrsUsr.clear();
                        parseAttrsUsr.add("Status");
                        resultParseXML = objITSKCASoap.parseXML(ResultFindUserCA.get("getUserRecordListResult").toString(),parseAttrsUsr);
                        if (resultParseXML.get(0).trim().equals("A"))
                         {
                            //Блокируем пользователя УЦ
                             port.deactivateUser(UserID, Boolean.TRUE);
                         }
                        setLog("MDM: Deactivate User CA is complite, userID "+ UserID +" in CA");
                         */
                        objResponseITSKCASoap.result = "SUCCESS";
                        objResponseITSKCASoap.propertyMap = result;
                        return objResponseITSKCASoap;

                    }
                }
            } else {
                FlagFindEmail = 1;
            }

            if (FlagFindEmail == 1) {
                ResultFindUserCA = uc.findUserCA(folderID, "OID." + CAOIDemail, Email.trim(), 8, port, this);
                if (ResultFindUserCA.isEmpty()) {
                    setErrorLog("Error in process find CA user, filter:" + CAOIDemail + "->" + Email);
                    objResponseITSKCASoap.propertyMap = result;
                    return objResponseITSKCASoap;

                } else if ((int) ResultFindUserCA.get("resultCount") > 0) {

                    if (ResultFindUserCA.get("resultCount").equals(1)) {
                        setLog("Find user for email" + Email + " in CA");
                        //Парсинг результата поиска пользователя УЦ
                        resultParseXML = parseXML(ResultFindUserCA.get("getUserRecordListResult").toString(), parseAttrsUsr);

                        if (resultParseXML.size() == 1) {
                            CAuserID = resultParseXML.get(0).get(0);
                            result.put("UserId", CAuserID);

                            setLog("Parsing result search user " + CAuserID + " complite");

                            if (CAuserID.isEmpty()) {
                                setErrorLog("Error: Not parsing result find CA user");
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;
                            }

                            //Получить список сертификатов пользователя УЦ
                            port.getCertificateRecordList(folderID, "V", "", Boolean.TRUE, "UserId", CAuserID, 8, 1, 100, Boolean.TRUE, getCertificateRecordListResult, resultCount, totalRowCount);
                            if (resultCount.value > 0) {
                                setLog("Complite find list of certificates, userID " + CAuserID + " in CA");

                                //Парсинг результата поиска сертификатов пользователя УЦ
                                resultParseXML = parseXML(getCertificateRecordListResult.value, parseAttrsCert);

                                //Сформировать запрос на отзыв сертификатов пользователя
                                for (int i = 0; i < resultParseXML.size(); i++) {
                                    RevRequest = "SN=" + resultParseXML.get(i).get(0) + ",TP=" + resultParseXML.get(i).get(1) + ",RR=" + RevocationReason + "";

                                    //Подписать запрос
                                    resultSignRequestCABase64 = signRequestCA(RevRequest, privateKey, cert);

                                    //Отзыв сертификатов пользователя
                                    resultSubmitAndAcceptRevReques = port.submitAndAcceptRevRequest(resultSignRequestCABase64, "СУИД", Boolean.TRUE);

                                    //Сохраняем сертификаты на отзыв
                                    revCertList = revCertList + "RevID: " + resultSubmitAndAcceptRevReques + ",CertSerialNum: " + resultParseXML.get(i).get(0) + ";";
                                    setLog("Revoked certificate SR-" + resultParseXML.get(i).get(0) + " to user " + Email);

                                }

                                setLog("Revoke user certificates is complite, userID " + UserID + " in CA");
                                result.put("CertList", revCertList);
                            } else {
                                setLog("Not found active certificates in CA for user " + Email);
                            }

                            /*
                         //Парсинг результата поиска пользователя УЦ
                        parseAttrsUsr.clear();
                        parseAttrsUsr.add("Status");
                        resultParseXML = objITSKCASoap.parseXML(ResultFindUserCA.get("getUserRecordListResult").toString(),parseAttrsUsr);

                        if (resultParseXML.get(0).trim().equals("A"))
                         {
                            //Блокируем пользователя УЦ
                             port.deactivateUser(CAuserID, Boolean.TRUE);
                         }
                        setLog("MDM: Deactivate User CA is complite, userID "+ UserID +" in CA");
                             */
                            objResponseITSKCASoap.result = "SUCCESS";
                            objResponseITSKCASoap.propertyMap = result;
                            return objResponseITSKCASoap;

                        } else {
                            //Ошибка, не найден ни один пользователь               
                            setErrorLog("Error: Parsing result found more than one CA user");
                            objResponseITSKCASoap.propertyMap = result;
                            return objResponseITSKCASoap;
                        }
                        //Получить список сертификатов пользователя УЦ

                    } else {
                        //Добавить в парсер поле статуса пользователя
                        parseAttrsUsr.add("Status");

                        //Парсинг результата поиска пользователя УЦ
                        resultParseXML = parseXML(ResultFindUserCA.get("getUserRecordListResult").toString(), parseAttrsUsr);

                        if (resultParseXML.size() > 0) {
                            for (int i = 0; i < resultParseXML.size(); i++) {
                                if (resultParseXML.get(i).get(1).equals("A")) {
                                    j = j + 1;
                                    CAuserID = resultParseXML.get(i).get(0);
                                    result.put("UserId", CAuserID);
                                }

                            }
                            if (j == 1) {
                                setLog("Find user " + CAuserID + " in CA");

                                //Получить список сертификатов пользователя УЦ
                                port.getCertificateRecordList(folderID, "V", "", Boolean.TRUE, "UserId", CAuserID, 8, 1, 100, Boolean.TRUE, getCertificateRecordListResult, resultCount, totalRowCount);
                                if (resultCount.value > 0) {
                                    setLog("Complite find list of certificates, userID " + CAuserID + " in CA");
                                    //Сформировать запрос на отзыв сертификатов пользователя
                                    for (int i = 0; i < resultParseXML.size(); i++) {
                                        RevRequest = "SN=" + resultParseXML.get(i).get(0) + ",TP=" + resultParseXML.get(i).get(1) + ",RR=" + RevocationReason + "";

                                        //Подписать запрос
                                        resultSignRequestCABase64 = signRequestCA(RevRequest, privateKey, cert);

                                        //Отзыв сертификатов пользователя
                                        resultSubmitAndAcceptRevReques = port.submitAndAcceptRevRequest(resultSignRequestCABase64, "СУИД", Boolean.TRUE);

                                        revCertList = revCertList + "RevID: " + resultSubmitAndAcceptRevReques + ",CertSerialNum: " + resultParseXML.get(i).get(0) + ";";
                                        setLog("Revoked certificate SR-" + resultParseXML.get(i).get(0) + " to user " + Email);

                                    }

                                    setLog("Revoke user certificates is complite, userID " + CAuserID + " in CA");
                                    result.put("CertList", revCertList);
                                } else {
                                    setLog("Not found active certificates in CA for user " + Email);
                                }

                                /*
                         //Парсинг результата поиска пользователя УЦ
                        
                        parseAttrsUsr.clear();
                        parseAttrsUsr.add("Status");
                        resultParseXML = objITSKCASoap.parseXML(ResultFindUserCA.get("getUserRecordListResult").toString(),parseAttrsUsr);
                        if (resultParseXML.get(0).trim().equals("A"))
                         {
                            //Блокируем пользователя УЦ
                             port.deactivateUser(CAuserID, Boolean.TRUE);
                         }
                        setLog("MDM: Deactivate User CA is complite, userID "+ UserID +" in CA");
                                 */
                                objResponseITSKCASoap.result = "SUCCESS";
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;

                            } else {
                                //Ошибка, найдено больше одного пользователя               
                                setErrorLog("Error: Parsing result found more than one CA user");
                                objResponseITSKCASoap.propertyMap = result;
                                return objResponseITSKCASoap;
                            }
                        }
                    }
                } else {
                    setErrorLog("Error: Not find CA user, filter:" + CAOIDemail + "->" + Email);
                    objResponseITSKCASoap.propertyMap = result;
                    return objResponseITSKCASoap;
                }
            }

            return objResponseITSKCASoap;

        } catch (Exception e) {
            String ss = getStackTrace(e);
            setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error initialization RevokeUser", e);
            objResponseITSKCASoap.propertyMap = result;
            return objResponseITSKCASoap;
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));          

        }
    }

    public String signRequestCA(String StrRequest, PrivateKey privateKey, X509Certificate cert) throws Exception {
        //Подпись как PKCS7 с использованием CMS
        byte[] data = StrRequest.getBytes("UTF-16LE");
        final PrivateKey[] keys = new PrivateKey[1];
        keys[0] = privateKey;
        final Certificate[] certs = new Certificate[1];
        certs[0] = cert;
        String DIGEST_OID = JCP.GOST_DIGEST_OID;
        String SIGN_OID = JCP.GOST_EL_KEY_OID;
        boolean isCMS = true;

        try {
            //byte[] resBase64String = null;
            final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(data);
            final ContentInfo all = new ContentInfo();

            byte[] res = createCMS(data, keys, certs, false);
            res = createCMS(res, keys, certs, false);
            ru.CryptoPro.JCP.tools.Encoder encoder = new ru.CryptoPro.JCP.tools.Encoder();
            String resBase64String = encoder.encode(res);
//System.out.println(resBase64String);
            return resBase64String;
        } catch (Exception e) {
            String ss = getStackTrace(e);
            setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error signed request CA:" + StrRequest, e);
            return "";
        }
    }

    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static byte[] createCMS(byte[] data, PrivateKey[] keys,
            Certificate[] certs, boolean detached)
            throws Exception {
        return createCMSEx(data, keys, certs, detached,
                JCP.GOST_DIGEST_OID, JCP.GOST_EL_KEY_OID, JCP.GOST_EL_SIGN_NAME,
                JCP.PROVIDER_NAME);
    }

    public static byte[] createCMSEx(byte[] data, PrivateKey[] keys,
            Certificate[] certs, boolean detached, String digestOid,
            String signOid, String signAlg, String providerName) throws Exception {

        //create CMS
        String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";
        String STR_CMS_OID_DATA = "1.2.840.113549.1.7.1";
        final ContentInfo all = new ContentInfo();
        all.contentType = new Asn1ObjectIdentifier(
                new OID(STR_CMS_OID_SIGNED).value);

        final SignedData cms = new SignedData();
        all.content = cms;
        cms.version = new CMSVersion(1);

        // digest
        cms.digestAlgorithms = new DigestAlgorithmIdentifiers(1);
        final DigestAlgorithmIdentifier a = new DigestAlgorithmIdentifier(
                new OID(digestOid).value);
        a.parameters = new Asn1Null();
        cms.digestAlgorithms.elements[0] = a;

        if (detached) {
            cms.encapContentInfo = new EncapsulatedContentInfo(
                    new Asn1ObjectIdentifier(
                            new OID(STR_CMS_OID_DATA).value), null);
        } // if
        else {
            cms.encapContentInfo
                    = new EncapsulatedContentInfo(new Asn1ObjectIdentifier(
                            new OID(STR_CMS_OID_DATA).value),
                            new Asn1OctetString(data));
        } // else

        // certificates
        final int nCerts = certs.length;
        cms.certificates = new CertificateSet(nCerts);
        cms.certificates.elements = new CertificateChoices[nCerts];

        for (int i = 0; i < cms.certificates.elements.length; i++) {

            final ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate certificate
                    = new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate();
            final Asn1BerDecodeBuffer decodeBuffer
                    = new Asn1BerDecodeBuffer(certs[i].getEncoded());
            certificate.decode(decodeBuffer);

            cms.certificates.elements[i] = new CertificateChoices();
            cms.certificates.elements[i].set_certificate(certificate);

        } // for

        // Signature.getInstance
        final Signature signature = Signature.getInstance(signAlg, providerName);
        byte[] sign;

        // signer infos
        final int nSign = keys.length;
        cms.signerInfos = new SignerInfos(nSign);
        for (int i = 0; i < cms.signerInfos.elements.length; i++) {

            signature.initSign(keys[i]);
            signature.update(data);
            sign = signature.sign();

            cms.signerInfos.elements[i] = new SignerInfo();
            cms.signerInfos.elements[i].version = new CMSVersion(1);
            cms.signerInfos.elements[i].sid = new SignerIdentifier();

            final byte[] encodedName = ((X509Certificate) certs[i])
                    .getIssuerX500Principal().getEncoded();
            final Asn1BerDecodeBuffer nameBuf = new Asn1BerDecodeBuffer(encodedName);
            final ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Name name = new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Name();
            name.decode(nameBuf);

            final CertificateSerialNumber num = new CertificateSerialNumber(
                    ((X509Certificate) certs[i]).getSerialNumber());
            cms.signerInfos.elements[i].sid.set_issuerAndSerialNumber(
                    new IssuerAndSerialNumber(name, num));
            cms.signerInfos.elements[i].digestAlgorithm
                    = new DigestAlgorithmIdentifier(new OID(digestOid).value);
            cms.signerInfos.elements[i].digestAlgorithm.parameters = new Asn1Null();
            cms.signerInfos.elements[i].signatureAlgorithm
                    = new SignatureAlgorithmIdentifier(new OID(signOid).value);
            cms.signerInfos.elements[i].signatureAlgorithm.parameters
                    = new Asn1Null();
            cms.signerInfos.elements[i].signature = new SignatureValue(sign);
        }

        // encode
        final Asn1BerEncodeBuffer asnBuf = new Asn1BerEncodeBuffer();
        all.encode(asnBuf, true);

        //System.out.println(asnBuf.getMsgCopy());
        //System.out.println(data.toString());
        //System.out.println(asnBuf.toString());
        return asnBuf.getMsgCopy();
    }

////Требуется в том случае если сообщение уже в формате CMS
    public static byte[] signCMS(byte[] buffer, PrivateKey[] keys,
            Certificate[] certs, byte[] data) throws Exception {
        return signCMSEx(buffer, keys, certs, data, JCP.GOST_DIGEST_OID, JCP.GOST_EL_KEY_OID, JCP.GOST_EL_SIGN_NAME, JCP.PROVIDER_NAME);
    }

    public static byte[] signCMSEx(byte[] buffer, PrivateKey[] keys,
            Certificate[] certs, byte[] data, String digestOidValue,
            String signOidValue, String signAlg, String providerName)
            throws Exception {

        int i;
        String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";
        String STR_CMS_OID_DATA = "1.2.840.113549.1.7.1";
        final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);
        final ContentInfo all = new ContentInfo();
        all.decode(asnBuf);

        if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
            throw new Exception("Not supported");
        } // if

        final SignedData cms = (SignedData) all.content;
        if (cms.version.value != 1) {
            throw new Exception("Incorrect version");
        } // if

        if (!new OID(STR_CMS_OID_DATA)
                .eq(cms.encapContentInfo.eContentType.value)) {
            throw new Exception("Nested not supported");
        } // if

        final byte[] text;
        if (cms.encapContentInfo.eContent != null) {
            text = cms.encapContentInfo.eContent.value;
        } // if
        else if (data != null) {
            text = data;
        } // else
        else {
            throw new Exception("No content");
        } // else

        OID digestOid = null;
        final DigestAlgorithmIdentifier a = new DigestAlgorithmIdentifier(
                new OID(digestOidValue).value);

        for (i = 0; i < cms.digestAlgorithms.elements.length; i++) {
            if (cms.digestAlgorithms.elements[i].algorithm.equals(a.algorithm)) {
                digestOid = new OID(cms.digestAlgorithms.elements[i].algorithm.value);
                break;
            } // if
        } // for

        if (digestOid == null) {
            throw new Exception("Unknown digest");
        } // if

        final CertificateChoices[] choices
                = new CertificateChoices[cms.certificates.elements.length];
        for (i = 0; i < cms.certificates.elements.length; i++) {
            choices[i] = cms.certificates.elements[i];
        }  // for

        final int nCerts = certs.length + choices.length;
        cms.certificates = new CertificateSet(nCerts);
        cms.certificates.elements = new CertificateChoices[nCerts];
        for (i = 0; i < choices.length; i++) {
            cms.certificates.elements[i] = choices[i];
        } // for

        for (i = 0; i < cms.certificates.elements.length - choices.length; i++) {

            final ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate certificate
                    = new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate();
            final Asn1BerDecodeBuffer decodeBuffer
                    = new Asn1BerDecodeBuffer(certs[i].getEncoded());
            certificate.decode(decodeBuffer);
            cms.certificates.elements[i + choices.length]
                    = new CertificateChoices();
            cms.certificates.elements[i + choices.length]
                    .set_certificate(certificate);

        } // for 
        // Signature.getInstance
        final Signature signature = Signature.getInstance(signAlg, providerName);
        byte[] sign;

        // signer infos
        final SignerInfo[] infos = new SignerInfo[cms.signerInfos.elements.length];
        for (i = 0; i < cms.signerInfos.elements.length; i++) {
            infos[i] = cms.signerInfos.elements[i];
        } // for

        final int nsign = keys.length + infos.length;
        cms.signerInfos = new SignerInfos(nsign);
        for (i = 0; i < infos.length; i++) {
            cms.signerInfos.elements[i] = infos[i];
        } // for

        for (i = 0; i < cms.signerInfos.elements.length - infos.length; i++) {

            signature.initSign(keys[i]);
            signature.update(text);
            sign = signature.sign();

            cms.signerInfos.elements[i + infos.length] = new SignerInfo();
            cms.signerInfos.elements[i + infos.length].version = new CMSVersion(1);
            cms.signerInfos.elements[i + infos.length].sid = new SignerIdentifier();

            final byte[] encodedName = ((X509Certificate) certs[i])
                    .getIssuerX500Principal().getEncoded();

            final Asn1BerDecodeBuffer nameBuf = new Asn1BerDecodeBuffer(encodedName);
            final ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Name name = new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Name();
            name.decode(nameBuf);

            final CertificateSerialNumber num = new CertificateSerialNumber(
                    ((X509Certificate) certs[i]).getSerialNumber());
            cms.signerInfos.elements[i + infos.length].sid
                    .set_issuerAndSerialNumber(
                            new IssuerAndSerialNumber(name, num));
            cms.signerInfos.elements[i + infos.length].digestAlgorithm
                    = new DigestAlgorithmIdentifier(new OID(digestOidValue).value);
            cms.signerInfos.elements[i + infos.length].digestAlgorithm.parameters
                    = new Asn1Null();
            cms.signerInfos.elements[i + infos.length].signatureAlgorithm
                    = new SignatureAlgorithmIdentifier(new OID(signOidValue).value);
            cms.signerInfos.elements[i + infos.length].signatureAlgorithm.parameters = new Asn1Null();
            cms.signerInfos.elements[i + infos.length].signature
                    = new SignatureValue(sign);
        }

        // encode
        final Asn1BerEncodeBuffer asn1Buf = new Asn1BerEncodeBuffer();
        all.encode(asn1Buf, true);
        return asn1Buf.getMsgCopy();
    }

    public List<List<String>> parseXML(String XMLString, List<String> Element) throws Exception {

        List<String> resultStr = new ArrayList<String>();
        List<List<String>> result = new ArrayList<>();
        List<String> listStrElems = new ArrayList<String>();
        InputSource streamXML = new InputSource(new StringReader(XMLString));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String strElems = "";
        try {
            org.w3c.dom.Document document = builder.parse(streamXML);

            NodeList nodes = document.getElementsByTagName("rs:data");

            org.w3c.dom.Element element1 = (org.w3c.dom.Element) nodes.item(0);
            NodeList movieList = element1.getElementsByTagName("z:row");
            org.w3c.dom.Element element2 = null;

            for (int i = 0; i < movieList.getLength(); i++) {

                element2 = (org.w3c.dom.Element) movieList.item(i);

                for (int j = 0; j < Element.size(); j++) {
                    if (j + 1 != Element.size()) {

                        strElems = strElems + element2.getAttributes().getNamedItem(Element.get(j)).getNodeValue().toString() + "||";

                    } else {

                        strElems = strElems + element2.getAttributes().getNamedItem(Element.get(j)).getNodeValue().toString();

                    }
                }

                listStrElems.add(strElems);
                strElems = "";

            }

            for (int i = 0; i < listStrElems.size(); i++) {
                resultStr = Arrays.asList(listStrElems.get(i).split("\\|\\|"));
                result.add(i, resultStr);

            }

            return result;

        } catch (Exception e) {
            String ss = getStackTrace(e);
            setErrorLog(ss);
            //LOGGER.log(Level.SEVERE, "Error parser result find user CA:", e);
            return result;
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw)); 
        }

    }

    void setErrorLog(String logString) {

        //Utils.setProcessTaskNote(taskId, taskNote);
        //notifyAdminAboutError(logString);
        String ss = "";
        ss = GetShortStackTrace(logString, 20);
        objResponseITSKCASoap.log = objResponseITSKCASoap.log + "<" + new Date() + "> " + "<" + this.getClass() + "> " + "<MDM> " + "<ERROR> " + ss + "\n";
        //LogStr = LogStr + "<" + new Date() + "> " + "<MDM> " + "<ERROR> " + logString + "\n";
        System.out.println(logString);
        notifyAboutAnyExeption(logString);
        LOGGER.log(Level.SEVERE, logString);
    }

    private void setLog(String logString) {

        objResponseITSKCASoap.log = objResponseITSKCASoap.log + "<" + new Date() + "> " + "<" + this.getClass() + "> " + "<MDM> " + "<LOG> " + logString + "\n";
        //LogStr = LogStr + "<" + new Date() + "> " + "<MDM> " + "<LOG> " + logString + "\n";  
        System.out.println(objResponseITSKCASoap.log);
        LOGGER.finest(objResponseITSKCASoap.log);
    }

    private String GetShortStackTrace(String st, int line) {
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
