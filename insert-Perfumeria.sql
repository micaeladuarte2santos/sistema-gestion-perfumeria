-- Script de carga inicial para Perfumeria
-- Recomendado ejecutar con la aplicacion ya iniciada para que Hibernate cree las tablas.

INSERT INTO proveedores (id, nombre, telefono, email, activo) VALUES
(1, 'Distribuidora Fragancias del Plata S.A.', '1145237812', 'ventas@fraganciasdelplata.com.ar', true),
(2, 'Procter & Gamble Argentina S.R.L.', '1146783201', 'canalminorista@pg.com', true),
(3, 'Capilar Beauty Supply S.A.', '1139842210', 'pedidos@capilarbeauty.com.ar', true),
(4, 'Johnson & Johnson Argentina', '1144412298', 'farmacias@jj.com', true),
(5, 'L''Oreal Argentina S.A.', '1156723401', 'mayoristas@loreal.com', true),
(6, 'Beiersdorf Argentina S.A.', '1151098877', 'cuentas@nivea.com.ar', true),
(7, 'Coty Beauty Cono Sur', '1162314400', 'distribucion@coty.com', true),
(8, 'Importadora Belleza Urbana S.R.L.', '1137721188', 'contacto@bellezaurbana.com.ar', false);

INSERT INTO categorias (id, nombre) VALUES
(1, 'Perfumes'),
(2, 'Desodorantes'),
(3, 'Cuidado Facial'),
(4, 'Cuidado Capilar'),
(5, 'Cuidado Infantil'),
(6, 'Maquillaje'),
(7, 'Cuidado Corporal'),
(8, 'Sets y Promociones');

INSERT INTO productos (id, codigo_barras, nombre, precio, precio_costo, stock, activo, imagen, categoria_id, proveedor_id) VALUES
(1, '7791001000001', 'Perfume Carolina Herrera 212 VIP 80 ml', 115000.00, 76000.00, 12, true, NULL, 1, 1),
(2, '7791001000002', 'Desodorante Dove Original 150 ml', 4300.00, 2800.00, 70, true, NULL, 2, 2),
(3, '7791001000003', 'Crema Nivea Soft 200 ml', 6900.00, 4200.00, 45, true, NULL, 7, 6),
(4, '7791001000004', 'Shampoo Pantene Hidratacion 400 ml', 6100.00, 3900.00, 38, true, NULL, 4, 3),
(5, '7791001000005', 'Acondicionador Pantene Hidratacion 400 ml', 6100.00, 3900.00, 34, true, NULL, 4, 3),
(6, '7791001000006', 'Colonia Baby Johnson 200 ml', 5900.00, 3600.00, 28, true, NULL, 5, 4),
(7, '7791001000007', 'Labial Maybelline SuperStay Matte Ink', 12900.00, 7800.00, 22, true, NULL, 6, 5),
(8, '7791001000008', 'Base L''Oreal True Match 30 ml', 15800.00, 9800.00, 16, true, NULL, 6, 5),
(9, '7791001000009', 'Perfume Paco Rabanne Invictus 100 ml', 132000.00, 89000.00, 8, true, NULL, 1, 7),
(10, '7791001000010', 'Crema Olay Regenerist 50 ml', 18900.00, 12100.00, 14, true, NULL, 3, 2),
(11, '7791001000011', 'Shampoo Elvive Dream Long 400 ml', 6200.00, 4100.00, 30, true, NULL, 4, 5),
(12, '7791001000012', 'Mascara de Pestanas Maybelline Sky High', 14900.00, 9100.00, 18, true, NULL, 6, 5),
(13, '7791001000013', 'Protector Solar Dermaglos FPS 50 180 g', 9800.00, 6200.00, 26, true, NULL, 7, 4),
(14, '7791001000014', 'Agua Micelar Garnier 400 ml', 7600.00, 4800.00, 24, true, NULL, 3, 5),
(15, '7791001000015', 'Perfume Antonio Banderas Blue Seduction 100 ml', 48500.00, 31500.00, 10, true, NULL, 1, 1),
(16, '7791001000016', 'Desodorante Axe Apollo 150 ml', 3900.00, 2400.00, 50, true, NULL, 2, 2),
(17, '7791001000017', 'Crema Dermaglos Facial Noche 40 g', 11200.00, 7100.00, 19, true, NULL, 3, 4),
(18, '7791001000018', 'Set Regalo Nivea Soft + Labial', 17500.00, 10800.00, 7, true, NULL, 8, 6),
(19, '7791001000019', 'Esmalte Revlon ColorStay Rojo Intenso', 8400.00, 5200.00, 9, false, NULL, 6, 8),
(20, '7791001000020', 'Crema Corporal Nivea Milk 400 ml', 8300.00, 5100.00, 21, true, NULL, 7, 6);

