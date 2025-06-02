-- users
--INSERT INTO users (email, password, first_name, last_name, image_url, enable, account_locked, is_using_mfa, active, username)
--VALUES ('admin@example.com', '$2a$10$SomeEncodedPasswordHash', 'Admin', 'User', null, true, false, false, true, 'admin@example.com');

-- roles
--INSERT INTO authorities (user_id, authority)
--VALUES (1, 'ROLE_ADMIN');


-- 1. Create Permissions
--INSERT INTO permission (id, name) VALUES (1, 'USER_READ');
--INSERT INTO permission (id, name) VALUES (2, 'USER_UPDATE');
--INSERT INTO permission (id, name) VALUES (3, 'USER_DELETE');
--INSERT INTO permission (id, name) VALUES (4, 'PRODUCT_CREATE');
--
---- 2. Create Roles
--INSERT INTO role (id, name) VALUES (1, 'USER');
--INSERT INTO role (id, name) VALUES (2, 'ADMIN');
--
---- 3. Map Roles to Permissions
---- USER role permissions
--INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 1); -- USER -> USER_READ
--
---- ADMIN role permissions
--INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 1); -- ADMIN -> USER_READ
--INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 2); -- ADMIN -> USER_UPDATE
--INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 3); -- ADMIN -> USER_DELETE
--INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 4); -- ADMIN -> PRODUCT_CREATE;

select 1 + 1;


