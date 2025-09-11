INSERT INTO users (id, email, first_name, last_name, password, is_deleted) VALUES
    (1, 'customer@example.com', 'John', 'Doe', '$2a$10$dummyhashedpassword', false);

INSERT INTO roles (id, name) VALUES
    (1, 'ROLE_CUSTOMER');

INSERT INTO users_roles (user_id, role_id) VALUES
    (1, 1);

INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted) VALUES
    (1, 'M3', 'BMW', 'SEDAN', 100, 100.56, false);

INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id, is_deleted) VALUES
    (1, '2024-01-01', '2024-01-05', NULL, 1, 1, false);

INSERT INTO payments (id, status, type, rental_id, session_url, session, amount_to_pay, is_deleted) VALUES
(1, 'PENDING', 'PAYMENT', 1, 'https://checkout.stripe.com/session', 'cs_test_111', 444.44, false),
(2, 'PAID', 'PAYMENT', 1, 'https://checkout.stripe.com/session2', 'cs_test_222', 100.00, false);