package de.rwth.dbis.acis.activitytracker.service.dal.transform;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.records.ActivityRecord;
import org.jooq.*;

import java.util.*;

import static de.rwth.dbis.acis.activitytracker.service.dal.jooq.tables.Activity.ACTIVITY;

public class ActivityTransformer implements Transformer<Activity, ActivityRecord> {
    @Override
    public ActivityRecord createRecord(Activity entity) {
        ActivityRecord activityRecord = new ActivityRecord();
        activityRecord.setCreationDate(new java.sql.Timestamp(entity.getCreationDate().getTime()));
        activityRecord.setActivityAction(entity.getActivityAction());
        activityRecord.setOrigin(entity.getOrigin());
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
                .creationDate(record.getCreationDate())
                .activityAction(record.getActivityAction())
                .origin(record.getOrigin())
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
                return Arrays.asList(ACTIVITY.ID.desc(),
                        ACTIVITY.ID.desc());
            case ASC:
                return Arrays.asList(ACTIVITY.ID.asc(),
                        ACTIVITY.ID.asc());
            case DESC:
                return Arrays.asList(ACTIVITY.ID.desc(),
                        ACTIVITY.ID.desc());
        }
        return null;
    }

    @Override
    public Condition getSearchCondition(String search) throws Exception {
        return ACTIVITY.ACTIVITY_ACTION.likeIgnoreCase("%" + search + "%")
                .or(ACTIVITY.DATA_TYPE.likeIgnoreCase("%" + search + "%"));
    }

    @Override
    public Collection<? extends Condition> getFilterConditions(Map<String, String> filters) throws Exception {
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, String> filterEntry : filters.entrySet()) {
            if (filterEntry.getKey().equals("activityAction")) {
                conditions.add(ACTIVITY.ACTIVITY_ACTION.equalIgnoreCase(filterEntry.getValue()));
            }
            if (filterEntry.getKey().equals("origin")) {
                conditions.add(ACTIVITY.ORIGIN.equalIgnoreCase(filterEntry.getValue()));
            }
            if (filterEntry.getKey().equals("dataType")) {
                conditions.add(ACTIVITY.DATA_TYPE.equalIgnoreCase(filterEntry.getValue()));
            }
            if (filterEntry.getKey().equals("dataUrl")) {
                conditions.add(ACTIVITY.DATA_URL.equalIgnoreCase(filterEntry.getValue()));
            }
            if (filterEntry.getKey().equals("parentDataType")) {
                conditions.add(ACTIVITY.PARENT_DATA_TYPE.equalIgnoreCase(filterEntry.getValue()));
            }
            if (filterEntry.getKey().equals("parentDataUrl")) {
                conditions.add(ACTIVITY.PARENT_DATA_URL.equalIgnoreCase(filterEntry.getValue()));
            }
            if (filterEntry.getKey().equals("userUrl")) {
                conditions.add(ACTIVITY.USER_URL.equalIgnoreCase(filterEntry.getValue()));
            }
        }
        return conditions;
    }
}
