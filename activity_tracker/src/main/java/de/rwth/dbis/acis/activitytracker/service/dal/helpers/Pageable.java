package de.rwth.dbis.acis.activitytracker.service.dal.helpers;

import java.util.List;
import java.util.Map;

public interface Pageable {
    int getCursor();
    void setCursor(int cursor);

    int getBeforeCursor();
    void setBeforeCursor(int beforeCursor);

    int getAfterCursor();
    void setAfterCursor(int afterCursor);

    int getLimit();

    Map<String, List<String>> getFilters();

    SortDirection getSortDirection();

    String getSearch();

    enum SortDirection {
        DEFAULT, ASC, DESC
    }

}
