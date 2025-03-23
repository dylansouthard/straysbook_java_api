package info.dylansouthard.StraysBookAPI.errors;

public class DuplicateOAuthProviderException extends RuntimeException {
    public DuplicateOAuthProviderException(String message) {
        super(message);
    }

    public DuplicateOAuthProviderException() {
        super("OAuth provider already exists");
    }
}
