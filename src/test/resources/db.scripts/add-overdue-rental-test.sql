INSERT INTO users (id, email, first_name, last_name, password, is_deleted) VALUES
    (1, 'customer@example.com', 'John', 'Doe', '$2a$10$dummyhashedpassword', false);

INSERT INTO roles (id, name) VALUES (1, 'ROLE_CUSTOMER');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);

INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted) VALUES
    (1, 'M3', 'BMW', 'SEDAN', 4, 100.00, false);

-- Overdue rental (return_date in the past, actual_return_date is NULL)
INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted) VALUES
    (1, '2023-12-01', '2023-12-10', NULL, 1, 1, false);
