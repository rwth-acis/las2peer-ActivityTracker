package de.rwth.dbis.acis.activitytracker.service.dal.repositories;

import de.rwth.dbis.acis.activitytracker.service.dal.entities.EntityBase;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.Pageable;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.PaginationResult;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;

import java.util.List;

/**
 * @param <E> Type of the Entity, which should be added, deleted, updated, getted using the repo.
 */
public interface Repository<E extends EntityBase> {
    /**
     * @param entity to add
     * @return the persisted entity
     * @throws ActivityTrackerException
     */
    public E add(E entity) throws ActivityTrackerException;


    /**
     * @param id of an entity, which should be deleted
     * @return the deleted entity. It is not anymore in the database!
     * @throws ActivityTrackerException
     */
    public E delete(int id) throws ActivityTrackerException;


    /**
     * @return all the entities currently in the database
     * @throws ActivityTrackerException
     */
    public List<E> findAll() throws ActivityTrackerException;


    /**
     * @param pageable
     * @return PaginationResult with all the entities currently in the database
     * @throws ActivityTrackerException
     */
    public PaginationResult<E> findAll(Pageable pageable) throws ActivityTrackerException;


    /**
     * @param searchTerm
     * @param pageable
     * @return PaginationResult with all the entities currently in the database matching the searchTerm
     * @throws ActivityTrackerException
     */
    public PaginationResult<E> searchAll(String searchTerm, Pageable pageable) throws ActivityTrackerException;

    /**
     * @param id of the entity we are looking for
     * @return the entity from the database with the given Id
     * @throws ActivityTrackerException
     */
    public E findById(int id) throws ActivityTrackerException;


    /**
     * @param entity object, which holds the new values of the database update
     * @return the entity after the database
     * @throws ActivityTrackerException
     */
    public E update(E entity) throws ActivityTrackerException;
}
