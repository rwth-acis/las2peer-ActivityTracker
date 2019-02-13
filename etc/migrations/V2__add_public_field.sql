SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE reqbaztrack.activity ADD public BOOLEAN DEFAULT 1;

UPDATE reqbaztrack.activity SET public = 1;

SET FOREIGN_KEY_CHECKS = 1;
