SET FOREIGN_KEY_CHECKS = 0;

-- tables
-- Table activities
CREATE TABLE IF NOT EXISTS activity (
  Id               INT          NOT NULL  AUTO_INCREMENT,
  creation_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  activity_action  VARCHAR(255) NOT NULL,
  data_url         VARCHAR(255) NULL,
  data_type        VARCHAR(255) NOT NULL,
  user_url         VARCHAR(255) NULL,
  CONSTRAINT activity_pk PRIMARY KEY (Id)
);

SET FOREIGN_KEY_CHECKS = 1;