package de.rwth.dbis.acis.activitytracker.service.dal.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Jacksonized
@Builder(builderClassName = "Builder", toBuilder = true)
public class Activity extends EntityBase {

    private int id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private OffsetDateTime creationDate;

    @NotNull
    @Size(min = 1, max = 255)
    private String activityAction;

    @NotNull
    @Size(min = 1, max = 255)
    private String origin;

    @Size(max = 255)
    private String dataUrl;

    @Size(max = 255)
    private String dataType;

    @Size(max = 255)
    private String dataFrontendUrl;

    @Size(max = 255)
    private String parentDataUrl;

    @Size(max = 255)
    private String parentDataType;

    @Size(max = 255)
    private String userUrl;

    @JsonIgnore
    @lombok.Builder.Default
    private Boolean stale = false;

    @JsonIgnore
    @lombok.Builder.Default
    private boolean publicActivity = true;

    @JsonDeserialize(using = JsonElementDeserialize.class)
    private JsonNode additionalObject;

    private Object data;

    private Object parentData;

    private Object user;
}
