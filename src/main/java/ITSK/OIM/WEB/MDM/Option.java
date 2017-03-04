package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class Option<T> {

    public static <T> Option<T> ofNullable(T value) {
        return null == value ? Option.<T>empty() : Option.of(value);
    }
    
    public static <T> Option<T> empty() {
        return new Option(null);
    }
    
    public static <T> Option<T> of(T value) {
        return new Option(value);
    }
    
    private final T value;
    
    
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

    public T orElseGet(T other) {
        return isPresent() ? get() : other;
    }
}
