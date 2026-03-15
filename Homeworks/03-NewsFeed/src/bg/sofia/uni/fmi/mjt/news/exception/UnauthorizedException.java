package bg.sofia.uni.fmi.mjt.news.exception;

public class UnauthorizedException extends NewsApiException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
