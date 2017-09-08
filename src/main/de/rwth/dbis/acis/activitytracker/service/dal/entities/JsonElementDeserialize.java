package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Martin on 29.05.2017.
 */
public class JsonElementDeserialize extends JsonDeserializer<JsonNode> {

    public JsonElementDeserialize() {
    }

    @Override
    public JsonNode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonParser);
        return actualObj;
    }
}