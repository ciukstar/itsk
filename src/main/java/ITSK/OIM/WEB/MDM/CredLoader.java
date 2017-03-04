package ITSK.OIM.WEB.MDM;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import ru.CryptoPro.JCP.JCP;

/**
 *
 * @author sergiu
 */
public class CredLoader {

    Either<? extends Throwable, Credentials> loadCredentials(char[] charPwd) {
        //Загрузить KeyStore
        try {
            KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
            keyStore.load(null, null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey("CA", charPwd);
            X509Certificate cert = (X509Certificate) keyStore.getCertificate("CA");
            return Either.right(new Credentials(privateKey, cert));
        } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            return Either.left(e);
        }
    }

}
