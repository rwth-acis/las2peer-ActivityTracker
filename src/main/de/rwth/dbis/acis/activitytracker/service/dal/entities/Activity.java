package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import java.util.Date;

public class Activity extends EntityBase {

    private int id;
    private Date creationDate;
    private String activityAction;
    private String dataUrl;
    private String dataType;
    private String dataFrontendUrl;
    private String parentDataUrl;
    private String parentDataType;
    private String userUrl;
    private Object data;
    private Object parentData;
    private Object user;

    public Activity() {
    }

    protected Activity(Builder builder) {
        this.id = builder.id;
        this.creationDate = builder.creationDate;
        this.activityAction = builder.activityAction;
        this.dataUrl = builder.dataUrl;
        this.dataType = builder.dataType;
        this.dataFrontendUrl = builder.dataFrontendUrl;
        this.parentDataUrl = builder.parentDataUrl;
        this.parentDataType = builder.parentDataType;
        this.userUrl = builder.userUrl;
        this.data = builder.data;
        this.parentData = builder.parentData;
        this.user = builder.user;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public int getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
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

    public Object getData() {
        return data;
    }

    public Object getParentData() {
        return parentData;
    }

    public Object getUser() {
        return user;
    }

    public static class Builder {

        private int id;
        private Date creationDate;
        private String activityAction;
        private String dataUrl;
        private String dataType;
        private String dataFrontendUrl;
        private String parentDataUrl;
        private String parentDataType;
        private String userUrl;
        private Object data;
        private Object parentData;
        private Object user;

        public Builder activity(Activity activity) {
            id(activity.getId());
            creationDate(activity.getCreationDate());
            activityAction(activity.getActivityAction());
            dataUrl(activity.getDataUrl());
            dataType(activity.getDataType());
            dataFrontendUrl(activity.getDataFrontendUrl());
            parentDataUrl(activity.getParentDataUrl());
            parentDataType(activity.getParentDataType());
            userUrl(activity.getUserUrl());
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder creationDate(Date creationDate) {
            this.creationDate = creationDate;
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

        public Activity.Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Activity.Builder parentData(Object parentData) {
            this.parentData = parentData;
            return this;
        }

        public Activity.Builder user(Object user) {
            this.user = user;
            return this;
        }

        public Activity build() {
            Activity created = new Activity(this);
            return created;
        }
    }
}
