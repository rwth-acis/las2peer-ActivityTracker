SET FOREIGN_KEY_CHECKS = 0;

-- tables
-- Table activity
CREATE TABLE IF NOT EXISTS reqbaztrack.activity (
  id                INT          NOT NULL  AUTO_INCREMENT,
  creation_date     TIMESTAMP    NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  activity_action   VARCHAR(255) NOT NULL,
  origin            VARCHAR(255) NOT NULL,
  data_url          VARCHAR(255) NULL,
  data_type         VARCHAR(255) NULL,
  data_frontend_url VARCHAR(255) NULL,
  parent_data_url   VARCHAR(255) NULL,
  parent_data_type  VARCHAR(255) NULL,
  user_url          VARCHAR(255) NULL,
  additional_object JSON         NULL,
  CONSTRAINT activity_pk PRIMARY KEY (id)
);

SET FOREIGN_KEY_CHECKS = 1;