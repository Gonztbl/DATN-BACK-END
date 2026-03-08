-- ==========================================================
-- FIX DELETE API - UPDATE PRODUCTS TABLE SCHEMA
-- ==========================================================

-- Step 1: Add missing columns if they don't exist
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'available';

ALTER TABLE products 
ADD COLUMN IF NOT EXISTS deleted_at DATETIME NULL;

-- Step 2: Update existing products to have default status
UPDATE products 
SET status = 'available' 
WHERE status IS NULL;

-- Step 3: Check current table structure
SHOW COLUMNS FROM products;

-- Step 4: Test query to verify soft delete works
SELECT id, name, status, deleted_at FROM products WHERE id = 1;
