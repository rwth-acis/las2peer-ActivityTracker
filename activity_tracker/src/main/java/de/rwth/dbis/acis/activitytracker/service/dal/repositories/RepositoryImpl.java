package de.rwth.dbis.acis.activitytracker.service.dal.repositories;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.EntityBase;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.dal.transform.Transformer;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RepositoryImpl<E extends EntityBase, R extends Record> implements Repository<E> {

    protected final DSLContext jooq;
    protected final Transformer<E, R> transformer;

    /**
     * @param jooq        DSLContext for JOOQ connection
     * @param transformer Transformer object to create mapping between JOOQ record and our entities
     */
    public RepositoryImpl(DSLContext jooq, Transformer<E, R> transformer) {
        this.jooq = jooq;
        this.transformer = transformer;
    }

    /**
     * @param entity to add
     * @return the persisted entity
     * @throws ActivityTrackerException
     */
    @Override
    public E add(E entity) throws ActivityTrackerException {
        E transformedEntity = null;
        try {
            R persisted;

            persisted = jooq.insertInto(transformer.getTable())
                    .set(transformer.createRecord(entity))
                    .returning()
                    .fetchOne();

            transformedEntity = transformer.mapToEntity(persisted);
        } catch (DataAccessException e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN, e.getMessage());
        }
        return transformedEntity;
    }


    /**
     * @param id of an entity, which should be deleted
     * @return the deleted entity. It is not anymore in the database!
     * @throws ActivityTrackerException
     */
    //TODO transaction (findById,delete)
    @Override
    public E delete(int id) throws ActivityTrackerException {
        E deleted = null;
        try {
            deleted = this.findById(id);

            int deletedRecordCount = jooq.delete(transformer.getTable())
                    .where(transformer.getTableId().equal(id))
                    .execute();
        } catch (ActivityTrackerException ex) {
            ExceptionHandler.getInstance().convertAndThrowException(ex);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN);
        }

        return deleted;
    }

    /**
     * @return all the entities currently in the database
     * @throws ActivityTrackerException
     */
    @Override
    public List<E> findAll() throws ActivityTrackerException {
        List<E> entries = null;
        try {
            entries = new ArrayList<>();

            List<R> queryResults = jooq.selectFrom(transformer.getTable()).fetchInto(transformer.getRecordClass());

            for (R queryResult : queryResults) {
                E entry = transformer.mapToEntity(queryResult);
                entries.add(entry);
            }
        } catch (DataAccessException e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN, e.getMessage());
        }

        return entries;
    }

    /**
     * @param pageable
     * @return PaginationResult with all the entities currently in the database
     * @throws ActivityTrackerException
     */
    @Override
    public PaginationResult<E> findAll(Pageable pageable) throws ActivityTrackerException {
        PaginationResult<E> result = null;
        try {
            List<E> entries = new ArrayList<>();

            Condition cursorCondition = transformer.getTableId().notEqual(-1);
            if (pageable.getCursor() != -1) {
                if (pageable.getSortDirection() == Pageable.SortDirection.ASC) {
                    cursorCondition = transformer.getTableId().greaterThan(pageable.getCursor());
                } else {
                    cursorCondition = transformer.getTableId().lessThan(pageable.getCursor());
                }
            }

            List<R> queryResults = jooq.selectFrom(transformer.getTable())
                    .where(transformer.getFilterConditions(pageable.getFilters()))
                    .and(transformer.getSearchCondition(pageable.getSearch()))
                    .and(cursorCondition)
                    .orderBy(transformer.getSortFields(pageable.getSortDirection()))
                    .limit(pageable.getLimit())
                    .fetchInto(transformer.getRecordClass());

            for (R queryResult : queryResults) {
                E entry = transformer.mapToEntity(queryResult);
                entries.add(entry);
            }

            // update cursors
            if (!entries.isEmpty()) {
                pageable.setCursor(entries.get(entries.size() - 1).getId());
                if (pageable.getSortDirection() == Pageable.SortDirection.ASC) {
                    if (pageable.getBeforeCursor() == 0) {
                        pageable.setBeforeCursor(entries.get(0).getId());
                    }
                    pageable.setAfterCursor(entries.get(entries.size() - 1).getId());
                } else {
                    pageable.setBeforeCursor(entries.get(entries.size() - 1).getId());
                    if (pageable.getAfterCursor() == 0) {
                        pageable.setAfterCursor(entries.get(0).getId());
                    }
                }
            }

            result = new PaginationResult<>(pageable, entries);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN, e.getMessage());
        }

        return result;
    }

    /**
     * @param id of the entity we are looking for
     * @return the entity from the database with the given Id
     * @throws ActivityTrackerException
     */
    @Override
    public E findById(int id) throws ActivityTrackerException {
        R queryResult = null;
        try {
            queryResult = jooq.selectFrom(transformer.getTable())
                    .where(transformer.getTableId().equal(id))
                    .fetchOne();

            if (queryResult == null) {
                throw new Exception("No " + transformer.getRecordClass() + " found with id: " + id);
            }
        } catch (ActivityTrackerException ex) {
            ExceptionHandler.getInstance().convertAndThrowException(ex);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.NOT_FOUND);
        }

        return transformer.mapToEntity(queryResult);
    }

    /**
     * @param entity object, which holds the new values of the database update
     * @return the entity after the database
     * @throws ActivityTrackerException
     */
    //TODO transaction(update,findById)
    @Override
    public E update(E entity) throws ActivityTrackerException {
        E byId = null;
        try {
            UpdateSetFirstStep<R> update = jooq.update(transformer.getTable());
            Map<Field, Object> map = transformer.getUpdateMap(entity);
            UpdateSetMoreStep moreStep = null;
            for (Map.Entry<Field, Object> item : map.entrySet()) {
                Field key = item.getKey();
                Object value = item.getValue();
                if (moreStep == null)
                    moreStep = update.set(key, value);
                else
                    moreStep.set(key, value);
            }
            assert moreStep != null;
            moreStep.where(transformer.getTableId().equal(entity.getId())).execute();
            byId = findById(entity.getId());
        } catch (ActivityTrackerException ex) {
            ExceptionHandler.getInstance().convertAndThrowException(ex);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN);
        }
        return byId;
    }
}
