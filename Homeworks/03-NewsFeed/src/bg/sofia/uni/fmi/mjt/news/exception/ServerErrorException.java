package bg.sofia.uni.fmi.mjt.news.exception;

public class ServerErrorException extends NewsApiException {
    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
