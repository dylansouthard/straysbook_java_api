package info.dylansouthard.StraysBookAPI.errors;
import java.util.Map;
import java.util.function.Supplier;

public interface ErrorMessages {
    String ANIMAL_NOT_FOUND = "No animal found.";
    String ANIMAL_NOT_ON_WATCHLIST = "Animal or litter not found on your watchlist.";
    String AUTH = "Unauthorized. Please log in and try again.";
    String AUTH_FORBIDDEN = "This request shall not pass! You are not authorized to perform this action.";
    String CARE_EVENTS_NOT_FOUND = "No care events found.";
    String COULD_NOT_DELETE = "The item could not be deleted.";
    String DELETE_VERIFIED_STERILIZATION_EVENT = "Deleting a verified sterilization event is not allowed.";
    String DUPLICATE_VACCINATION_EVENT = "A vaccination event of this type has already been registered recently. Please check the records before registering a new event.";
    String FAILED_LOGOUT = "Could not log out the user";
    String FAILED_TO_FETCH = "Could not fetch the requested item";
    String FEED_ITEMS_FETCH_ERROR = "Error fetching feed items. Please try again later.";
    String INTERNAL_SERVER_ERROR = "An unexpected error occurred.";
    String INVALID_COORDINATES = "Invalid Coordinates.";
    String INVALID_CREATE = "Could not save data. Some required fields may be missing";
    String INVALID_PARAMS = "The query is missing the required parameters.";
    String INVALID_VERIFICATION = "Verification could not be completed. The item may have already been verified or may not currently require verification.";
    String INVALID_UPDATE = "Could not update data.";
    String ITEM_EXISTS = "Could not create the item as it already exists";
    String NO_VACCINATION_FOUND = "No vaccination found with the given id, so it could not be removed.";
    String RECENT_CARE_EVENT_EXISTS = "A similar care event has been registered for this litter by the same user within the past two hours. Please try again later.";
    String UPLOAD_ERROR = "Failed to upload or convert the image. Please try again.";
    String USER_NOT_FOUND = "No user found.";
}
