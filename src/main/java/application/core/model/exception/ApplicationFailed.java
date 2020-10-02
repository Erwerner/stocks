package application.core.model.exception;

public class ApplicationFailed extends RuntimeException {
    public ApplicationFailed(Exception e) {
        super(e);
    }
}
