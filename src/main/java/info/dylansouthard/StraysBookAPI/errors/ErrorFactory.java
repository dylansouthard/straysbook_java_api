package info.dylansouthard.StraysBookAPI.errors;
import java.util.Map;
import java.util.function.Supplier;

public interface ErrorFactory {

    public static AppException of(String code, String message, int status) {
        return new AppException(code, message, status);
    }

    public static AppException animalNotFound() {
        return of(ErrorCodes.ANIMAL_NOT_FOUND, ErrorMessages.ANIMAL_NOT_FOUND, 404);
    }

    public static AppException animalNotOnWatchlist() {
        return of(ErrorCodes.ANIMAL_NOT_ON_WATCHLIST, ErrorMessages.ANIMAL_NOT_ON_WATCHLIST, 404);
    }

    public static AppException auth() {
        return of(ErrorCodes.AUTH, ErrorMessages.AUTH, 401);
    }

    public static AppException authForbidden() {
        return of(ErrorCodes.AUTH_FORBIDDEN, ErrorMessages.AUTH_FORBIDDEN, 403);
    }

    public static AppException careEventsNotFound() {
        return of(ErrorCodes.CARE_EVENTS_NOT_FOUND, ErrorMessages.CARE_EVENTS_NOT_FOUND, 404);
    }

    public static AppException couldNotDelete() {
        return of(ErrorCodes.COULD_NOT_DELETE, ErrorMessages.COULD_NOT_DELETE, 500);
    }

    public static AppException deleteVerifiedSterilizationEvent() {
        return of(ErrorCodes.DELETE_VERIFIED_STERILIZATION_EVENT, ErrorMessages.DELETE_VERIFIED_STERILIZATION_EVENT, 403);
    }

    public static AppException duplicateVaccinationEvent() {
        return of(ErrorCodes.DUPLICATE_VACCINATION_EVENT, ErrorMessages.DUPLICATE_VACCINATION_EVENT, 409);
    }

    public static AppException failedLogout() {
        return of(ErrorCodes.FAILED_LOGOUT, ErrorMessages.FAILED_LOGOUT, 400);
    }

    public static AppException failedToFetch() {
        return of(ErrorCodes.FAILED_TO_FETCH, ErrorMessages.FAILED_TO_FETCH, 500);
    }

    public static AppException feedItemsFetchError() {
        return of(ErrorCodes.FEED_ITEMS_FETCH_ERROR, ErrorMessages.FEED_ITEMS_FETCH_ERROR, 500);
    }

    public static AppException internalServerError() {
        return of(ErrorCodes.INTERNAL_SERVER_ERROR, ErrorMessages.INTERNAL_SERVER_ERROR, 500);
    }

    public static AppException invalidCoordinates() {
        return of(ErrorCodes.INVALID_COORDINATES, ErrorMessages.INVALID_COORDINATES, 400);
    }

    public static AppException invalidCreate() {
        return of(ErrorCodes.INVALID_CREATE, ErrorMessages.INVALID_CREATE, 400);
    }

    public static AppException invalidParams() {
        return of(ErrorCodes.INVALID_PARAMS, ErrorMessages.INVALID_PARAMS, 400);
    }

    public static AppException invalidVerification() {
        return of(ErrorCodes.INVALID_VERIFICATION, ErrorMessages.INVALID_VERIFICATION, 400);
    }

    public static AppException invalidUpdate() {
        return of(ErrorCodes.INVALID_UPDATE, ErrorMessages.INVALID_UPDATE, 400);
    }

    public static AppException itemExists() {
        return of(ErrorCodes.ITEM_EXISTS, ErrorMessages.ITEM_EXISTS, 409);
    }

    public static AppException noVaccinationFound() {
        return of(ErrorCodes.NO_VACCINATION_FOUND, ErrorMessages.NO_VACCINATION_FOUND, 404);
    }

    public static AppException recentCareEventExists() {
        return of(ErrorCodes.RECENT_CARE_EVENT_EXISTS, ErrorMessages.RECENT_CARE_EVENT_EXISTS, 429);
    }

    public static AppException uploadError() {
        return of(ErrorCodes.UPLOAD_ERROR, ErrorMessages.UPLOAD_ERROR, 500);
    }

    public static AppException userNotFound() {
        return of(ErrorCodes.USER_NOT_FOUND, ErrorMessages.USER_NOT_FOUND, 404);
    }

    public static final Map<String, Supplier<AppException>> errorMap = Map.ofEntries(
        Map.entry("ANIMAL_NOT_FOUND", ErrorFactory::animalNotFound),
        Map.entry("ANIMAL_NOT_ON_WATCHLIST", ErrorFactory::animalNotOnWatchlist),
        Map.entry("AUTH", ErrorFactory::auth),
        Map.entry("AUTH_FORBIDDEN", ErrorFactory::authForbidden),
        Map.entry("CARE_EVENTS_NOT_FOUND", ErrorFactory::careEventsNotFound),
        Map.entry("COULD_NOT_DELETE", ErrorFactory::couldNotDelete),
        Map.entry("DELETE_VERIFIED_STERILIZATION_EVENT", ErrorFactory::deleteVerifiedSterilizationEvent),
        Map.entry("DUPLICATE_VACCINATION_EVENT", ErrorFactory::duplicateVaccinationEvent),
        Map.entry("FAILED_LOGOUT", ErrorFactory::failedLogout),
        Map.entry("FAILED_TO_FETCH", ErrorFactory::failedToFetch),
        Map.entry("FEED_ITEMS_FETCH_ERROR", ErrorFactory::feedItemsFetchError),
        Map.entry("INTERNAL_SERVER_ERROR", ErrorFactory::internalServerError),
        Map.entry("INVALID_COORDINATES", ErrorFactory::invalidCoordinates),
        Map.entry("INVALID_CREATE", ErrorFactory::invalidCreate),
        Map.entry("INVALID_PARAMS", ErrorFactory::invalidParams),
        Map.entry("INVALID_VERIFICATION", ErrorFactory::invalidVerification),
        Map.entry("INVALID_UPDATE", ErrorFactory::invalidUpdate),
        Map.entry("ITEM_EXISTS", ErrorFactory::itemExists),
        Map.entry("NO_VACCINATION_FOUND", ErrorFactory::noVaccinationFound),
        Map.entry("RECENT_CARE_EVENT_EXISTS", ErrorFactory::recentCareEventExists),
        Map.entry("UPLOAD_ERROR", ErrorFactory::uploadError),
        Map.entry("USER_NOT_FOUND", ErrorFactory::userNotFound)
    );

    static AppException getErrorOfType(String code) {
        return errorMap.getOrDefault(code, ErrorFactory::internalServerError).get();
    }

}
