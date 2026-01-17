package bg.sofia.uni.fmi.mjt.news.exception;

public class ApiKeyException extends NewsApiException {
    public ApiKeyException(String message) {
        super(message);
    }

    public ApiKeyException(String message, Throwable cause) {
        super(message, cause);
    }

}
