package application.core.exception;

public class ApplicationFailed extends RuntimeException {
    public ApplicationFailed(Exception e) {
        super(e);
    }
}
