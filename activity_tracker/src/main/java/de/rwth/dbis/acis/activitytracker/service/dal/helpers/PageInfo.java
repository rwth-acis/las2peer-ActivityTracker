package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageInfo implements Pageable {

    private int cursor;
    private int beforeCursor;
    private int afterCursor;
    private final int limit;
    private final Map<String, List<String>> filters;
    private final SortDirection sortDirection;
    private final String search;

    public PageInfo(int cursor, int limit) {
        this(cursor, limit, new HashMap<>());
    }

    public PageInfo(int cursor, int limit, Map<String, List<String>> filters) {
        this(cursor, limit, filters, SortDirection.DEFAULT, null);
    }

    public PageInfo(int cursor, int limit, Map<String, List<String>> filters, SortDirection sortDirection, String search) {
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
    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    @Override
    public int getBeforeCursor() {
        return beforeCursor;
    }

    @Override
    public void setBeforeCursor(int beforeCursor) {
        this.beforeCursor = beforeCursor;
    }

    @Override
    public int getAfterCursor() {
        return afterCursor;
    }

    @Override
    public void setAfterCursor(int afterCursor) {
        this.afterCursor = afterCursor;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public Map<String, List<String>> getFilters() {
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
