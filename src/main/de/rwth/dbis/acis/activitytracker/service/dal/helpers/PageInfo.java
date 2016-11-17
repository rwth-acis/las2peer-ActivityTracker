package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

public class PageInfo implements Pageable {

    private final int cursor;
    private final int limit;
    private final String filter;
    private final SortDirection sortDirection;

    public PageInfo(int cursor, int limit, String filter) {
        this(cursor, limit, filter, SortDirection.DEFAULT);
    }

    public PageInfo(int cursor, int limit, String filter, SortDirection sortDirection) {
        this.cursor = cursor;
        this.limit = limit;
        this.filter = filter;
        this.sortDirection = sortDirection;
    }

    @Override
    public int getCursor() {
        return cursor;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public String getFilter() {
        return filter;
    }

    @Override
    public SortDirection getSortDirection() {
        return sortDirection;
    }
}
