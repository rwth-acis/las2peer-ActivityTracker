package de.rwth.dbis.acis.activitytracker.service.dal;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.dal.repositories.ActivityRepository;
import de.rwth.dbis.acis.activitytracker.service.dal.repositories.ActivityRepositoryImpl;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

public class DALFacadeImpl implements DALFacade {

    private final DSLContext dslContext;
    private final ActivityRepository activityRepository;

    public DALFacadeImpl(DataSource dataSource, SQLDialect dialect) {
        Settings settings = new Settings();
        if (dialect.equals(SQLDialect.POSTGRES)) {
            settings.withRenderNameCase(RenderNameCase.LOWER);              // Defaults to AS_IS
        }
        dslContext = DSL.using(dataSource, dialect, settings);
        activityRepository = new ActivityRepositoryImpl(dslContext);
    }

    @Override
    public void close() {
        // No longer necessary, jooq claims gc will take care of it
        // dslContext.close();
    }


    @Override
    public PaginationResult<Activity> findActivities(Pageable pageable) throws ActivityTrackerException {
        return activityRepository.findAll(pageable);
    }

    @Override
    public Activity createActivity(Activity activity) throws ActivityTrackerException {
        return activityRepository.add(activity);
    }

    @Override
    public void markStale(int activityId) throws ActivityTrackerException {
        Activity activity = activityRepository.findById(activityId);

        activity.setStale(true);

        activityRepository.update(activity);
    }
}
