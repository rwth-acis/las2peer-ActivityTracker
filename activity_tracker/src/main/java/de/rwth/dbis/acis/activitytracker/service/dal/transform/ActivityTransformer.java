package de.rwth.dbis.acis.activitytracker.service.dal.transform;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.rwth.dbis.acis.activitytracker.dal.jooq.tables.records.ActivityRecord;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.Activity;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;

import static de.rwth.dbis.acis.activitytracker.dal.jooq.tables.Activity.ACTIVITY;
import static org.jooq.JSON.json;

public class ActivityTransformer implements Transformer<Activity, ActivityRecord> {

    @Override
    public ActivityRecord createRecord(Activity entity) {
        ActivityRecord activityRecord = new ActivityRecord();
        activityRecord.setCreationDate(entity.getCreationDate());
        activityRecord.setActivityAction(entity.getActivityAction());
        activityRecord.setOrigin(entity.getOrigin());
        activityRecord.setDataUrl(entity.getDataUrl());
        activityRecord.setDataType(entity.getDataType());
        activityRecord.setDataFrontendUrl(entity.getDataFrontendUrl());
        activityRecord.setParentDataUrl(entity.getParentDataUrl());
        activityRecord.setParentDataType(entity.getParentDataType());
        activityRecord.setUserUrl(entity.getUserUrl());
        activityRecord.setPublic(entity.isPublicActivity());
        activityRecord.setStale(entity.getStale());

        // TODO: Reconsider parsing between jackson and jooq. Maybe involve SO
        activityRecord.setAdditionalObject(entity.getAdditionalObject() == null ? null : json(entity.getAdditionalObject().toString()));
        return activityRecord;
    }

    @Override
    public Activity mapToEntity(ActivityRecord record) throws ActivityTrackerException {

        JsonNode actualObj = null;
        try {
            if (record.getAdditionalObject() != null) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                // TODO: When fixing the above todo-comment, consider this as well.
                actualObj = mapper.readTree(record.getAdditionalObject().toString());
            }
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.DALFACADE, ErrorCode.SERILIZATION_PROBLEM);
        }

        return Activity.builder()
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
                .publicActivity(record.getPublic())
                .additionalObject(actualObj)
                .stale(record.getStale())
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
        return new HashMap<>() {{
            put(ACTIVITY.STALE, entity.getStale());
        }};
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
    public Condition getSearchCondition(String search) {
        return ACTIVITY.ACTIVITY_ACTION.likeIgnoreCase("%" + search + "%")
                .or(ACTIVITY.DATA_TYPE.likeIgnoreCase("%" + search + "%"));
    }

    @Override
    public Collection<? extends Condition> getFilterConditions(Map<String, List<String>> filters) {
        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry<String, List<String>> filterEntry : filters.entrySet()) {
            if (filterEntry.getKey().equals("activityAction")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.ACTIVITY_ACTION));
            }
            if (filterEntry.getKey().equals("origin")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.ORIGIN));
            }
            if (filterEntry.getKey().equals("dataType")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.DATA_TYPE));          //conditions.add(ACTIVITY.DATA_TYPE.in(filterEntry.getValue())); more elegant but no way to make case-insensitive
            }
            if (filterEntry.getKey().equals("dataUrl")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.DATA_URL));
            }
            if (filterEntry.getKey().equals("parentDataType")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.PARENT_DATA_TYPE));
            }
            if (filterEntry.getKey().equals("parentDataUrl")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.PARENT_DATA_URL));
            }
            if (filterEntry.getKey().equals("userUrl")) {
                conditions.add(listToOrConcat(filterEntry.getValue(), ACTIVITY.USER_URL));
            }
            if (filterEntry.getKey().equals("combinedFilter")) {
                Condition orConcat = DSL.falseCondition();
                for (String filter : filterEntry.getValue()) {
                    Condition andConcat = DSL.trueCondition();
                    String[] parts = filter.split("-");
                    if (parts.length >= 2) {
                        if (!parts[0].equals("*")) {
                            andConcat = andConcat.and(ACTIVITY.ACTIVITY_ACTION.equalIgnoreCase(parts[0]));
                        }
                        if (!parts[1].equals("*")) {
                            andConcat = andConcat.and(ACTIVITY.DATA_TYPE.equalIgnoreCase(parts[1]));
                        }
                        if (parts.length == 3 && !parts[2].equals("*")) {
                            andConcat = andConcat.and(ACTIVITY.PARENT_DATA_TYPE.equalIgnoreCase(parts[2]));
                        }
                        orConcat = orConcat.or(andConcat);
                    }
                }
                conditions.add(orConcat);
            }
        }
        // Always filter out stale objects
        conditions.add(ACTIVITY.STALE.isFalse());
        return conditions;
    }

    private Condition listToOrConcat(List<String> entries, TableField<ActivityRecord, String> field) {
        Condition result = DSL.falseCondition();
        for (String entry : entries) {
            result = result.or(field.equalIgnoreCase(entry));
        }
        return result;
    }
}
