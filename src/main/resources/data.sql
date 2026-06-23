-- Insert default admin (password: password123)
INSERT INTO employees (name, email, password, role, department, joining_date, active)
SELECT 'Admin User', 'admin@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN', 'Administration', '2024-01-01', true
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'admin@company.com');

-- Insert default manager (password: password123)
INSERT INTO employees (name, email, password, role, department, joining_date, active)
SELECT 'John Manager', 'manager@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_MANAGER', 'Engineering', '2024-01-15', true
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'manager@company.com');

-- Insert default employee (password: password123, manager_id references the manager)
INSERT INTO employees (name, email, password, role, department, manager_id, joining_date, active)
SELECT 'Jane Employee', 'employee@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_EMPLOYEE', 'Engineering',
    (SELECT id FROM employees WHERE email = 'manager@company.com'), '2024-02-01', true
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'employee@company.com');

-- Initialize leave balances for admin
INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'CASUAL', 12, 0, YEAR(CURDATE()) FROM employees WHERE email = 'admin@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'admin@company.com') AND leave_type = 'CASUAL' AND year = YEAR(CURDATE()));

INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'SICK', 10, 0, YEAR(CURDATE()) FROM employees WHERE email = 'admin@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'admin@company.com') AND leave_type = 'SICK' AND year = YEAR(CURDATE()));

INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'EARNED', 15, 0, YEAR(CURDATE()) FROM employees WHERE email = 'admin@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'admin@company.com') AND leave_type = 'EARNED' AND year = YEAR(CURDATE()));

-- Initialize leave balances for manager
INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'CASUAL', 12, 0, YEAR(CURDATE()) FROM employees WHERE email = 'manager@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'manager@company.com') AND leave_type = 'CASUAL' AND year = YEAR(CURDATE()));

INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'SICK', 10, 0, YEAR(CURDATE()) FROM employees WHERE email = 'manager@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'manager@company.com') AND leave_type = 'SICK' AND year = YEAR(CURDATE()));

INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'EARNED', 15, 0, YEAR(CURDATE()) FROM employees WHERE email = 'manager@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'manager@company.com') AND leave_type = 'EARNED' AND year = YEAR(CURDATE()));

-- Initialize leave balances for employee
INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'CASUAL', 12, 0, YEAR(CURDATE()) FROM employees WHERE email = 'employee@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'employee@company.com') AND leave_type = 'CASUAL' AND year = YEAR(CURDATE()));

INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'SICK', 10, 0, YEAR(CURDATE()) FROM employees WHERE email = 'employee@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'employee@company.com') AND leave_type = 'SICK' AND year = YEAR(CURDATE()));

INSERT INTO leave_balances (employee_id, leave_type, total_leaves, used_leaves, year)
SELECT id, 'EARNED', 15, 0, YEAR(CURDATE()) FROM employees WHERE email = 'employee@company.com'
AND NOT EXISTS (SELECT 1 FROM leave_balances WHERE employee_id = (SELECT id FROM employees WHERE email = 'employee@company.com') AND leave_type = 'EARNED' AND year = YEAR(CURDATE()));
