package de.rwth.dbis.acis.activitytracker.service.dal.helpers;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    public void trim(int maxSize) {
        if(elements.size() > maxSize) {
            if (pageable.getSortDirection() == Pageable.SortDirection.ASC) {
                pageable.setAfterCursor(pageable.getAfterCursor() -
                        (elements.size() - maxSize));
            } else {
                pageable.setBeforeCursor(pageable.getBeforeCursor() +
                        (elements.size() - maxSize));
            }
            elements = elements.subList(0, maxSize);
        }
    }

    public String toJSON() throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(this.getElements());
    }

}
