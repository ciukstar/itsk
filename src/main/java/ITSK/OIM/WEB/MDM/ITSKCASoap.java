package ITSK.OIM.WEB.MDM;

import com.objsys.asn1j.runtime.Asn1BerDecodeBuffer;
import com.objsys.asn1j.runtime.Asn1BerEncodeBuffer;
import com.objsys.asn1j.runtime.Asn1Null;
import com.objsys.asn1j.runtime.Asn1ObjectIdentifier;
import com.objsys.asn1j.runtime.Asn1OctetString;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.Holder;
import ru.CryptoPro.JCP.JCP;
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

/**
 *
 * @author Birillo
 */
public class ITSKCASoap {

    private final CredLoader credLoader;
    private final UC uc;
    private final LogFormater logFormatter;
    private final XMLParser parser;

    public ITSKCASoap(CredLoader credLoader, UC uc, LogFormater logFormatter, XMLParser parser) {
        this.credLoader = credLoader;
        this.uc = uc;
        this.logFormatter = logFormatter;
        this.parser = parser;
    }

    public ResponseITSKCASoap createUser(String email, String ADLogin, String fio, Map<String, Object> params) {

        ResponseITSKCASoap response = new ResponseITSKCASoap();
        response.appendLog(
                logFormatter.log("Begin find user " + email + " in CA", this.getClass())
        );

        //Инициализировать подключение к УЦ
        Either<? extends Throwable, RegAuthLegacyContract> port = uc.initializeCA(params);
        if (port.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(port.getLeft()), this.getClass()));
        }

        final UserAccountInfo userAccountInfo = UserAccountInfo.builder()
                .caOidUpn(Option.ofNullable((String) params.get("UPN")).orElseGet("1.3.6.1.4.1.311.20.2.3"))
                .adLogin(ADLogin)
                .caOidEmail(Option.ofNullable((String) params.get("EMAIL")).orElseGet("1.2.840.113549.1.9.1"))
                .email(email)
                .caOidCn(Option.ofNullable((String) params.get("CN")).orElseGet("2.5.4.3"))
                .fio(fio)
                .folderId(Option.ofNullable((String) params.get("CAFolderID")).orElseGet("c5619331-7426-e611-80ed-00505681c485"))
                .build();

        //Поск пользователя УЦ
        final Either<? extends Throwable, Map<String, Object>> resultFindUserCA
                = uc.findUcUser(params, port.getRight(), userAccountInfo);

        if (resultFindUserCA.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(resultFindUserCA.getLeft()), this.getClass()));
            response.appendLog(logFormatter.logError("Error: Not Found CA Users, filter- " + userAccountInfo.getCAOIDemail() + "->" + userAccountInfo.getEmail(), this.getClass()));
            response.setPropertyMap(emptyResult());
            return response;
        }

        if ((int) resultFindUserCA.getRight().get("resultCount") > 0) {
            if (checkoutUser(resultFindUserCA.getRight().get("getUserRecordListResult").toString(), response, email, port)) {
                return response;
            }

        }

        if ((int) resultFindUserCA.getRight().get("resultCount") <= 0) {
            response.appendLog(logFormatter.log("User not found, Email: " + email, this.getClass()));

            final char[] charPwd = Option.ofNullable((String) params.get("PasswordKeyStoreJCP"))
                    .orElseGet("Qwerty123").toCharArray();

            if (registerUser(response, userAccountInfo, charPwd, port)) {
                return response;
            }
        }

        response.setResult("SUCCESS");
        return response;

    }

    boolean checkoutUser(String getUserRecordListResult, ResponseITSKCASoap response, String email, Either<? extends Throwable, RegAuthLegacyContract> port) {
        //Дополнитьльно для парсинга добавляем статус пользователя
        //Парсинг результата поиска пользователя УЦ
        final Either<? extends Throwable, List<List<String>>> resultParseXML = parser.parseXML(
                getUserRecordListResult,
                Arrays.asList("UserId", "Status")
        );
        if (resultParseXML.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(resultParseXML.getLeft()), this.getClass()));
        } else {
            response.appendLog(logFormatter.log("Parsing result search user " + email + " complite", this.getClass()));
        }
        if (resultParseXML.getRight().size() == 1
                && resultParseXML.getRight().get(0).get(0).isEmpty()) {
            response.appendLog(logFormatter.logError("Error: Not parsing result find CA user", this.getClass()));
            response.addEntry("UserId", "");
            return true;
        }
        if (resultParseXML.getRight().size() == 1
                && !resultParseXML.getRight().get(0).get(1).equals("A")) {
            response.appendLog(logFormatter.logError("Error: user" + email + "is not active status", this.getClass()));
            return true;
        }
        final String usrid = resultParseXML.getRight().get(0).get(0);
        response.appendLog(logFormatter.log("User found, User ID: " + usrid, this.getClass()));
        final Holder<String> webLogin = new Holder<>();
        final Holder<String> webPassword = new Holder<>();
        webLogin.value = "";
        webPassword.value = "";
        //Создать маркер временного доступа для пользователя
        final Either<? extends Throwable, HashMap> userToken = createTokenForUser(port.getRight(), usrid, webLogin, webPassword, response);
        if (userToken.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(userToken.getLeft()), this.getClass()));
        }
        if (resultParseXML.getRight().size() == 1
                && resultParseXML.getRight().get(0).get(1).equals("A")
                && userToken.isEmpty()) {
            response.addEntry("UserId", usrid);
            return true;
        }
        if (resultParseXML.getRight().size() == 1
                && resultParseXML.getRight().get(0).get(1).equals("A")
                && !userToken.isEmpty()) {
            response.appendLog(logFormatter.log("Create Marker CA for user, User ID: " + usrid + " Complite", this.getClass()));
            response.setOutcome("SUCCESS");
            response.addEntries(userToken.getRight());
            return true;
        }
        Option<String> userId = findFirstActiveUser(resultParseXML);
        if (resultParseXML.getRight().size() > 1
                && userId.isEmpty()) {
            //Ошибка, найдено больше одного пользователя
            response.appendLog(logFormatter.logError("Error: In CA found more than one active user for email" + email, this.getClass()));
            return true;
        }
        response.appendLog(logFormatter.log("Found one active user CA for user email: " + email + "User ID:" + userId, this.getClass()));
        //Создать маркер временного доступа для пользователя
        final Either<? extends Throwable, HashMap> resultCreateTokenForUser = createTokenForUser(port.getRight(), userId.get(), webLogin, webPassword, response);
        if (resultCreateTokenForUser.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(resultCreateTokenForUser.getLeft()), this.getClass()));
        }
        if (resultParseXML.getRight().size() > 1
                && userId.isPresent()
                && resultCreateTokenForUser.isEmpty()) {
            response.addEntry("UserId", userId.get());
            return true;
        }
        if (resultParseXML.getRight().size() > 1
                && userId.isPresent()
                && !resultCreateTokenForUser.isEmpty()) {
            response.appendLog(logFormatter.log("Create Marker CA for user, User ID: " + userId.get() + " Complite", this.getClass()));
            response.setOutcome("SUCCESS");
            response.addEntries(resultCreateTokenForUser.getRight());
            return true;
        }
        return false;
    }

    boolean registerUser(
            ResponseITSKCASoap response,
            UserAccountInfo userAccount,
            final char[] charPwd,
            Either<? extends Throwable, RegAuthLegacyContract> port
    ) {

        Request request = new RequestBuilder().buildRegisterUserRequest(userAccount);
        response.appendLog(logFormatter.log("Request for create user CA complite, Request: \n" + request.contents(), this.getClass()));
        final Either<? extends Throwable, Credentials> cred = credLoader.loadCredentials(charPwd);
        if (cred.isEmpty()) {
            response.appendLog(logFormatter.logError("Error while loding credentials", this.getClass()));
            return true;
        }
        //Подписать запрос
        final Either<? extends Throwable, String> resultSignRequestCABase64 = signRequestCA(request.contents(), cred.getRight());
        if (resultSignRequestCABase64.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(resultSignRequestCABase64.getLeft()), this.getClass()));
            return true;
        }
        response.appendLog(logFormatter.log("Request is signed", this.getClass()));
        //Выполнить запрос на регистрацию пользователя УЦ
        final String keyPhrase = "key";
        final String description = "СУИД:Предоставление доступа в УЦ";
        final String managerComment = "СУИД:Предоставление доступа в УЦ";
        final String resultSubmitAndAcceptRegRequest = port.getRight()
                .submitAndAcceptRegRequest(userAccount.getFolderId(), resultSignRequestCABase64.getRight(), userAccount.getEmail(), keyPhrase, description, managerComment, Boolean.FALSE);
        //Получить описание запроса на регистрацию + получить UserID
        final String resultgetRegRequestRecord = port.getRight().getRegRequestRecord(resultSubmitAndAcceptRegRequest, "");
        //Парсинг результата поиска пользователя УЦ
        final List<String> parseAttrs = new ArrayList<>();
        parseAttrs.add("UserId");
        final Either<? extends Throwable, List<List<String>>> resultParseXML = parser.parseXML(resultgetRegRequestRecord, parseAttrs);
        if (!resultSignRequestCABase64.isEmpty()
                && resultParseXML.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(resultParseXML.getLeft()), this.getClass()));
        }
        if (!resultSignRequestCABase64.isEmpty()
                && resultParseXML.getRight().size() > 1) {
            //Ошибка, найдено больше одного пользователя
            response.appendLog(logFormatter.logError("Error: Parsing result found more than one CA user", this.getClass()));
            //Сохранить результат submitAndAcceptRegRequest (номер запроса)
            response.addEntry("RegID", resultSubmitAndAcceptRegRequest);
            return true;
        }
        response.appendLog(logFormatter.log("User register in CA, RegID: " + resultSubmitAndAcceptRegRequest + " ,UserID: " + resultParseXML.getRight().get(0).get(0), this.getClass()));
        if (!resultSignRequestCABase64.isEmpty()
                && resultParseXML.getRight().size() == 1
                && resultParseXML.getRight().get(0).get(0).isEmpty()) {
            response.appendLog(logFormatter.logError("Error in the create new CA user, Not parsing result find userID for RegRequest", this.getClass()));
            response.addEntry("UserId", resultParseXML.getRight().get(0).get(0));
            return true;
        }
        final Holder<String> webLogin = new Holder<>();
        final Holder<String> webPassword = new Holder<>();
        webLogin.value = "";
        webPassword.value = "";
        //Создать маркер временного доступа для пользователя
        final Either<? extends Throwable, HashMap> resultCreateTokenForUser = createTokenForUser(port.getRight(), resultParseXML.getRight().get(0).get(0), webLogin, webPassword, response);
        if (resultCreateTokenForUser.isEmpty()) {
            response.appendLog(logFormatter.logError(getStackTrace(resultCreateTokenForUser.getLeft()), this.getClass()));
            return true;
        }
        if (!resultSignRequestCABase64.isEmpty()
                && resultParseXML.getRight().size() == 1
                && !resultParseXML.getRight().get(0).get(0).isEmpty()
                && resultCreateTokenForUser.isEmpty()) {
            return true;
        }
        if (!resultSignRequestCABase64.isEmpty()
                && resultParseXML.getRight().size() == 1
                && !resultParseXML.getRight().get(0).get(0).isEmpty()
                && !resultCreateTokenForUser.isEmpty()) {
            response.appendLog(logFormatter.log("Create Token for user: " + resultParseXML.getRight().get(0).get(0) + " Email: " + userAccount.getEmail(), this.getClass()));
            response.setResult("SUCCESS");
            response.addEntries(resultCreateTokenForUser.getRight());
            response.addEntry("UserId", resultParseXML.getRight().get(0).get(0));
            return true;
        }
        return false;
    }

    Option<String> findFirstActiveUser(final Either<? extends Throwable, List<List<String>>> resultParseXML) {
        int j = 0;
        for (int i = 0; i < resultParseXML.getRight().size(); i++) {
            if (resultParseXML.getRight().get(i).get(1).equals("A")) {
                j = j + 1;
                return Option.of(resultParseXML.getRight().get(i).get(0));
            }
        }
        return Option.empty();
    }

    public Either<? extends Throwable, HashMap> createTokenForUser(
            RegAuthLegacyContract port,
            String userID,
            Holder<String> webLogin,
            Holder<String> webPassword,
            ResponseITSKCASoap response
    ) {

        HashMap result = new HashMap();
        try {
            port.createTokenForUser(userID, webLogin, webPassword);
            result.put("webLogin", webLogin.value);
            result.put("webPassword", webPassword.value);
            return Either.right(result);
        } catch (Exception e) {
            return Either.left(e);
        }
    }

    public ResponseITSKCASoap revokeUser(String Email, String UserID, int RevocationReason, HashMap Params) throws Exception {
        ResponseITSKCASoap response = new ResponseITSKCASoap();

        HashMap result = new HashMap();

        try {

            Either<? extends Throwable, RegAuthLegacyContract> port = null;

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
            Holder<String> getCertificateRecordListResult = new Holder<>();
            Holder<Integer> resultCount = new Holder<>();
            Holder<Integer> totalRowCount = new Holder<>();
            String resultSubmitAndAcceptRevReques = "";
            Either<? extends Throwable, String> resultSignRequestCABase64;
            Either<? extends Throwable, Map<String, Object>> resultFindUserCA;
            String CAuserID = "";
            //Email = "Andrianov.IA@gazprom-neft.ru";//"Moskvichev.IA@Gazprom-Neft.RU";
            Either<? extends Throwable, List<List<String>>> resultParseXML;
            List<String> parseAttrsCert = new ArrayList<>();
            List<String> parseAttrsUsr = new ArrayList<>();
            parseAttrsCert.add("SerialNumber");
            parseAttrsCert.add("Thumbprint");
            parseAttrsUsr.add("UserId");
            String RevRequest = "";
            List<String> certStr = null;
            List<List<String>> certList = new ArrayList<>();
            List<List<String>> userList = new ArrayList<>();

            //Инициализировать подключение к УЦ
            port = uc.initializeCA(Params);

            //Загрузить KeyStore
            final KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
            keyStore.load(null, null);
            final Credentials credentials = new Credentials(
                    (PrivateKey) keyStore.getKey("CA", charPwd),
                    (X509Certificate) keyStore.getCertificate("CA")
            );

            if (!UserID.isEmpty()) {
                result.put("UserId", UserID);

                //Поск пользователя УЦ
                resultFindUserCA = uc.findUserCA(folderID, "UserId", UserID, 8, port.getRight());
                if (resultFindUserCA.isEmpty()) {
                    response.appendLog(logFormatter.logError(getStackTrace(resultFindUserCA.getLeft()), this.getClass()));
                    FlagFindEmail = 1;

                } else if ((int) resultFindUserCA.getRight().get("resultCount") > 0) {
                    response.appendLog(logFormatter.log("Find user " + UserID + " in CA", this.getClass()));

                    if (resultFindUserCA.getRight().get("resultCount").equals(1)) {
                        //Получить список сертификатов пользователя УЦ
                        port.getRight().getCertificateRecordList(folderID, "V", "", Boolean.TRUE, "UserId", UserID, 8, 1, 100, Boolean.TRUE, getCertificateRecordListResult, resultCount, totalRowCount);
                        if (resultCount.value > 0) {
                            response.appendLog(logFormatter.log("complite find list of certificates CA, userID " + UserID, this.getClass()));
                            //Парсинг результата поиска сертификатов пользователя УЦ
                            resultParseXML = parser.parseXML(getCertificateRecordListResult.value, parseAttrsCert);
                            if (resultParseXML.isEmpty()) {
                                response.appendLog(logFormatter.logError(getStackTrace(resultParseXML.getLeft()), this.getClass()));
                            }

                            //Сформировать запрос на отзыв сертификатов пользователя
                            for (int i = 0; i < resultParseXML.getRight().size(); i++) {
                                RevRequest = "SN=" + resultParseXML.getRight().get(i).get(0) + ",TP=" + resultParseXML.getRight().get(i).get(1) + ",RR=" + RevocationReason + "";

                                //Подписать запрос
                                resultSignRequestCABase64 = signRequestCA(RevRequest, credentials);

                                //Отзыв сертификатов пользователя
                                resultSubmitAndAcceptRevReques = port.getRight().submitAndAcceptRevRequest(resultSignRequestCABase64.getRight(), "СУИД", Boolean.TRUE);

                                //Сохраняем сертификаты на отзыв
                                revCertList = revCertList + "RevID: " + resultSubmitAndAcceptRevReques + ",CertSerialNum: " + resultParseXML.getRight().get(i).get(0) + ";";
                                response.appendLog(logFormatter.log("Revoked certificate SR-" + resultParseXML.getRight().get(i).get(0) + " to user " + Email, this.getClass()));

                            }

                            response.appendLog(logFormatter.log("Revoke user certificates is complite, userID " + UserID + " in CA", this.getClass()));
                            result.put("CertList", revCertList);
                        } else {
                            response.appendLog(logFormatter.log("Not found active certificates in CA for user " + Email, this.getClass()));
                        }
                        response.setResult("SUCCESS");
                        response.setPropertyMap(result);
                        return response;

                    }
                }
            } else {
                FlagFindEmail = 1;
            }

            if (FlagFindEmail == 1) {
                resultFindUserCA = uc.findUserCA(folderID, "OID." + CAOIDemail, Email.trim(), 8, port.getRight());
                if (resultFindUserCA.isEmpty()) {
                    response.appendLog(logFormatter.logError(getStackTrace(resultFindUserCA.getLeft()), this.getClass()));
                    response.appendLog(logFormatter.logError("Error in process find CA user, filter:" + CAOIDemail + "->" + Email, this.getClass()));
                    response.setPropertyMap(result);
                    return response;

                } else if ((int) resultFindUserCA.getRight().get("resultCount") > 0) {

                    if (resultFindUserCA.getRight().get("resultCount").equals(1)) {
                        response.appendLog(logFormatter.log("Find user for email" + Email + " in CA", this.getClass()));
                        //Парсинг результата поиска пользователя УЦ
                        resultParseXML = parser.parseXML(resultFindUserCA.getRight().get("getUserRecordListResult").toString(), parseAttrsUsr);

                        if (resultParseXML.isEmpty()) {
                            response.appendLog(logFormatter.logError(getStackTrace(resultParseXML.getLeft()), this.getClass()));
                        }

                        if (resultParseXML.getRight().size() == 1) {
                            CAuserID = resultParseXML.getRight().get(0).get(0);
                            result.put("UserId", CAuserID);

                            response.appendLog(logFormatter.log("Parsing result search user " + CAuserID + " complite", this.getClass()));

                            if (CAuserID.isEmpty()) {
                                response.appendLog(logFormatter.logError("Error: Not parsing result find CA user", this.getClass()));
                                response.setPropertyMap(result);
                                return response;
                            }

                            //Получить список сертификатов пользователя УЦ
                            port.getRight().getCertificateRecordList(folderID, "V", "", Boolean.TRUE, "UserId", CAuserID, 8, 1, 100, Boolean.TRUE, getCertificateRecordListResult, resultCount, totalRowCount);
                            if (resultCount.value > 0) {
                                response.appendLog(logFormatter.log("Complite find list of certificates, userID " + CAuserID + " in CA", this.getClass()));

                                //Парсинг результата поиска сертификатов пользователя УЦ
                                resultParseXML = parser.parseXML(getCertificateRecordListResult.value, parseAttrsCert);
                                if (resultParseXML.isEmpty()) {
                                    response.appendLog(logFormatter.logError(getStackTrace(resultParseXML.getLeft()), this.getClass()));
                                }
                                //Сформировать запрос на отзыв сертификатов пользователя
                                for (int i = 0; i < resultParseXML.getRight().size(); i++) {
                                    RevRequest = "SN=" + resultParseXML.getRight().get(i).get(0) + ",TP=" + resultParseXML.getRight().get(i).get(1) + ",RR=" + RevocationReason + "";

                                    //Подписать запрос
                                    resultSignRequestCABase64 = signRequestCA(RevRequest, credentials);

                                    //Отзыв сертификатов пользователя
                                    resultSubmitAndAcceptRevReques = port.getRight().submitAndAcceptRevRequest(resultSignRequestCABase64.getRight(), "СУИД", Boolean.TRUE);

                                    //Сохраняем сертификаты на отзыв
                                    revCertList = revCertList + "RevID: " + resultSubmitAndAcceptRevReques + ",CertSerialNum: " + resultParseXML.getRight().get(i).get(0) + ";";
                                    response.appendLog(logFormatter.log("Revoked certificate SR-" + resultParseXML.getRight().get(i).get(0) + " to user " + Email, this.getClass()));

                                }

                                response.appendLog(logFormatter.log("Revoke user certificates is complite, userID " + UserID + " in CA", this.getClass()));
                                result.put("CertList", revCertList);
                            } else {
                                response.appendLog(logFormatter.log("Not found active certificates in CA for user " + Email, this.getClass()));
                            }

                            response.setResult("SUCCESS");
                            response.setPropertyMap(result);
                            return response;

                        } else {
                            //Ошибка, не найден ни один пользователь
                            response.appendLog(logFormatter.logError("Error: Parsing result found more than one CA user", this.getClass()));
                            response.setPropertyMap(result);
                            return response;
                        }
                        //Получить список сертификатов пользователя УЦ

                    } else {
                        //Добавить в парсер поле статуса пользователя
                        parseAttrsUsr.add("Status");

                        //Парсинг результата поиска пользователя УЦ
                        resultParseXML = parser.parseXML(resultFindUserCA.getRight().get("getUserRecordListResult").toString(), parseAttrsUsr);
                        if (resultParseXML.isEmpty()) {
                            response.appendLog(logFormatter.logError(getStackTrace(resultParseXML.getLeft()), this.getClass()));
                        }

                        if (resultParseXML.getRight().size() > 0) {
                            for (int i = 0; i < resultParseXML.getRight().size(); i++) {
                                if (resultParseXML.getRight().get(i).get(1).equals("A")) {
                                    j = j + 1;
                                    CAuserID = resultParseXML.getRight().get(i).get(0);
                                    result.put("UserId", CAuserID);
                                }

                            }
                            if (j == 1) {
                                response.appendLog(logFormatter.log("Find user " + CAuserID + " in CA", this.getClass()));

                                //Получить список сертификатов пользователя УЦ
                                port.getRight().getCertificateRecordList(folderID, "V", "", Boolean.TRUE, "UserId", CAuserID, 8, 1, 100, Boolean.TRUE, getCertificateRecordListResult, resultCount, totalRowCount);
                                if (resultCount.value > 0) {
                                    response.appendLog(logFormatter.log("Complite find list of certificates, userID " + CAuserID + " in CA", this.getClass()));
                                    //Сформировать запрос на отзыв сертификатов пользователя
                                    for (int i = 0; i < resultParseXML.getRight().size(); i++) {
                                        RevRequest = "SN=" + resultParseXML.getRight().get(i).get(0) + ",TP=" + resultParseXML.getRight().get(i).get(1) + ",RR=" + RevocationReason + "";

                                        //Подписать запрос
                                        resultSignRequestCABase64 = signRequestCA(RevRequest, credentials);

                                        //Отзыв сертификатов пользователя
                                        resultSubmitAndAcceptRevReques = port.getRight().submitAndAcceptRevRequest(resultSignRequestCABase64.getRight(), "СУИД", Boolean.TRUE);

                                        revCertList = revCertList + "RevID: " + resultSubmitAndAcceptRevReques + ",CertSerialNum: " + resultParseXML.getRight().get(i).get(0) + ";";
                                        response.appendLog(logFormatter.log("Revoked certificate SR-" + resultParseXML.getRight().get(i).get(0) + " to user " + Email, this.getClass()));

                                    }

                                    response.appendLog(logFormatter.log("Revoke user certificates is complite, userID " + CAuserID + " in CA", this.getClass()));
                                    result.put("CertList", revCertList);
                                } else {
                                    response.appendLog(logFormatter.log("Not found active certificates in CA for user " + Email, this.getClass()));
                                }
                                response.setResult("SUCCESS");
                                response.setPropertyMap(result);
                                return response;

                            } else {
                                //Ошибка, найдено больше одного пользователя
                                response.appendLog(logFormatter.logError("Error: Parsing result found more than one CA user", this.getClass()));
                                response.setPropertyMap(result);
                                return response;
                            }
                        }
                    }
                } else {
                    response.appendLog(logFormatter.logError("Error: Not find CA user, filter:" + CAOIDemail + "->" + Email, this.getClass()));
                    response.setPropertyMap(result);
                    return response;
                }
            }

            return response;

        } catch (Exception e) {
            String ss = getStackTrace(e);
            response.appendLog(logFormatter.logError(ss, this.getClass()));
            //LOGGER.log(Level.SEVERE, "Error initialization revokeUser", e);
            response.setPropertyMap(result);
            return response;
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));

        }
    }

    public Either<? extends Throwable, String> signRequestCA(String strRequest, Credentials credentials) {
        try {
            //Подпись как PKCS7 с использованием CMS
            byte[] data = strRequest.getBytes("UTF-16LE");
            final PrivateKey[] keys = new PrivateKey[1];
            keys[0] = credentials.getPrivateKey();
            final Certificate[] certs = new Certificate[1];
            certs[0] = credentials.getCertificate();
            String DIGEST_OID = JCP.GOST_DIGEST_OID;
            String SIGN_OID = JCP.GOST_EL_KEY_OID;
            boolean isCMS = true;

            //byte[] resBase64String = null;
            final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(data);
            final ContentInfo all = new ContentInfo();

            byte[] res = createCMS(data, keys, certs, false);
            res = createCMS(res, keys, certs, false);
            ru.CryptoPro.JCP.tools.Encoder encoder = new ru.CryptoPro.JCP.tools.Encoder();
            String resBase64String = encoder.encode(res);
            return Either.right(resBase64String);
        } catch (Exception e) {
            return Either.left(e);
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

    private HashMap emptyResult() {
        return new HashMap();
    }

}
