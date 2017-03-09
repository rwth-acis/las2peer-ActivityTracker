package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import java.util.Date;

public class Activity extends EntityBase {

    private final Date creationTime;
    private final String activityAction;
    private final String dataUrl;
    private final String dataType;
    private final String dataFrontendUrl;
    private final String parentDataUrl;
    private final String parentDataType;
    private final String userUrl;

    public Date getCreationTime() {
        return creationTime;
    }

    public String getActivityAction() {
        return activityAction;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDataFrontendUrl() {
        return dataFrontendUrl;
    }

    public String getParentDataUrl() {
        return parentDataUrl;
    }

    public String getParentDataType() {
        return parentDataType;
    }

    public String getUserUrl() {
        return userUrl;
    }

    protected Activity(Builder builder) {
        super(builder);
        this.creationTime = builder.creationTime;
        this.activityAction = builder.activityAction;
        this.dataUrl = builder.dataUrl;
        this.dataType = builder.dataType;
        this.dataFrontendUrl = builder.dataFrontendUrl;
        this.parentDataUrl = builder.parentDataUrl;
        this.parentDataType = builder.parentDataType;
        this.userUrl = builder.userUrl;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        protected int id;
        protected Date creationTime;
        protected String activityAction;
        protected String dataUrl;
        protected String dataType;
        protected String dataFrontendUrl;
        protected String parentDataUrl;
        protected String parentDataType;
        protected String userUrl;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder creationTime(Date creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder activityAction(String activityAction) {
            this.activityAction = activityAction;
            return this;
        }

        public Builder dataUrl(String dataUrl) {
            this.dataUrl = dataUrl;
            return this;
        }

        public Builder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        public Builder dataFrontendUrl(String dataFrontendUrl) {
            this.dataFrontendUrl = dataFrontendUrl;
            return this;
        }

        public Builder parentDataUrl(String parentDataUrl) {
            this.parentDataUrl = parentDataUrl;
            return this;
        }

        public Builder parentDataType(String parentDataType) {
            this.parentDataType = parentDataType;
            return this;
        }

        public Builder userUrl(String userUrl) {
            this.userUrl = userUrl;
            return this;
        }

        public Activity build() {
            Activity created = new Activity(this);
            return created;
        }
    }
}
