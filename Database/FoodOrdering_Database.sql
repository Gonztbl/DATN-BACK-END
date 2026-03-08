-- Food Ordering Database Schema
-- Use existing bank_db database

USE bank_db;

-- Restaurants table
CREATE TABLE restaurants (
  id VARCHAR(20) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  email VARCHAR(255),
  address TEXT NOT NULL,
  logo_base64 LONGTEXT,
  status BOOLEAN DEFAULT TRUE,
  product_count INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL
);

-- Categories table
CREATE TABLE categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  icon VARCHAR(50),
  order_index INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  image_base64 LONGTEXT,
  category_id INT NOT NULL,
  restaurant_id VARCHAR(20) NOT NULL,
  rating_avg DECIMAL(3,2) DEFAULT 0.00,
  rating_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'available',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CONSTRAINT fk_product_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Reviews table
CREATE TABLE reviews (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
  comment TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Orders table
CREATE TABLE orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status ENUM('pending', 'confirmed', 'preparing', 'delivering', 'completed', 'cancelled') DEFAULT 'pending',
  delivery_address TEXT NOT NULL,
  recipient_name VARCHAR(100) NOT NULL,
  recipient_phone VARCHAR(20) NOT NULL,
  note TEXT,
  payment_method VARCHAR(50),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order items table
CREATE TABLE order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  price_at_time DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert sample data
-- Categories
INSERT INTO categories (name, icon, order_index) VALUES
('Tất cả', 'restaurant', 1),
('Bánh Mì', 'lunch_dining', 2),
('Phở & Bún', 'ramen_dining', 3),
('Trà Sữa', 'bubbles', 4),
('Cà phê', 'local_cafe', 5);

-- Restaurants
INSERT INTO restaurants (id, name, phone, email, address, status, product_count) VALUES
('RS-1234', 'Phở Thìn Lò Đúc', '02439712738', 'contact@phothin.com', '13 Lò Đúc, Hai Bà Trưng, Hà Nội', TRUE, 2),
('RS-1235', 'Bánh Mì Hồng Hoa', '02439712739', 'info@banhmihonghoa.com', '25 Bà Triệu, Hai Bà Trưng, Hà Nội', TRUE, 1),
('RS-1236', 'Highlands Coffee', '02439712740', 'highlands@example.com', '458 Minh Khai, Hai Bà Trưng, Hà Nội', TRUE, 1),
('RS-1237', 'Trà Sữa Gong Cha', '02439712741', 'gongcha@example.com', '120 Trần Quang Khải, Hoàn Kiếm, Hà Nội', TRUE, 1);

-- Products
INSERT INTO products (name, description, price, category_id, restaurant_id, rating_avg, rating_count, status) VALUES
('Bánh Mì Đặc Biệt', 'Bánh mì kẹp thịt nguội, pate, rau thơm, đồ chua', 35000.00, 2, 'RS-1235', 4.8, 250, 'available'),
('Phở Bò Gia Truyền', 'Phở bò truyền thống với nước dùng hầm xương trong 12 giờ', 65000.00, 3, 'RS-1234', 4.9, 1200, 'available'),
('Bún Thang', 'Bún thang Hà Nội truyền thống với giò lụa, trứng, gà', 55000.00, 3, 'RS-1234', 4.7, 180, 'available'),
('Trà Sữa Trân Châu Đường Đen', 'Trà sữa đặc trưng với trân châu trắng', 45000.00, 4, 'RS-1237', 4.6, 890, 'available'),
('Cà Phê Sữa Đá', 'Cà phê Việt Nam đậm đà với sữa đặc', 25000.00, 5, 'RS-1236', 4.5, 450, 'available'),
('Bánh Mì Bì', 'Bánh mì kẹp bì heo, rau thơm, nước mắm chua ngọt', 30000.00, 2, 'RS-1235', 4.4, 120, 'unavailable');

-- Sample reviews (assuming user IDs 1-5 exist)
INSERT INTO reviews (user_id, product_id, rating, comment) VALUES
(1, 1, 5, 'Bánh mì rất ngon, nhân đầy đặn, giá cả hợp lý.'),
(2, 2, 5, 'Nước dùng rất ngon và thanh. Sẽ quay lại ủng hộ!'),
(3, 4, 4, 'Trà sữa ngon, trân châu dai, nhưng hơi ngọt.'),
(4, 5, 4, 'Cà phê đậm vị đúng gu Hà Nội, giá tốt.'),
(5, 3, 5, 'Bún thang chuẩn vị Hà Nội, nhiều topping.');
