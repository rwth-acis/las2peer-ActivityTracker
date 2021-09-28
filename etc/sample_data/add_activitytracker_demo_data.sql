SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE reqbaztrack.activity;

REPLACE INTO reqbaztrack.activity (id, creation_date, activity_action, origin, data_url, data_type,
                                   data_frontend_url, parent_data_url, parent_data_type, user_url, additional_object)
VALUES
  (1, '2015-10-21 07:00:00', 'CREATE', 'https://requirements-bazaar.org/beta',
      'http://requirements-bazaar.org/beta/bazaar/projects/1', 'PROJECT',
      'http://localhost:5000/projects/1', NULL, NULL, 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}}'),
  (2, '2015-10-21 07:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/categories/1', 'CATEGORY',
      'http://localhost:5000/projects/1/categories/1', 'http://requirements-bazaar.org/beta/bazaar/projects/1',
      'PROJECT', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}}'),
  (3, '2015-10-21 08:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/requirements/1', 'REQUIREMENT',
      'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/beta/bazaar/categories/1',
      'CATEGORY', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 1, "name": "Requirement 1"}}'),
  (4, '2015-10-21 09:00:00', 'UPDATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/requirements/1', 'REQUIREMENT',
      'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/beta/bazaar/categories/1',
      'CATEGORY', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 1, "name": "Requirement 1 updated"}}'),
  (5, '2015-10-21 10:00:00', 'REALIZE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/requirements/1', 'REQUIREMENT',
      'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/beta/bazaar/categories/1', 'CATEGORY', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 1, "name": "Requirement 1 updated"}}'),
  (6, '2015-10-21 10:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/projects/2', 'PROJECT',
      'http://localhost:5000/projects/2', NULL, NULL, 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 2, "name": "Project 2"}}'),
  (7, '2015-10-21 10:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/categories/2', 'CATEGORY',
      'http://localhost:5000/projects/2/categories/2', 'http://requirements-bazaar.org/beta/bazaar/projects/2',
      'PROJECT', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 2, "name": "Project 2"}, "category": {"id": 2, "name": "Category 2"}}'),
  (8, '2015-10-21 11:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/requirements/2', 'REQUIREMENT',
      'http://localhost:5000/projects/1/categories/1/requirements/2', 'http://requirements-bazaar.org/beta/bazaar/categories/1',
      'CATEGORY', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 2, "name": "Requirement 2"}}'),
  (9, '2015-10-21 12:00:00', 'UPDATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/requirements/2', 'REQUIREMENT',
      'http://localhost:5000/projects/1/categories/1/requirements/2', 'http://requirements-bazaar.org/beta/bazaar/categories/1',
      'CATEGORY', 'http://requirements-bazaar.org/beta/bazaar/users/2',
      '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 2, "name": "Requirement 2 updated"}}'),
  (10, '2015-10-21 13:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/comments/1', 'COMMENT',
       'http://localhost:5000/projects/1/categories/1/requirements/1', 'http://requirements-bazaar.org/beta/bazaar/requirements/1',
       'REQUIREMENT', 'http://requirements-bazaar.org/beta/bazaar/users/2',
       '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 1, "name": "Requirement 1 updated"}}'),
  (11, '2015-10-21 14:00:00', 'CREATE', 'https://requirements-bazaar.org/beta', 'http://requirements-bazaar.org/beta/bazaar/comments/2', 'COMMENT',
       'http://localhost:5000/projects/1/categories/1/requirements/2', 'http://requirements-bazaar.org/beta/bazaar/requirements/2',
       'REQUIREMENT', 'http://requirements-bazaar.org/beta/bazaar/users/2',
       '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 2, "name": "Requirement 2"}}'),
  (12, '2015-10-21 15:00:00', 'CREATE', 'https://requirements-bazaar.org/beta',
   'http://requirements-bazaar.org/beta/bazaar/requirements/1', 'VOTE',
   'http://localhost:5000/projects/1/categories/1/requirements/1',
   'http://requirements-bazaar.org/beta/bazaar/requirements/1',
   'REQUIREMENT', 'http://requirements-bazaar.org/beta/bazaar/users/2',
   '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 1, "name": "Requirement 1 updated"}}'),
  (13, '2015-10-21 15:00:00', 'CREATE', 'https://requirements-bazaar.org/beta',
   'http://requirements-bazaar.org/beta/bazaar/requirements/1',
   'VOTE', 'http://localhost:5000/projects/1/categories/1/requirements/1',
   'http://requirements-bazaar.org/beta/bazaar/requirements/1',
   'REQUIREMENT', 'http://requirements-bazaar.org/beta/bazaar/users/3',
   '{"project": {"id": 1, "name": "Project 1"}, "category": {"id": 1, "name": "Category 1"}, "requirement": {"id": 1, "name": "Requirement 1 updated"}}');

SET FOREIGN_KEY_CHECKS = 1;







