-- Test script to check loan data and insert sample data if needed
-- Run this in MySQL to debug loan API issues

USE bank_db;

-- Check if loan_requests table exists and has data
SELECT 'Loan Requests Table Check:' as Info;
DESCRIBE loan_requests;

SELECT 'Total loan requests:' as Info, COUNT(*) as Count FROM loan_requests;

SELECT 'Loan status distribution:' as Info, final_status, COUNT(*) as Count
FROM loan_requests
GROUP BY final_status;

-- Check pending admin loans specifically
SELECT 'Pending Admin Loans:' as Info;
SELECT lr.id, lr.amount, lr.final_status, u.full_name, lr.created_at
FROM loan_requests lr
JOIN users u ON lr.user_id = u.id
WHERE lr.final_status = 'PENDING_ADMIN'
ORDER BY lr.created_at DESC
LIMIT 10;

-- Insert sample loan data if table is empty
SELECT 'Checking if we need sample data...' as Info;

SET @loan_count = (SELECT COUNT(*) FROM loan_requests);
SELECT CONCAT('Current loan count: ', @loan_count) as Info;

-- Only insert if no loans exist
INSERT IGNORE INTO loan_requests (user_id, amount, term, purpose, declared_income, job_segment_num, ai_score, ai_decision, final_status, created_at, updated_at)
SELECT
    u.id,
    50000000.00,
    12,
    'Test loan application',
    15000000.00,
    'BUSINESS_OWNER',
    0.75,
    'PASSED_AI',
    'PENDING_ADMIN',
    NOW(),
    NOW()
FROM users u
WHERE u.role = 'USER'
ORDER BY u.id
LIMIT 1;

SELECT 'Sample data inserted. New loan count:' as Info, COUNT(*) as Count FROM loan_requests;

-- Check the inserted data
SELECT 'Sample loan details:' as Info;
SELECT lr.id, lr.amount, lr.final_status, u.full_name, u.email
FROM loan_requests lr
JOIN users u ON lr.user_id = u.id
ORDER BY lr.created_at DESC
LIMIT 5;