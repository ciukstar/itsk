package ITSK.OIM.WEB.MDM;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 *
 * @author sergiu
 */
public class Credentials {
    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    public Credentials(PrivateKey privateKey, X509Certificate certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }
    
}
