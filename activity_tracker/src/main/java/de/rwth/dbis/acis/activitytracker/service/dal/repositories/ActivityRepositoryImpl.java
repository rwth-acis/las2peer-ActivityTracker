package de.rwth.dbis.acis.activitytracker.service.dal.repositories;

import de.rwth.dbis.acis.activitytracker.dal.jooq.reqbaztrack.tables.records.ActivityRecord;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.transform.ActivityTransformer;
import org.jooq.DSLContext;


public class ActivityRepositoryImpl extends RepositoryImpl<Activity, ActivityRecord> implements ActivityRepository {

    public ActivityRepositoryImpl(DSLContext jooq) {
        super(jooq, new ActivityTransformer());
    }
}
