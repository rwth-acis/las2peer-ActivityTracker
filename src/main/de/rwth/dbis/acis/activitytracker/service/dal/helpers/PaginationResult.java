package de.rwth.dbis.acis.activitytracker.service.dal.helpers;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.dbis.acis.activitytracker.service.dal.entities.EntityBase;

import java.util.List;

public class PaginationResult<T extends EntityBase> {

    private Pageable pageable;
    private List<T> elements;

    public PaginationResult(Pageable pageable, List<T> elements) {
        this.pageable = pageable;
        this.elements = elements;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public List<T> getElements() {
        return elements;
    }

    public int getPrevCursor() {
        if (this.getElements().isEmpty()) {
            return -1;
        } else {
            return this.getElements().get(0).getId();
        }

    }

    public int getNextCursor() {
        if (this.getElements().isEmpty()) {
            return -1;
        } else {
            return this.getElements().get(this.getElements().size() - 1).getId();
        }
    }

    public String toJSON() throws JsonProcessingException {
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(this.getElements());
    }

}

