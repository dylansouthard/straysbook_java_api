package info.dylansouthard.StraysBookAPI.constants;

public class ApiRoutes {
    public static class ANIMALS {
        public static final String BASE = "/api/animals";
        public static final String CREATE = "/api/animals";
        public static final String NEARBY = "/api/animals/nearby";
        public static final String DETAIL = "/api/animals/{id}";
    }
    public static class USERS {
        public static final String BASE = "/api/users";
        public static final String DETAIL = "/api/users/{id}";
        public static final String ME = "/api/users/me";
        public static final String WATCHLIST = "/api/users/{id}/watchlist";
    }
    public static class AUTH {
        public static final String BASE = "/api/auth";
        public static final String STATUS = "/api/auth/status";
        public static final String REFRESH = "/api/auth/refresh";
        public static final String REVOKE = "/api/auth/revoke";
        public static final String REVOKE_ALL = "/api/auth/revoke-all";
    }
}