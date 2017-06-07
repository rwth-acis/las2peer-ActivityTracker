SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE reqbaztrack.activity;

REPLACE INTO reqbaztrack.activity (id, creation_date, activity_action, origin, data_url, data_type,
                                   data_frontend_url, parent_data_url, parent_data_type, user_url)
VALUES
  (1, '2015-10-21 07:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService',
   'http://requirements-bazaar.org/betabazaar/projects/1', 'PROJECT',
   'http://localhost:5000/projects/1', NULL, NULL, 'http://requirements-bazaar.org/betabazaar/users/2'),
  (2, '2015-10-21 07:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/categories/1', 'CATEGORY',
   'http://localhost:5000/projects/1/categories/1', 'http://requirements-bazaar.org/betabazaar/projects/1',
   'PROJECT', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (3, '2015-10-21 08:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/requirements/1', 'REQUIREMENT',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/categories/1',
   'CATEGORY', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (4, '2015-10-21 09:00:00', 'UPDATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/requirements/1', 'REQUIREMENT',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/categories/1',
   'CATEGORY', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (5, '2015-10-21 10:00:00', 'DELETE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', NULL, 'REQUIREMENT',
   NULL, 'http://requirements-bazaar.org/betabazaar/categories/1', 'CATEGORY', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (6, '2015-10-21 10:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/projects/2', 'PROJECT',
   'http://localhost:5000/projects/2', NULL, NULL, 'http://requirements-bazaar.org/betabazaar/users/2'),
  (7, '2015-10-21 10:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/categories/2', 'CATEGORY',
   'http://localhost:5000/projects/2/categories/2', 'http://requirements-bazaar.org/betabazaar/projects/2',
   'PROJECT', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (8, '2015-10-21 11:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/requirements/2', 'REQUIREMENT',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/categories/1',
   'CATEGORY', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (9, '2015-10-21 12:00:00', 'UPDATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/requirements/2', 'REQUIREMENT',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/categories/1',
   'CATEGORY', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (10, '2015-10-21 13:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/comments/1', 'COMMENT',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/requirements/1',
   'REQUIREMENT', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (11, '2015-10-21 14:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', 'http://requirements-bazaar.org/betabazaar/comments/2', 'COMMENT',
   'http://localhost:5000/projects/1/categories/1/requirements/2', 'http://requirements-bazaar.org/betabazaar/requirements/2',
   'REQUIREMENT', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (12, '2015-10-21 15:00:00', 'CREATE', 'de.rwth.dbis.acis.bazaar.service.BazaarService',
   'http://requirements-bazaar.org/betabazaar/requirements/1', 'VOTE',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/requirements/1',
   'REQUIREMENT', 'http://requirements-bazaar.org/betabazaar/users/2'),
  (13, '2015-10-21 15:00:00', 'DELETE', 'de.rwth.dbis.acis.bazaar.service.BazaarService', NULL, 'VOTE',
   'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/betabazaar/requirements/1',
   'REQUIREMENT', 'http://requirements-bazaar.org/betabazaar/users/2');

SET FOREIGN_KEY_CHECKS = 1;







