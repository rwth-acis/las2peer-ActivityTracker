package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

public interface Pageable {
    int getOffset();

    int getPageNumber();

    int getPageSize();

    SortDirection getSortDirection();

    public enum SortDirection {
        DEFAULT, ASC, DESC
    }
}
