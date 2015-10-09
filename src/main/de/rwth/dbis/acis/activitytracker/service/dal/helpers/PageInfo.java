package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

public class PageInfo implements Pageable {

    private final int pageNumber;
    private final int pageSize;
    private final SortDirection sortDirection;

    public PageInfo(int pageNumber, int pageSize) {
        this(pageNumber, pageSize, SortDirection.DEFAULT);
    }

    public PageInfo(int pageNumber, int pageSize, SortDirection sortDirection) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortDirection = sortDirection;
    }

    @Override
    public int getOffset() {
        return pageNumber * pageSize;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public SortDirection getSortDirection() {
        return sortDirection;
    }
}
