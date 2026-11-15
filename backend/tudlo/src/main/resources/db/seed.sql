INSERT INTO `user` (full_name, email, password_hash, user_type, status, created_at)
SELECT 'Tudlo Admin', 'admin@tudlo.test',
       '$2b$10$HkoY1lU1EeAY0Tb1lPGB9eFUIODTWEVCkekoL5PXrcyMyxNVzrrtu',
       'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE email = 'admin@tudlo.test');

INSERT INTO `user` (full_name, email, password_hash, user_type, status, created_at)
SELECT 'Tudlo Teacher', 'teacher@tudlo.test',
       '$2b$10$HkoY1lU1EeAY0Tb1lPGB9eFUIODTWEVCkekoL5PXrcyMyxNVzrrtu',
       'TEACHER', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE email = 'teacher@tudlo.test');

INSERT INTO `user` (full_name, email, password_hash, user_type, status, created_at)
SELECT 'Tudlo Student', 'student1@tudlo.test',
       '$2b$10$HkoY1lU1EeAY0Tb1lPGB9eFUIODTWEVCkekoL5PXrcyMyxNVzrrtu',
       'STUDENT', 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE email = 'student1@tudlo.test');

INSERT INTO admin (user_id)
SELECT u.user_id FROM `user` u
WHERE u.email = 'admin@tudlo.test'
  AND NOT EXISTS (SELECT 1 FROM admin a WHERE a.user_id = u.user_id);

INSERT INTO teacher (user_id)
SELECT u.user_id FROM `user` u
WHERE u.email = 'teacher@tudlo.test'
  AND NOT EXISTS (SELECT 1 FROM teacher t WHERE t.user_id = u.user_id);

INSERT INTO student (user_id, student_no)
SELECT u.user_id, 'TU-0001' FROM `user` u
WHERE u.email = 'student1@tudlo.test'
  AND NOT EXISTS (SELECT 1 FROM student s WHERE s.user_id = u.user_id);
