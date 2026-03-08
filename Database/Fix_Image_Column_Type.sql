-- ==========================================================
-- FIX IMAGE COLUMN TYPE FOR BASE64 STORAGE
-- ==========================================================
-- Problem: image_base64 column is too small to store large base64 strings
-- Solution: Change to LONGTEXT to support up to 4GB of data

-- Step 1: Check current column type
SHOW COLUMNS FROM products WHERE Field = 'image_base64';

-- Step 2: Modify column to LONGTEXT (MySQL)
ALTER TABLE products 
MODIFY COLUMN image_base64 LONGTEXT;

-- Step 3: Verify the change
SHOW COLUMNS FROM products WHERE Field = 'image_base64';

-- Step 4: Test with sample data (optional)
-- UPDATE products 
-- SET image_base64 = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...' 
-- WHERE id = 1;

-- Step 5: Check if data fits
SELECT 
    id, 
    name,
    CASE 
        WHEN image_base64 IS NOT NULL THEN LENGTH(image_base64)
        ELSE 0
    END as image_length,
    CASE 
        WHEN LENGTH(image_base64) > 1000 THEN 'Large image'
        WHEN LENGTH(image_base64) > 100 THEN 'Medium image'
        WHEN LENGTH(image_base64) > 0 THEN 'Small image'
        ELSE 'No image'
    END as image_size_category
FROM products 
WHERE image_base64 IS NOT NULL
LIMIT 5;
