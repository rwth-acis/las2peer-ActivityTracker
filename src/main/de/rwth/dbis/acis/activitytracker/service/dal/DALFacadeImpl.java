package de.rwth.dbis.acis.activitytracker.service.dal;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.repositories.ActivityRepository;
import de.rwth.dbis.acis.activitytracker.service.dal.repositories.ActivityRepositoryImpl;
import de.rwth.dbis.acis.activitytracker.service.dal.transform.ActivityTransformator;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.List;

public class DALFacadeImpl implements DALFacade {

    private final DSLContext dslContext;
    private final Connection connection;
    private ActivityRepository activityRepository;

    public DALFacadeImpl(Connection connection, SQLDialect dialect) {
        this.connection = connection;
        dslContext = DSL.using(connection, dialect);
        activityRepository = new ActivityRepositoryImpl(dslContext);
    }

    public DSLContext getDslContext() {
        return dslContext;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public List<Activity> findActivitiesForUser(Pageable pageable) throws ActivityTrackerException{
        return activityRepository.findAll();
    }
}
