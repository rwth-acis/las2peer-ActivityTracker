SET FOREIGN_KEY_CHECKS = 0;

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('1', '2015-10-21 07:00:00', 'CREATE', 'http://localhost:8080/bazaar/projects/1', 'PROJECT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('2', '2015-10-21 07:00:00', 'CREATE', 'http://localhost:8080/bazaar/components/1', 'COMPONENT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('3', '2015-10-21 08:00:00', 'CREATE', 'http://localhost:8080/bazaar/requirements/1', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('4', '2015-10-21 09:00:00', 'UPDATE', 'http://localhost:8080/bazaar/requirements/1', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('5', '2015-10-21 10:00:00', 'DELETE', '', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/1');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('6', '2015-10-21 10:00:00', 'CREATE', 'http://localhost:8080/bazaar/projects/2', 'PROJECT',
        'http://localhost:8080/bazaar/users/2');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('7', '2015-10-21 07:00:00', 'CREATE', 'http://localhost:8080/bazaar/components/2', 'COMPONENT',
        'http://localhost:8080/bazaar/users/2');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('8', '2015-10-21 11:00:00', 'CREATE', 'http://localhost:8080/bazaar/requirements/2', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/2');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('9', '2015-10-21 12:00:00', 'UPDATE', 'http://localhost:8080/bazaar/requirements/2', 'REQUIREMENT',
        'http://localhost:8080/bazaar/users/2');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('10', '2015-10-21 13:00:00', 'CREATE', 'http://localhost:8080/bazaar/comments/1', 'COMMENT',
        'http://localhost:8080/bazaar/users/3');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('11', '2015-10-21 14:00:00', 'CREATE', 'http://localhost:8080/bazaar/comments/2', 'COMMENT',
        'http://localhost:8080/bazaar/users/2');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('12', '2015-10-21 15:00:00', 'CREATE', 'http://localhost:8080/bazaar/requirements/1', 'VOTE',
        'http://localhost:8080/bazaar/users/2');

REPLACE INTO `reqbaztrack`.`activity` (`Id`, `creation_time`, `activity_action`, `data_url`, `data_type`, `user_url`)
VALUES ('13', '2015-10-21 15:00:00', 'DELETE', '', 'VOTE',
        'http://localhost:8080/bazaar/users/2');

SET FOREIGN_KEY_CHECKS = 1;









