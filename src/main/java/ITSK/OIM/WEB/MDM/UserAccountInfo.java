package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class UserAccountInfo {
    private final String caOIDUPN;
    private final String ADLogin;
    private final String CAOIDemail;
    private final String email;
    private final String caOIDCN;
    private final String fio;
    private final String folderId;

    private UserAccountInfo(
            String caOIDUPN, 
            String ADLogin, 
            String CAOIDemail, 
            String email, 
            String caOIDCN, 
            String fio,
            String folderId
    ) {
        this.caOIDUPN = caOIDUPN;
        this.ADLogin = ADLogin;
        this.CAOIDemail = CAOIDemail;
        this.email = email;
        this.caOIDCN = caOIDCN;
        this.fio = fio;
        this.folderId = folderId;
    }

    public String getCaOIDUPN() {
        return caOIDUPN;
    }

    public String getADLogin() {
        return ADLogin;
    }

    public String getCAOIDemail() {
        return CAOIDemail;
    }

    public String getEmail() {
        return email;
    }

    public String getCaOIDCN() {
        return caOIDCN;
    }

    public String getFio() {
        return fio;
    }

    public String getFolderId() {
        return folderId;
    }
    
    public static CaOidUpnStep builder() {
        return new Steps();
    }
    
    public interface CaOidUpnStep {
        public AdLoginStep caOidUpn(String value);
    }
    public interface AdLoginStep {
        public CaOidEmailStep adLogin(String value);
    }
    public interface CaOidEmailStep {
        public EmailStep caOidEmail(String value);
    }
    public interface EmailStep {
        public CaOidCnStep email(String value);
    }
    public interface CaOidCnStep {
        public FioStep caOidCn(String value);
    }
    public interface FioStep {
        public FolderIdStep fio(String value);
    }
    public interface FolderIdStep {
        public BuildStep folderId(String value);
    }
    public interface BuildStep {
        public UserAccountInfo build();
    }
    
    private static class Steps implements CaOidUpnStep, AdLoginStep, CaOidEmailStep,
            EmailStep, CaOidCnStep, FioStep, FolderIdStep, BuildStep {

        private String caOidUpn;
        private String adLogin;
        private String caOidEmail;
        private String email;
        private String caOidCn;
        private String fio;
        private String folderId;

        @Override
        public AdLoginStep caOidUpn(String value) {
            this.caOidUpn = value;
            return this;
        }

        @Override
        public CaOidEmailStep adLogin(String value) {
            this.adLogin = value;
            return this;
        }

        @Override
        public EmailStep caOidEmail(String value) {
            this.caOidEmail = value;
            return this;
        }

        @Override
        public CaOidCnStep email(String value) {
            this.email = value;
            return this;
        }

        @Override
        public FioStep caOidCn(String value) {
            this.caOidCn = value;
            return this;
        }

        @Override
        public FolderIdStep fio(String value) {
            this.fio = value;
            return this;
        }

        @Override
        public BuildStep folderId(String value) {
            this.folderId = value;
            return this;
        }

        @Override
        public UserAccountInfo build() {
            return new UserAccountInfo(
                    caOidUpn, adLogin, caOidEmail, 
                    email, caOidCn, fio, folderId
            );
        }
        
    }
}
