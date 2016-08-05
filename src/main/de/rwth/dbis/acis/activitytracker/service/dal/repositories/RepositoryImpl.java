package de.rwth.dbis.acis.activitytracker.service.dal.repositories;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.EntityBase;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.dal.transform.Transformator;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;
import org.jooq.*;
import org.jooq.Condition;
import org.jooq.exception.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.*;

public class RepositoryImpl<E extends EntityBase, R extends Record> implements Repository<E> {

    protected final DSLContext jooq;
    protected final Transformator<E, R> transformator;

    /**
     * @param jooq          DSLContext for JOOQ connection
     * @param transformator Transformator object to create mapping between JOOQ record and our entities
     */
    public RepositoryImpl(DSLContext jooq, Transformator<E, R> transformator) {
        this.jooq = jooq;
        this.transformator = transformator;
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
            persisted = jooq.insertInto(transformator.getTable())
                    .set(transformator.createRecord(entity))
                    .returning()
                    .fetchOne();

            transformedEntity = transformator.mapToEntity(persisted);
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

            int deletedRecordCount = jooq.delete(transformator.getTable())
                    .where(transformator.getTableId().equal(id))
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

            List<R> queryResults = jooq.selectFrom(transformator.getTable()).fetchInto(transformator.getRecordClass());

            for (R queryResult : queryResults) {
                E entry = transformator.mapToEntity(queryResult);
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

            Condition condition = transformator.getTableId().notEqual(-1);
            if (pageable.getCursor() != -1) {
                if (pageable.getSortDirection() == Pageable.SortDirection.ASC) {
                    condition = transformator.getTableId().greaterThan(pageable.getCursor());
                } else {
                    condition = transformator.getTableId().lessThan(pageable.getCursor());
                }
            }

            List<R> queryResults = jooq.selectFrom(transformator.getTable())
                    .where(condition)
                    .orderBy(transformator.getSortFields(pageable.getSortDirection()))
                    .limit(pageable.getLimit())
                    .fetchInto(transformator.getRecordClass());

            for (R queryResult : queryResults) {
                E entry = transformator.mapToEntity(queryResult);
                entries.add(entry);
            }

            result = new PaginationResult<>(pageable, entries);
        } catch (DataAccessException e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN, e.getMessage());
        }

        return result;
    }

    /**
     * @param searchTerm
     * @param pageable
     * @return PaginationResult with all the entities currently in the database matching the searchTerm
     * @throws ActivityTrackerException
     */
    @Override
    public PaginationResult<E> searchAll(String searchTerm, Pageable pageable) throws ActivityTrackerException {
        PaginationResult<E> result = null;
        try {
            List<E> entries = new ArrayList<>();

            Condition condition = transformator.getTableId().notEqual(-1);
            if (pageable.getCursor() != -1) {
                if (pageable.getSortDirection() == Pageable.SortDirection.ASC) {
                    condition = transformator.getTableId().greaterThan(pageable.getCursor());
                } else {
                    condition = transformator.getTableId().lessThan(pageable.getCursor());
                }
            }
            String likeExpression = "%" + searchTerm + "%";

            List<R> queryResults = jooq.selectFrom(transformator.getTable())
                    .where(transformator.getSearchFields(likeExpression)).and(condition)
                    .orderBy(transformator.getSortFields(pageable.getSortDirection()))
                    .limit(pageable.getLimit())
                    .fetchInto(transformator.getRecordClass());

            for (R queryResult : queryResults) {
                E entry = transformator.mapToEntity(queryResult);
                entries.add(entry);
            }

            result = new PaginationResult<>(pageable, entries);
        } catch (ActivityTrackerException ex) {
            ExceptionHandler.getInstance().convertAndThrowException(ex);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN);
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
            queryResult = jooq.selectFrom(transformator.getTable())
                    .where(transformator.getTableId().equal(id))
                    .fetchOne();

            if (queryResult == null) {
                throw new Exception("No " + transformator.getRecordClass() + " found with id: " + id);
            }
        } catch (ActivityTrackerException ex) {
            ExceptionHandler.getInstance().convertAndThrowException(ex);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.NOT_FOUND);
        }

        return transformator.mapToEntity(queryResult);
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
            UpdateSetFirstStep<R> update = jooq.update(transformator.getTable());
            Map<Field, Object> map = transformator.getUpdateMap(entity);
            UpdateSetMoreStep moreStep = null;
            for (Map.Entry<Field, Object> item : map.entrySet()) {
                Field key = item.getKey();
                Object value = item.getValue();
                if(moreStep == null)
                    moreStep = update.set(key, value);
                else
                    moreStep.set(key,value);
            }
            assert moreStep != null;
            moreStep.where(transformator.getTableId().equal(entity.getId())).execute();
            byId = findById(entity.getId());
        } catch (ActivityTrackerException ex) {
            ExceptionHandler.getInstance().convertAndThrowException(ex);
        } catch (Exception e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.REPOSITORY, ErrorCode.UNKNOWN);
        }
        return byId;
    }
}
