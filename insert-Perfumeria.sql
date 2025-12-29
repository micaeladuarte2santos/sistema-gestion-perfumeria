INSERT INTO proveedores (id, nombre, telefono, email) VALUES
(1, 'Distribuidora Fragancias S.A.', '1123456789', 'contacto@fragancias.com'),
(2, 'Procter & Gamble Argentina', '1145678910', 'ventas@pg.com'),
(3, 'Distribuidora Capilar SRL', '1134567890', 'info@capilar.com'),
(4, 'Johnson & Johnson Argentina', '1144445566', 'jjargentina@jj.com'),
(5, 'L’Oréal Argentina S.A.', '1156789012', 'contacto@loreal.com');

INSERT INTO categorias (id, nombre) VALUES
(1, 'Perfumes'),
(2, 'Desodorantes'),
(3, 'Cremas y Cuidado Facial'),
(4, 'Cuidado Capilar'),
(5, 'Cuidado Infantil'),
(6, 'Maquillaje');


INSERT INTO productos (id, codigo_barras, nombre, precio, stock, categoria_id, proveedor_id, activo) VALUES
(1, '7791293034567', 'Perfume Carolina Herrera 212 VIP 80ml', 82000.00, 15, 1, 1, true),
(2, '7790123456789', 'Desodorante Dove Original 150ml', 2800.00, 60, 2, 2, true),
(3, '7796543210987', 'Crema Nivea Soft 200ml', 4500.00, 35, 3, 2, true),
(4, '7799988776655', 'Shampoo Pantene Hidratación 400ml', 5200.00, 40, 4, 3, true),
(5, '7792233445566', 'Acondicionador Pantene Hidratación 400ml', 5200.00, 35, 4, 3, true),
(6, '7791122334455', 'Colonia Baby Johnson 200ml', 3700.00, 25, 5, 4, true),
(7, '7794433221100', 'Labial Maybelline SuperStay Matte Ink', 8900.00, 20, 6, 5, true),
(8, '7795566778899', 'Base L’Oréal True Match 30ml', 11200.00, 18, 6, 5, true),
(9, '7796655443322', 'Perfume Paco Rabanne Invictus 100ml', 95000.00, 10, 1, 1, true),
(10, '7797778889990', 'Crema Antiage Olay Regenerist 50ml', 15800.00, 12, 3, 2, true);

