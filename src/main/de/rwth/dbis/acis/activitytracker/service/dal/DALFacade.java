package de.rwth.dbis.acis.activitytracker.service.dal;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;

import java.sql.Connection;
import java.util.List;

public interface DALFacade {

    Connection getConnection();

    List<Activity> findActivitiesForUser(Pageable pageable) throws ActivityTrackerException;
}
