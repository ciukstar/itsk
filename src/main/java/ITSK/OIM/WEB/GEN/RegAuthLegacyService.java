package ITSK.OIM.WEB.GEN;

import ITSK.OIM.WEB.MDM.RegAuthLegacyContract;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 *
 * @author sergiu
 */
public class RegAuthLegacyService {

    private URL url;
    private QName qn;

    public RegAuthLegacyService(URL url, QName qn) {
        this.url = url;
        this.qn = qn;
    }

    public RegAuthLegacyService() {
        this(null, null);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public QName getQn() {
        return qn;
    }

    public void setQn(QName qn) {
        this.qn = qn;
    }
    
    public RegAuthLegacyContract getRegAuthLegacyServiceEndpoint() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
