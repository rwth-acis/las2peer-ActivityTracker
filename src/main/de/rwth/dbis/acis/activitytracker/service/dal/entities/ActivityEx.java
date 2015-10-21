package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import de.rwth.dbis.acis.activitytracker.service.dal.helpers.ActivityAction;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.DataType;

import java.util.Date;

public class ActivityEx extends Activity {

    private String data;

    private String user;

    private ActivityEx(BuilderEx builderEx) {
        super(builderEx);
        this.data = builderEx.data;
        this.user = builderEx.user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static BuilderEx getBuilderEx() {
        return new BuilderEx();
    }

    public static class BuilderEx extends Builder {

        private String data;
        private String user;

        public BuilderEx() {
            super();
        }

        public BuilderEx activity(Activity activity) {
            creationTime(activity.getCreationTime());
            activityAction(activity.getActivityAction());
            dataUrl(activity.getDataUrl());
            dataType(activity.getDataType());
            userUrl(activity.getUserUrl());
            return this;
        }

        @Override
        public BuilderEx creationTime(Date creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        @Override
        public BuilderEx activityAction(ActivityAction activityAction) {
            this.activityAction = activityAction;
            return this;
        }

        @Override
        public BuilderEx dataUrl(String dataUrl) {
            this.dataUrl = dataUrl;
            return this;
        }

        @Override
        public BuilderEx dataType(DataType dataType) {
            this.dataType = dataType;
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