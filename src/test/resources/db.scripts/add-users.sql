INSERT INTO users (id, email, first_name, last_name, password, is_deleted) VALUES
(1, 'customer@example.com', 'John', 'Doe', '$2a$10$dummyhashedpassword', false),
(2, 'manager@example.com', 'Jane', 'Smith', '$2a$10$dummyhashedpassword', false);

INSERT INTO roles (id, name) VALUES
(1, 'ROLE_CUSTOMER'),
(2, 'ROLE_MANAGER');

INSERT INTO users_roles (user_id, role_id) VALUES
(1, 1),
(2, 2);