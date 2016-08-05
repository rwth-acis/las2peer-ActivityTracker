package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import com.google.gson.Gson;

public abstract class EntityBase implements IdentifiedById {

    private final int id;

    public EntityBase(Activity.Builder builder) {
        this.id = builder.id;
    }

    @Override
    public int getId() {
        return id;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    // TODO: Add an abstract Builder to avoid import Activity.Builder here
}
