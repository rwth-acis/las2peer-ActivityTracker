package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rwth.dbis.acis.activitytracker.service.exception.ActivityTrackerException;
import de.rwth.dbis.acis.activitytracker.service.exception.ErrorCode;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionHandler;
import de.rwth.dbis.acis.activitytracker.service.exception.ExceptionLocation;

public abstract class EntityBase implements IdentifiedById {

    public String toJSON() throws ActivityTrackerException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            ExceptionHandler.getInstance().convertAndThrowException(e, ExceptionLocation.DALFACADE, ErrorCode.SERILIZATION_PROBLEM);
        }
        return jsonInString;
    }
}
