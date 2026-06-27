SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS cafeteria_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cafeteria_db;

-- 1. Categorías
CREATE TABLE IF NOT EXISTS tbl_categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255)
);

-- 2. Productos
CREATE TABLE IF NOT EXISTS tbl_producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL,
    categoria_id INT,
    FOREIGN KEY (categoria_id) REFERENCES tbl_categoria(id)
);

-- 3. Usuarios
CREATE TABLE IF NOT EXISTS tbl_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL
);

-- 4. Pedidos
CREATE TABLE IF NOT EXISTS tbl_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    mesa VARCHAR(255),
    estado VARCHAR(255),
    total DOUBLE NOT NULL,
    fecha DATETIME,
    mesero VARCHAR(255)
);

-- 5. Detalle Pedido
CREATE TABLE IF NOT EXISTS tbl_detalle_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    pedido_id INT,
    producto_id INT,
    FOREIGN KEY (pedido_id) REFERENCES tbl_pedido(id),
    FOREIGN KEY (producto_id) REFERENCES tbl_producto(id)
);

-- Insertar Categorías
INSERT INTO tbl_categoria (id, nombre, descripcion) VALUES
(1, 'Bebidas Calientes', 'Café y tés calientes premium'),
(2, 'Bebidas Frías', 'Cafés helados, frapés y refrescos'),
(3, 'Jugos y Batidos', 'Jugos naturales y batidos nutritivos')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), descripcion=VALUES(descripcion);

-- Insertar Productos (Bebidas)
INSERT INTO tbl_producto (id, nombre, precio, stock, categoria_id) VALUES
(1, 'Espresso', 5.00, 50, 1),
(2, 'Café Americano', 6.00, 100, 1),
(3, 'Cappuccino', 8.50, 40, 1),
(4, 'Latte Macchiato', 9.00, 45, 1),
(5, 'Café Moka', 10.00, 30, 1),
(6, 'Iced Americano', 7.00, 60, 2),
(7, 'Iced Latte', 9.50, 50, 2),
(8, 'Frappé de Café', 12.00, 35, 2),
(9, 'Frappé de Oreo', 13.50, 25, 2),
(10, 'Jugo de Naranja', 8.00, 80, 3),
(11, 'Batido de Fresa', 10.00, 40, 3),
(12, 'Jugo de Papaya', 9.00, 50, 3)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), precio=VALUES(precio), stock=VALUES(stock), categoria_id=VALUES(categoria_id);

-- Insertar Usuarios Semilla
INSERT INTO tbl_usuario (id, username, password, nombre, rol) VALUES
(1, 'mesero1', '123', 'Juan Mesero', 'MESERO'),
(2, 'cocinero1', '123', 'Chef Carlos', 'COCINERO'),
(3, 'admin', '123', 'Admin Ambrosia', 'ADMINISTRADOR')
ON DUPLICATE KEY UPDATE username=VALUES(username), password=VALUES(password), nombre=VALUES(nombre), rol=VALUES(rol);
