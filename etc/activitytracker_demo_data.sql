SET FOREIGN_KEY_CHECKS = 0;

REPLACE INTO `activitytracker`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('1', '2015-10-21 07:00:00', 'CREATE', 'http://localhost:8080/bazaar/projects/1', 'PROJECT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `activitytracker`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('2', '2015-10-21 07:00:00', 'CREATE', 'http://localhost:8080/bazaar/components/1', 'COMPONENT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `activitytracker`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('3', '2015-10-21 08:00:00', 'CREATE', 'http://localhost:8080/bazaar/requirements/1', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `activitytracker`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('4', '2015-10-21 09:00:00', 'UPDATE', 'http://localhost:8080/bazaar/requirements/1', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/1');

SET FOREIGN_KEY_CHECKS = 1;









