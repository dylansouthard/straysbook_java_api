package info.dylansouthard.StraysBookAPI.model.enums;

public enum OAuthProviderType {
    GOOGLE,
    FACEBOOK,
    LINE;

    public String key() {
        return this.name().toLowerCase();
    }
}
