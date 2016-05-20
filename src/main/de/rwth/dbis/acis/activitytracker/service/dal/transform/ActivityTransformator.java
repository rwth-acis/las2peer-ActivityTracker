package de.rwth.dbis.acis.activitytracker.service.dal.transform;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.*;
import de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.records.ActivityRecord;
import org.jooq.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.Activity.ACTIVITY;

public class ActivityTransformator implements Transformator<Activity, ActivityRecord> {
    @Override
    public ActivityRecord createRecord(Activity entity) {
        ActivityRecord activityRecord = new ActivityRecord();
        activityRecord.setCreationTime(new java.sql.Timestamp(entity.getCreationTime().getTime()));
        activityRecord.setActivityAction(entity.getActivityAction());
        activityRecord.setDataUrl(entity.getDataUrl());
        activityRecord.setDataType(entity.getDataType());
        activityRecord.setDataFrontendUrl(entity.getDataFrontendUrl());
        activityRecord.setParentDataUrl(entity.getParentDataUrl());
        activityRecord.setParentDataType(entity.getParentDataType());
        activityRecord.setUserUrl(entity.getUserUrl());
        return activityRecord;
    }

    @Override
    public Activity mapToEntity(ActivityRecord record) {
        return Activity.getBuilder()
                .id(record.getId())
                .creationTime(record.getCreationTime())
                .activityAction(record.getActivityAction())
                .dataUrl(record.getDataUrl())
                .dataType(record.getDataType())
                .dataFrontendUrl(record.getDataFrontendUrl())
                .parentDataUrl(record.getParentDataUrl())
                .parentDataType(record.getParentDataType())
                .userUrl(record.getUserUrl())
                .build();
    }

    @Override
    public Table<ActivityRecord> getTable() {
        return ACTIVITY;
    }

    @Override
    public TableField<ActivityRecord, Integer> getTableId() {
        return ACTIVITY.ID;
    }

    @Override
    public Class<? extends ActivityRecord> getRecordClass() {
        return ActivityRecord.class;
    }

    @Override
    public Map<Field, Object> getUpdateMap(Activity entity) {
        return new HashMap<Field, Object>();
    }

    @Override
    public Collection<? extends SortField<?>> getSortFields(Pageable.SortDirection sortDirection) {
        switch (sortDirection) {
            case DEFAULT:
                return Arrays.asList(ACTIVITY.CREATION_TIME.desc(),
                        ACTIVITY.ID.desc());
            case ASC:
                return Arrays.asList(ACTIVITY.CREATION_TIME.asc(),
                        ACTIVITY.ID.asc());
            case DESC:
                return Arrays.asList(ACTIVITY.CREATION_TIME.desc(),
                        ACTIVITY.ID.desc());
        }
        return null;
    }

    @Override
    public Collection<? extends Condition> getSearchFields(String likeExpression) throws Exception {
        return Arrays.asList(
                ACTIVITY.ACTIVITY_ACTION.likeIgnoreCase(likeExpression)
                        .or(ACTIVITY.DATA_TYPE.likeIgnoreCase(likeExpression))
        );
    }
}
