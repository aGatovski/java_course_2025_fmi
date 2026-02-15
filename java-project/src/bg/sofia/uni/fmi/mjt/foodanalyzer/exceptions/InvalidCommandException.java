package bg.sofia.uni.fmi.mjt.foodanalyzer.exceptions;

//trq vidq checked ili unchecked mislq checked i  da go handle kato catch kaji neshto??? ne da krashva
public class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
