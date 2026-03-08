-- Update products table to add missing columns for admin product management
-- Run this script if your database doesn't have the latest schema

-- Add status column if not exists
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'available';

-- Add deleted_at column if not exists  
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS deleted_at DATETIME NULL;

-- Update existing products to have status = 'available' if NULL
UPDATE products 
SET status = 'available' 
WHERE status IS NULL;

-- Check current schema
DESCRIBE products;
