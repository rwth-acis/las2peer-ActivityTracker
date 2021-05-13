package de.rwth.dbis.acis.activitytracker.service.dal;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;


public interface DALFacade {
    void close();

    PaginationResult<Activity> findActivities(Pageable pageable) throws ActivityTrackerException;

    Activity createActivity(Activity activity) throws ActivityTrackerException;

    void markStale(int activityId) throws ActivityTrackerException;
}
