INSERT INTO users (id, email, first_name, last_name, password, is_deleted) VALUES
(1, 'customer@example.com', 'John', 'Doe', '$2a$10$dummyhashedpassword', false),
(2, 'manager@example.com', 'Jane', 'Smith', '$2a$10$dummyhashedpassword', false);

INSERT INTO roles (id, name) VALUES
                                 (1, 'ROLE_CUSTOMER'),
                                 (2, 'ROLE_MANAGER');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1), (2, 2);

INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted) VALUES
(1, 'M3', 'BMW', 'SEDAN', 5, 100.00, false),
(2, 'A6', 'Audi', 'SUV', 3, 80.00, false);

-- Mix of active and completed rentals for user 1
INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted) VALUES
(1, '2024-01-01', '2024-01-10', NULL, 1, 1, false), -- active
(2, '2023-12-01', '2023-12-10', '2023-12-09', 2, 1, false), -- completed
(3, '2024-01-05', '2024-01-15', NULL, 2, 1, false), -- active
(4, '2023-11-15', '2023-11-25', '2023-11-24', 1, 1, false); -- completed
