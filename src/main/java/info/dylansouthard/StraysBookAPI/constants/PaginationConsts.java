package info.dylansouthard.StraysBookAPI.constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationConsts {
    public static final int DEFAULT_NOTIFICATION_PAGE_SIZE = 10;
    public static final int HP_NOTIFICATION_PAGE_SIZE = 20;
    public static final String SORT_FIELD_CREATED_AT = "createdAt";
    public static final String SORT_FIELD_PRIORITY = "priority";

    public static Pageable notificationPageable(int pageNumber) {
//        if (priority == PriorityType.HIGH || priority == PriorityType.MEDIUM) {
//            return PageRequest.of(pageNumber, HP_NOTIFICATION_PAGE_SIZE,
//                    Sort.by(SORT_FIELD_PRIORITY).descending().and(Sort.by(SORT_FIELD_CREATED_AT).descending()));
//        }

        return PageRequest.of(pageNumber, DEFAULT_NOTIFICATION_PAGE_SIZE,
                Sort.by(SORT_FIELD_CREATED_AT).descending());
    }

    public static Pageable notificationForAnimalProfile(int pageNumber) {
        return PageRequest.of(pageNumber, DEFAULT_NOTIFICATION_PAGE_SIZE,
                Sort.by(SORT_FIELD_CREATED_AT).descending());
    }

}
