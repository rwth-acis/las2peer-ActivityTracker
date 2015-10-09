package de.rwth.dbis.acis.activitytracker.service.dal.entities;

public interface IdentifiedById {
    /**
     * @return Returns the identifier of the implementer. All entities should implement this.
     */
    public int getId();
}
