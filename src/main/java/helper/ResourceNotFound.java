package helper;

public class ResourceNotFound extends Exception {
    public ResourceNotFound(String directory) {
        super(directory);
    }
}
