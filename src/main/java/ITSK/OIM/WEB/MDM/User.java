package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class User {
    
    public enum Status {
        A,NA
    }
    
    private final String id;
    private final Status status;

    public User(String id, Status status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
    
}
