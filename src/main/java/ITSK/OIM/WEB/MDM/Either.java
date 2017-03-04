package ITSK.OIM.WEB.MDM;

/**
 *
 * @author sergiu
 */
public class Either<L, R> {

    public static <L, R> Either<L, R> of(L left, R right) {
        return new Either<>(left, right);
    }

    public static <L, R> Either<L, R> right(R right) {
        return Either.of(null, right);
    }

    public static <L, R> Either<L, R> left(L left) {
        return Either.of(left, null);
    }
    private final L left;
    private final R right;

    public Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public boolean isEmpty() {
        return null == getRight();
    }

}
