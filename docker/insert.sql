-- chrisalanapazaa@gmail.com user
INSERT INTO "user" (file_photo_id, first_name, last_name, email, username, password, phone, description, status, tx_date, tx_user, tx_host) 
VALUES (1, 'Chris', 'Apaza', 'chrisalanapazaa@gmail.com', 'chrisalanapazaa@gmail.com','$2a$12$JOteLJjWHDHBFX3Nne4XTuEL9w8z3bEoTfIiyw9eGxTSl6LM4f/ci', '', '', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- Groups
INSERT INTO "group" (group_name, group_description, status, tx_date, tx_user, tx_host)
VALUES  ('DIRECTOR', 'Director del laboratorio multimedia', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('DOCENTE', 'Docente del laboratorio multimedia', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('AUXILIAR', 'Auxiliar del laboratorio multimedia', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- Roles / permisos
INSERT INTO "role" (role_name, role_description, status, tx_date, tx_user, tx_host)
VALUES  ('VER DASHBOARD', 'Acceso al dashboard del sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER PROYECTOS', 'Acceso a la lista de proyectos', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('CREAR PROYECTOS', 'Crear proyectos en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('EDITAR PROYECTOS', 'Editar proyectos en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER TAREAS', 'Ver tareas en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('CREAR TAREAS', 'Crear tareas en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('EDITAR TAREAS', 'Editar tareas en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER REPORTES GENERADOS', 'Ver reportes del sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER REPORTES EJECUTIVOS', 'Ver reportes ejecutivos', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER REPORTES DE PROYECTOS', 'Ver reportes de proyectos', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER REPORTES DE TAREAS', 'Ver reportes de tareas', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('VER USUARIOS', 'Ver usuarios del sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('CREAR USUARIOS', 'Crear usuarios en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('EDITAR USUARIOS', 'Editar usuarios en el sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('GESTIONAR ROLES Y PERMISOS', 'Gestionar roles y permisos del sistema', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- User Group
INSERT INTO "user_group" (user_id, group_id, status, tx_date, tx_user, tx_host)
VALUES  (1, 1, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- Group Role - Director
INSERT INTO "group_role" (group_id, role_id, status, tx_date, tx_user, tx_host)
VALUES  (1, 1, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 2, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 3, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 4, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 5, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 6, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 7, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 8, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 9, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 10, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 11, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 12, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 13, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 14, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (1, 15, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- Group Role - Docente
INSERT INTO "group_role" (group_id, role_id, status, tx_date, tx_user, tx_host)
VALUES  (2, 1, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 2, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 5, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 6, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 7, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 8, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 9, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 10, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 11, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (2, 12, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- Group Role - Auxiliar
INSERT INTO "group_role" (group_id, role_id, status, tx_date, tx_user, tx_host)
VALUES  (3, 2, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        (3, 5, true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');
        
-- Task Status
INSERT INTO "task_status" (task_status_name, status, tx_date, tx_user, tx_host)
VALUES  ('PENDIENTE', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('EN PROGRESO', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('FINALIZADO', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');

-- Task Priority
INSERT INTO "task_priority" (task_priority_name, status, tx_date, tx_user, tx_host)
VALUES  ('BAJA', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('MEDIA', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost'),
        ('ALTA', true, CURRENT_TIMESTAMP, 'chrisalanapazaa@gmail.com', 'localhost');
        