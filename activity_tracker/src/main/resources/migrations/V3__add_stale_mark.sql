SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE reqbaztrack.activity ADD stale BOOLEAN DEFAULT FALSE;

UPDATE reqbaztrack.activity SET stale = FALSE;

SET FOREIGN_KEY_CHECKS = 1;
