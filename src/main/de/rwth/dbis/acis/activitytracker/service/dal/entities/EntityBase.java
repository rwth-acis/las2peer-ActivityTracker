package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import com.google.gson.Gson;

public abstract class EntityBase implements IdentifiedById {

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
