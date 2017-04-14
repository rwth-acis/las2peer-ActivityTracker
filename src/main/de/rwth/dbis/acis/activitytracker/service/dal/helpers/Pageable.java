package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

import java.util.Map;

public interface Pageable {
    int getCursor();

    int getLimit();

    Map<String, String> getFilters();

    SortDirection getSortDirection();

    enum SortDirection {
        DEFAULT, ASC, DESC
    }

}
