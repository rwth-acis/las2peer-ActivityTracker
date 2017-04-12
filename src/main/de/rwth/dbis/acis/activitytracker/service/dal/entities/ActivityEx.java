package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import java.util.Date;

public class ActivityEx extends Activity {

    private Object data;
    private Object parentData;
    private Object user;

    private ActivityEx(BuilderEx builderEx) {
        super(builderEx);
        this.data = builderEx.data;
        this.parentData = builderEx.parentData;
        this.user = builderEx.user;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getParentData() {
        return parentData;
    }

    public void setParentData(Object parentData) {
        this.parentData = parentData;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public static BuilderEx getBuilderEx() {
        return new BuilderEx();
    }

    public static class BuilderEx extends Builder {

        private Object data;
        private Object parentData;
        private Object user;

        public BuilderEx() {
            super();
        }

        public BuilderEx activity(Activity activity) {
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

        public BuilderEx data(Object data) {
            this.data = data;
            return this;
        }

        public BuilderEx parentData(Object parentData) {
            this.parentData = parentData;
            return this;
        }

        public BuilderEx user(Object user) {
            this.user = user;
            return this;
        }

        @Override
        public BuilderEx creationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        @Override
        public BuilderEx activityAction(String activityAction) {
            this.activityAction = activityAction;
            return this;
        }

        @Override
        public BuilderEx dataUrl(String dataUrl) {
            this.dataUrl = dataUrl;
            return this;
        }

        @Override
        public BuilderEx dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }

        @Override
        public Builder dataFrontendUrl(String dataFrontendUrl) {
            this.dataFrontendUrl = dataFrontendUrl;
            return this;
        }

        @Override
        public Builder parentDataUrl(String parentDataUrl) {
            this.parentDataUrl = parentDataUrl;
            return this;
        }

        @Override
        public Builder parentDataType(String parentDataType) {
            this.parentDataType = parentDataType;
            return this;
        }

        @Override
        public BuilderEx userUrl(String userUrl) {
            this.userUrl = userUrl;
            return this;
        }

        @Override
        public ActivityEx build() {
            return new ActivityEx(this);
        }
    }
}
