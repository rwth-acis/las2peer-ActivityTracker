package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

import java.util.Map;

public class PageInfo implements Pageable {

    private final int cursor;
    private final int limit;
    private final Map<String, String> filters;
    private final SortDirection sortDirection;
    private final String search;

    public PageInfo(int cursor, int limit, Map<String, String> filters) {
        this(cursor, limit, filters, SortDirection.DEFAULT, null);
    }

    public PageInfo(int cursor, int limit, Map<String, String> filters, SortDirection sortDirection, String search) {
        this.cursor = cursor;
        this.limit = limit;
        this.filters = filters;
        this.sortDirection = sortDirection;
        this.search = search != null ? search : new String();
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
    public Map<String, String> getFilters() {
        return filters;
    }

    @Override
    public SortDirection getSortDirection() {
        return sortDirection;
    }

    @Override
    public String getSearch() {
        return search;
    }
}
