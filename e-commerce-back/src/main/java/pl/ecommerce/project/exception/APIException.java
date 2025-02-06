package pl.ecommerce.project.exception;

public class APIException extends RuntimeException{
    private static final long serialVersion = 1L;

    public APIException() {
    }

    public APIException(String message) {
        super(message);
    }
}
