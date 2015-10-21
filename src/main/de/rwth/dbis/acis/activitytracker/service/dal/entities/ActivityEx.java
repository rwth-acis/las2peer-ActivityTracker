package de.rwth.dbis.acis.activitytracker.service.dal.entities;

import de.rwth.dbis.acis.activitytracker.service.dal.helpers.ActivityAction;
import de.rwth.dbis.acis.activitytracker.service.dal.helpers.DataType;

import java.util.Date;

public class ActivityEx extends Activity {

    private final Object data;
    private final Object user;

    private ActivityEx(BuilderEx builderEx) {
        super(builderEx);
        this.data = builderEx.data;
        this.user = builderEx.user;
    }

    public static BuilderEx getBuilderEx() {
        return new BuilderEx();
    }

    public static class BuilderEx extends Builder {

        private Object data;
        private Object user;

        public BuilderEx() {
            super();
        }

        @Override
        public Builder creationTime(Date creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        @Override
        public Builder activityAction(ActivityAction activityAction) {
            this.activityAction = activityAction;
            return this;
        }

        @Override
        public Builder dataUrl(String dataUrl) {
            this.dataUrl = dataUrl;
            return this;
        }

        @Override
        public Builder dataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        @Override
        public Builder userUrl(String userUrl) {
            this.userUrl = userUrl;
            return this;
        }

        @Override
        public ActivityEx build() {
            return new ActivityEx(this);
        }
    }
}