-- Usuarios de prueba
-- admin / admin123
-- vendedor / vendedor123
-- cliente / cliente123
INSERT INTO usuarios (username, password, nombre, apellido, email, fecha_nacimiento, verificado) VALUES
('admin', '$2a$10$5y4bDUqhY9t/nQhCCF9x7eo10ZE5Xz9g.0pUmKkG3XUV3JCJ2MHRu', 'Luciano', 'Martinez', 'admin@perfumeria.com', '1990-04-12', true),
('vendedor', '$2a$10$XaWgFP30ZM2VbwSXAMAY4uRjEu6WhyAJWpKJfGJpvnypXp.ePaAUC', 'Marina', 'Suarez', 'vendedor@perfumeria.com', '1995-09-03', true),
('cliente', '$2a$10$dXQaz5ScAG60n7NeUpEZF.GhvUVzQdtvrYSqAltpc2qH43IV6O/TK', 'Carla', 'Gimenez', 'cliente@perfumeria.com', '1998-11-21', true),
('sofia.ramirez', '$2a$10$dXQaz5ScAG60n7NeUpEZF.GhvUVzQdtvrYSqAltpc2qH43IV6O/TK', 'Sofia', 'Ramirez', 'sofia.ramirez@gmail.com', '1997-06-15', true),
('bruno.lopez', '$2a$10$dXQaz5ScAG60n7NeUpEZF.GhvUVzQdtvrYSqAltpc2qH43IV6O/TK', 'Bruno', 'Lopez', 'bruno.lopez@gmail.com', '1992-01-28', false);

INSERT INTO ventas (id, nombre_cliente, fecha, total, estado, metodo_pago) VALUES
(1, 'Lucia Fernandez', '2026-03-15 11:24:00', 18400.00, 'ABONADA', 'DEBITO'),
(2, 'Mariano Sosa', '2026-03-16 18:42:00', 121200.00, 'ABONADA', 'CREDITO'),
(3, 'Camila Roldan', '2026-03-20 16:10:00', 36300.00, 'ABONADA', 'TRANSFERENCIA'),
(4, 'Sofia Benitez', '2026-03-24 13:05:00', 23000.00, 'PENDIENTE', 'EFECTIVO'),
(5, 'Nicolas Acosta', '2026-03-27 19:18:00', 139800.00, 'CANCELADA', 'CREDITO'),
(6, 'Valentina Peralta', '2026-03-29 17:50:00', 55400.00, 'DEVUELTA', 'DEBITO');

INSERT INTO detalles_venta (id, venta_id, producto_id, cantidad, subtotal) VALUES
(1, 1, 2, 2, 8600.00),
(2, 1, 13, 1, 9800.00),
(3, 2, 1, 1, 115000.00),
(4, 2, 11, 1, 6200.00),
(5, 3, 7, 1, 12900.00),
(6, 3, 8, 1, 15800.00),
(7, 3, 14, 1, 7600.00),
(8, 4, 6, 2, 11800.00),
(9, 4, 17, 1, 11200.00),
(10, 5, 9, 1, 132000.00),
(11, 5, 16, 2, 7800.00),
(12, 6, 15, 1, 48500.00),
(13, 6, 3, 1, 6900.00);
