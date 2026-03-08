-- ==========================================================
-- MIGRATE IMAGE COLUMN FROM image_url TO image_base64
-- ==========================================================

-- Step 1: Add new column if not exists
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS image_base64 LONGTEXT;

-- Step 2: Migrate data from image_url to image_base64
UPDATE products 
SET image_base64 = image_url 
WHERE image_url IS NOT NULL AND image_base64 IS NULL;

-- Step 3: Drop old column (optional - comment out if want to keep)
-- ALTER TABLE products DROP COLUMN image_url;

-- Step 4: Check the migration
SELECT id, name, 
       CASE 
           WHEN image_base64 IS NOT NULL THEN 'Migrated'
           ELSE 'No image'
       END as migration_status,
       LEFT(image_base64, 50) as image_preview
FROM products 
LIMIT 5;
