package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class Pair<L, R> {

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public static <L, R> Pair<L, R> right(R right) {
        return Pair.of(null, right);
    }

    public static <L, R> Pair<L, R> left(L left) {
        return Pair.of(left, null);
    }
    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

}
