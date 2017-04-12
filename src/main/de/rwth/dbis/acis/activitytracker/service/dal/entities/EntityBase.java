package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import com.google.gson.Gson;

public abstract class EntityBase implements IdentifiedById {

    // TODO: use this method in Activity
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
