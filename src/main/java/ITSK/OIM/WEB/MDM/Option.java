package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class Option<T> {
    private final T value;
    
    public static <T> Option<T> empty() {
        return new Option(null);
    }
    
    public static <T> Option<T> of(T value) {
        return new Option(value);
    }
    
    private Option(T value) {
        this.value = value;
    }
    
    public boolean isEmpty() {
        return null == value;
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
