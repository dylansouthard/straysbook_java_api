package info.dylansouthard.StraysBookAPI.errors;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
