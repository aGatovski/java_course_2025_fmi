package bg.sofia.uni.fmi.mjt.news.exception;

public class TooManyRequestException extends NewsApiException {
    public TooManyRequestException(String message) {
        super(message);
    }

    public TooManyRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
