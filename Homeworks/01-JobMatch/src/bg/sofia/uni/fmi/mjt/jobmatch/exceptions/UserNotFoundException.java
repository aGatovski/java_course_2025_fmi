package bg.sofia.uni.fmi.mjt.jobmatch.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
