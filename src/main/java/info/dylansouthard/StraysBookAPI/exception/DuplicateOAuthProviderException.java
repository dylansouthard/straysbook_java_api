package info.dylansouthard.StraysBookAPI.exception;

public class DuplicateOAuthProviderException extends RuntimeException {
    public DuplicateOAuthProviderException(String message) {
        super(message);
    }

    public DuplicateOAuthProviderException() {
        super("OAuth provider already exists");
    }
}
