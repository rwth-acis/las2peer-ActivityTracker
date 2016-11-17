package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

public interface Pageable {
    int getCursor();

    int getLimit();

    String getFilter();

    SortDirection getSortDirection();

    enum SortDirection {
        DEFAULT, ASC, DESC
    }

}
