package de.rwth.dbis.acis.activitytracker.service.dal;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.repositories.ActivityRepository;
import de.rwth.dbis.acis.activitytracker.service.dal.repositories.ActivityRepositoryImpl;
import de.rwth.dbis.acis.activitytracker.service.dal.transform.ActivityTransformator;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import org.jooq.*;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

public class DALFacadeImpl implements DALFacade {

    private final DSLContext dslContext;
    private ActivityRepository activityRepository;

    public DALFacadeImpl(DataSource dataSource, SQLDialect dialect) {
        dslContext = DSL.using(dataSource, dialect);
        activityRepository = new ActivityRepositoryImpl(dslContext);
    }

    public void close() {
       dslContext.close();
    }


    @Override
    public List<Activity> findActivities(Pageable pageable) throws ActivityTrackerException{
        List<Activity> activities = activityRepository.findAll(pageable);
        return activities;
    }

    @Override
    public Activity createActivity(Activity activity) throws ActivityTrackerException {
        return activityRepository.add(activity);
    }
}
