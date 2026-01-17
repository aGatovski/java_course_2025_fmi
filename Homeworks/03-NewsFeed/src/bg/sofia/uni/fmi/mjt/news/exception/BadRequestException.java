package bg.sofia.uni.fmi.mjt.news.exception;

public class BadRequestException extends NewsApiException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
