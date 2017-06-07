package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

import java.util.List;
import java.util.Map;

public interface Pageable {
    int getCursor();

    int getLimit();

    Map<String, List<String>> getFilters();

    SortDirection getSortDirection();

    String getSearch();

    enum SortDirection {
        DEFAULT, ASC, DESC
    }

}
