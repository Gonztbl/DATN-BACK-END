-- Step 0: Clean up existing data for 'perfect_user' to avoid duplicate errors
SET @target_user_id = (SELECT id FROM users WHERE userName = 'perfect_user');
SET @target_wallet_id = (SELECT id FROM wallets WHERE user_id = @target_user_id);

DELETE FROM wallet_daily_snapshots WHERE wallet_id = @target_wallet_id;
DELETE FROM transactions WHERE wallet_id = @target_wallet_id;
DELETE FROM wallets WHERE id = @target_wallet_id;
DELETE FROM users WHERE id = @target_user_id;

-- Step 1: Create the User (Perfect Profile)
-- Precisely following @Column(name=...) and field names from User.java
INSERT INTO users (userName, email, phone, full_name, date_of_birth, address, password_hash, is_active, is_verified, role, created_at, kyc_level, membership, job_segment) 
VALUES ('perfect_user', 'perfect@test.com', '0987654321', 'Nguyen Van Hoan Hao', '1995-05-15', '123 Success St, Finance City', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 1, 1, 'USER', DATE_SUB(NOW(), INTERVAL 120 DAY), 4, 'Platinum', 'OFFICE_WORKER');

-- Step 2: Get user ID and create a Wallet
SET @last_user_id = LAST_INSERT_ID();
-- Note: 'user_id' is from @JoinColumn(name="user_id") in Wallet.java
INSERT INTO wallets (user_id, balance, availableBalance, code, status, accountNumber, createdAt) 
VALUES (@last_user_id, 120000000.0, 120000000.0, CONCAT('WPERFECT', @last_user_id), 'ACTIVE', '0987654321', DATE_SUB(NOW(), INTERVAL 120 DAY));

-- Step 3: Get wallet ID
SET @last_wallet_id = LAST_INSERT_ID();

-- Step 4: Insert 3 Monthly Salary Inflows (50M each)
INSERT INTO transactions (wallet_id, type, direction, amount, fee, balanceBefore, balanceAfter, status, referenceId, createdAt)
VALUES 
(@last_wallet_id, 'DEPOSIT', 'IN', 50000000.0, 0.0, 20000000.0, 70000000.0, 'COMPLETED', 'SALARY_JAN', DATE_SUB(NOW(), INTERVAL 75 DAY)),
(@last_wallet_id, 'DEPOSIT', 'IN', 50000000.0, 0.0, 70000000.0, 120000000.0, 'COMPLETED', 'SALARY_FEB', DATE_SUB(NOW(), INTERVAL 45 DAY)),
(@last_wallet_id, 'DEPOSIT', 'IN', 50000000.0, 0.0, 120000000.0, 170000000.0, 'COMPLETED', 'SALARY_MAR', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- Step 5: Generate 90 days of Daily Snapshots (Maintaining 120M balance)
-- Table name is wallet_daily_snapshots, columns are record_date, end_of_day_balance per @Column
INSERT INTO wallet_daily_snapshots (wallet_id, record_date, end_of_day_balance, created_at)
SELECT 
    @last_wallet_id, 
    DATE_SUB(CURDATE(), INTERVAL (n.n) DAY), 
    120000000.0,
    NOW()
FROM (
    SELECT a.a + b.a * 10 AS n
    FROM (SELECT 0 AS a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS a
    CROSS JOIN (SELECT 0 AS a UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) AS b
) AS n
WHERE n < 91;

-- Final check
SELECT 'SUCCESS: Perfect User created with ID' as msg, @last_user_id as userId;
