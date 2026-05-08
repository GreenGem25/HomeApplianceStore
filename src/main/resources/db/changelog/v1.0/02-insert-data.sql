--liquibase formatted sql

--changeset test-data:1
--comment: Insert test data with UUID keys

-- Категории
INSERT INTO categories (category_id, name, description, parent_category_id) VALUES
                                                                                ('11111111-1111-1111-1111-111111111111', 'Электроника', 'Товары, работающие на электричестве', NULL),
                                                                                ('22222222-2222-2222-2222-222222222222', 'Смартфоны', 'Мобильные телефоны и аксессуары', '11111111-1111-1111-1111-111111111111'),
                                                                                ('33333333-3333-3333-3333-333333333333', 'Ноутбуки', 'Портативные компьютеры', '11111111-1111-1111-1111-111111111111'),
                                                                                ('44444444-4444-4444-4444-444444444444', 'Бытовая техника', 'Техника для дома', '11111111-1111-1111-1111-111111111111'),
                                                                                ('55555555-5555-5555-5555-555555555555', 'Аудиотехника', 'Наушники, колонки, плееры', '11111111-1111-1111-1111-111111111111'),
                                                                                ('66666666-6666-6666-6666-666666666666', 'Планшеты', 'Компактные компьютеры', '11111111-1111-1111-1111-111111111111'),
                                                                                ('77777777-7777-7777-7777-777777777777', 'Игровые консоли', 'PlayStation, Xbox, Nintendo', '11111111-1111-1111-1111-111111111111'),
                                                                                ('88888888-8888-8888-8888-888888888888', 'Фототехника', 'Камеры и объективы', '11111111-1111-1111-1111-111111111111'),
                                                                                ('99999999-9999-9999-9999-999999999999', 'Умный дом', 'Умные колонки, лампочки, датчики', '11111111-1111-1111-1111-111111111111');

-- Поставщики
INSERT INTO suppliers (supplier_id, name, contact_name, phone, email, address) VALUES
                                                                                   ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ООО "Электроника-Юг"', 'Петров Иван Иванович', '8-800-555-35-35', 'info@electronicsouth.ru', 'г. Краснодар, ул. Северная, 123'),
                                                                                   ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ИП Смирнов А.В.', 'Смирнов Алексей Владимирович', '8-495-123-45-67', 'smirnov@tech.ru', 'г. Москва, ул. Тверская, 45'),
                                                                                   ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'ООО "ТехноПоставка"', 'Козлова Елена Петровна', '8-812-555-55-55', 'sales@techno.ru', 'г. Санкт-Петербург, пр. Невский, 100'),
                                                                                   ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'ООО "Альфа-Трейд"', 'Соколов Дмитрий Николаевич', '8-499-123-45-67', 'alfa@trade.ru', 'г. Москва, ул. Ленина, 1'),
                                                                                   ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'ООО "Глобал Тех"', 'Морозова Анна Сергеевна', '8-383-123-45-67', 'global@tech.ru', 'г. Новосибирск, пр. Дзержинского, 10');

-- Товары
INSERT INTO products (product_id, supplier_id, name, description, price, stock_quantity, category_id, manufacturer, warranty_period, image_path) VALUES
                                                                                                                                                     ('11111111-aaaa-1111-aaaa-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Смартфон iPhone 15 Pro', '256 ГБ, титановый корпус, A17 Pro', 119999.00, 15, '22222222-2222-2222-2222-222222222222', 'Apple', 12, '/images/iphone15-pro.jpg'),
                                                                                                                                                     ('22222222-bbbb-2222-bbbb-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Смартфон iPhone 15', '128 ГБ, розовый', 89999.00, 8, '22222222-2222-2222-2222-222222222222', 'Apple', 12, '/images/iphone15.jpg'),
                                                                                                                                                     ('33333333-cccc-3333-cccc-333333333333', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Смартфон Samsung Galaxy S24', '256 ГБ, черный, Snapdragon', 109999.00, 12, '22222222-2222-2222-2222-222222222222', 'Samsung', 12, '/images/samsung-s24.jpg'),
                                                                                                                                                     ('44444444-dddd-4444-dddd-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Смартфон Samsung Galaxy A54', '128 ГБ, зеленый', 39999.00, 25, '22222222-2222-2222-2222-222222222222', 'Samsung', 12, '/images/samsung-a54.jpg'),
                                                                                                                                                     ('55555555-eeee-5555-eeee-555555555555', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Смартфон Xiaomi Redmi Note 13', '8/256 ГБ, синий', 28999.00, 50, '22222222-2222-2222-2222-222222222222', 'Xiaomi', 12, '/images/xiaomi-note13.jpg'),
                                                                                                                                                     ('66666666-ffff-6666-ffff-666666666666', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Смартфон Xiaomi 14 Ultra', '16/512 ГБ, черный', 89999.00, 7, '22222222-2222-2222-2222-222222222222', 'Xiaomi', 12, '/images/xiaomi-14ultra.jpg'),
                                                                                                                                                     ('77777777-aaaa-7777-aaaa-777777777777', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Ноутбук Irbis NB261', '4 ГБ RAM, 64 ГБ eMMC', 17999.00, 50, '33333333-3333-3333-3333-333333333333', 'Irbis', 6, '/images/irbis-nb261.jpg'),
                                                                                                                                                     ('88888888-bbbb-8888-bbbb-888888888888', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Ноутбук Irbis NB275', '8 ГБ RAM, 256 ГБ SSD', 24999.00, 30, '33333333-3333-3333-3333-333333333333', 'Irbis', 6, '/images/irbis-nb275.jpg'),
                                                                                                                                                     ('99999999-cccc-9999-cccc-999999999999', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Ноутбук Asus TUF Gaming F15', '16 ГБ RAM, 512 ГБ SSD, RTX 3050', 89999.00, 10, '33333333-3333-3333-3333-333333333333', 'Asus', 12, '/images/asus-tuf.jpg'),
                                                                                                                                                     ('aaaaaaaa-dddd-aaaa-dddd-aaaaaaaaaaaa', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Ноутбук MSI Katana 15', '16 ГБ RAM, 1 ТБ SSD, RTX 4060', 129999.00, 5, '33333333-3333-3333-3333-333333333333', 'MSI', 12, '/images/msi-katana.jpg'),
                                                                                                                                                     ('bbbbbbbb-eeee-bbbb-eeee-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Планшет iPad Pro 13"', 'M4, 256 ГБ, серебристый', 149999.00, 8, '66666666-6666-6666-6666-666666666666', 'Apple', 12, '/images/ipad-pro.jpg'),
                                                                                                                                                     ('cccccccc-ffff-cccc-ffff-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Планшет iPad Air 11"', 'M2, 128 ГБ, синий', 79999.00, 15, '66666666-6666-6666-6666-666666666666', 'Apple', 12, '/images/ipad-air.jpg'),
                                                                                                                                                     ('dddddddd-aaaa-dddd-aaaa-dddddddddddd', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Планшет Samsung Tab S9', '12 ГБ RAM, 256 ГБ, бежевый', 89999.00, 12, '66666666-6666-6666-6666-666666666666', 'Samsung', 12, '/images/samsung-tab.jpg'),
                                                                                                                                                     ('eeeeeeee-bbbb-eeee-bbbb-eeeeeeeeeeee', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'PlayStation 5 Slim', '1 ТБ, две игры в комплекте', 59999.00, 7, '77777777-7777-7777-7777-777777777777', 'Sony', 12, '/images/ps5.jpg'),
                                                                                                                                                     ('ffffffff-cccc-ffff-cccc-ffffffffffff', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Xbox Series X', '1 ТБ, черный', 54999.00, 5, '77777777-7777-7777-7777-777777777777', 'Microsoft', 12, '/images/xbox.jpg'),
                                                                                                                                                     ('12121212-dddd-1212-dddd-121212121212', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Наушники Sony WH-1000XM5', 'беспроводные, шумоподавление', 32999.00, 20, '55555555-5555-5555-5555-555555555555', 'Sony', 12, '/images/sony-xm5.jpg'),
                                                                                                                                                     ('13131313-eeee-1313-eeee-131313131313', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Наушники AirPods Pro 2', 'USB-C, беспроводная зарядка', 24999.00, 25, '55555555-5555-5555-5555-555555555555', 'Apple', 12, '/images/airpods-pro.jpg'),
                                                                                                                                                     ('14141414-ffff-1414-ffff-141414141414', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Умная колонка Яндекс Станция 2', 'с Алисой, черный', 15999.00, 30, '99999999-9999-9999-9999-999999999999', 'Яндекс', 6, '/images/yandex-station.jpg'),
                                                                                                                                                     ('15151515-aaaa-1515-aaaa-151515151515', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Умная колонка SberBoom', 'Салют, красный', 12999.00, 15, '99999999-9999-9999-9999-999999999999', 'Sber', 6, '/images/sberboom.jpg'),
                                                                                                                                                     ('16161616-bbbb-1616-bbbb-161616161616', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Зеркальный фотоаппарат Canon EOS 2000D', 'Kit 18-55 мм', 45999.00, 5, '88888888-8888-8888-8888-888888888888', 'Canon', 12, '/images/canon.jpg'),
                                                                                                                                                     ('17171717-cccc-1717-cccc-171717171717', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Холодильник LG GA-B459', 'No Frost, 350 л, серебро', 59999.00, 4, '44444444-4444-4444-4444-444444444444', 'LG', 24, '/images/fridge-lg.jpg'),
                                                                                                                                                     ('18181818-dddd-1818-dddd-181818181818', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Стиральная машина Samsung WW90', '9 кг, белый', 45999.00, 6, '44444444-4444-4444-4444-444444444444', 'Samsung', 24, '/images/washer.jpg'),
                                                                                                                                                     ('19191919-eeee-1919-eeee-191919191919', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Микроволновка LG MS-2042', '20 л, белый', 7999.00, 20, '44444444-4444-4444-4444-444444444444', 'LG', 12, '/images/microwave.jpg'),
                                                                                                                                                     ('20202020-ffff-2020-ffff-202020202020', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Пылесос Dyson V15 Detect', 'беспроводной, желтый', 59999.00, 7, '44444444-4444-4444-4444-444444444444', 'Dyson', 24, '/images/dyson.jpg');

-- Клиенты
INSERT INTO customers (customer_id, first_name, last_name, email, phone, address, discount, money_spent) VALUES
                                                                                                             ('aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', 'Иван', 'Петров', 'ivan.petrov@email.ru', '+7 (901) 123-45-67', 'г. Москва, ул. Ленина, д. 1, кв. 1', 5, 150000.00),
                                                                                                             ('bbbbbbbb-2222-bbbb-2222-bbbbbbbb2222', 'Мария', 'Иванова', 'maria.ivanova@email.ru', '+7 (902) 234-56-78', 'г. Москва, ул. Пушкина, д. 2, кв. 2', 10, 250000.00),
                                                                                                             ('cccccccc-3333-cccc-3333-cccccccc3333', 'Алексей', 'Смирнов', 'alexey.smirnov@email.ru', '+7 (903) 345-67-89', 'г. Санкт-Петербург, пр. Невский, д. 3, кв. 3', 0, 50000.00),
                                                                                                             ('dddddddd-4444-dddd-4444-dddddddd4444', 'Елена', 'Козлова', 'elena.kozlova@email.ru', '+7 (904) 456-78-90', 'г. Санкт-Петербург, ул. Садовая, д. 4, кв. 4', 15, 350000.00),
                                                                                                             ('eeeeeeee-5555-eeee-5555-eeeeeeee5555', 'Дмитрий', 'Морозов', 'dmitry.morozov@email.ru', '+7 (905) 567-89-01', 'г. Казань, ул. Баумана, д. 5, кв. 5', 0, 75000.00),
                                                                                                             ('ffffffff-6666-ffff-6666-ffffffff6666', 'Анна', 'Волкова', 'anna.volkova@email.ru', '+7 (906) 678-90-12', 'г. Казань, пр. Победы, д. 6, кв. 6', 5, 125000.00),
                                                                                                             ('12121212-7777-1212-7777-121212127777', 'Сергей', 'Соколов', 'sergey.sokolov@email.ru', '+7 (907) 789-01-23', 'г. Екатеринбург, ул. Малышева, д. 7, кв. 7', 20, 450000.00),
                                                                                                             ('13131313-8888-1313-8888-131313138888', 'Ольга', 'Михайлова', 'olga.mikhailova@email.ru', '+7 (908) 890-12-34', 'г. Екатеринбург, пр. Ленина, д. 8, кв. 8', 0, 25000.00),
                                                                                                             ('14141414-9999-1414-9999-141414149999', 'Павел', 'Федоров', 'pavel.fedorov@email.ru', '+7 (909) 901-23-45', 'г. Новосибирск, ул. Красный проспект, д. 9, кв. 9', 5, 180000.00),
                                                                                                             ('15151515-0000-1515-0000-151515150000', 'Татьяна', 'Павлова', 'tatiana.pavlova@email.ru', '+7 (910) 012-34-56', 'г. Новосибирск, ул. Советская, д. 10, кв. 10', 0, 90000.00);

-- Заказы
INSERT INTO orders (order_id, customer_id, order_number, total_price, status, shipping_address, order_date) VALUES
                                                                                                                ('aaaaaaaa-1111-2222-3333-aaaaaaaaaaaa', 'aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', 'ORD-20260309-000001', 119999.00, 'COMPLETED', 'г. Москва, ул. Ленина, д. 1, кв. 1', '2026-03-09 10:30:00'),
                                                                                                                ('bbbbbbbb-1111-2222-3333-bbbbbbbbbbbb', 'aaaaaaaa-1111-aaaa-1111-aaaaaaaa1111', 'ORD-20260310-000002', 32999.00, 'IN_PROGRESS', 'г. Москва, ул. Ленина, д. 1, кв. 1', '2026-03-10 14:15:00'),
                                                                                                                ('cccccccc-1111-2222-3333-cccccccccccc', 'bbbbbbbb-2222-bbbb-2222-bbbbbbbb2222', 'ORD-20260308-000003', 179999.00, 'COMPLETED', 'г. Москва, ул. Пушкина, д. 2, кв. 2', '2026-03-08 09:45:00'),
                                                                                                                ('dddddddd-1111-2222-3333-dddddddddddd', 'bbbbbbbb-2222-bbbb-2222-bbbbbbbb2222', 'ORD-20260309-000004', 45999.00, 'COMPLETED', 'г. Москва, ул. Пушкина, д. 2, кв. 2', '2026-03-09 16:20:00'),
                                                                                                                ('eeeeeeee-1111-2222-3333-eeeeeeeeeeee', 'cccccccc-3333-cccc-3333-cccccccc3333', 'ORD-20260311-000005', 89999.00, 'IN_PROGRESS', 'г. Санкт-Петербург, пр. Невский, д. 3, кв. 3', '2026-03-11 11:10:00'),
                                                                                                                ('ffffffff-1111-2222-3333-ffffffffffff', 'dddddddd-4444-dddd-4444-dddddddd4444', 'ORD-20260307-000006', 149999.00, 'COMPLETED', 'г. Санкт-Петербург, ул. Садовая, д. 4, кв. 4', '2026-03-07 13:30:00'),
                                                                                                                ('12121212-1111-2222-3333-121212121212', 'dddddddd-4444-dddd-4444-dddddddd4444', 'ORD-20260309-000007', 7999.00, 'COMPLETED', 'г. Санкт-Петербург, ул. Садовая, д. 4, кв. 4', '2026-03-09 17:45:00'),
                                                                                                                ('13131313-1111-2222-3333-131313131313', 'eeeeeeee-5555-eeee-5555-eeeeeeee5555', 'ORD-20260310-000008', 129999.00, 'IN_PROGRESS', 'г. Казань, ул. Баумана, д. 5, кв. 5', '2026-03-10 12:00:00'),
                                                                                                                ('14141414-1111-2222-3333-141414141414', 'ffffffff-6666-ffff-6666-ffffffff6666', 'ORD-20260308-000009', 15999.00, 'COMPLETED', 'г. Казань, пр. Победы, д. 6, кв. 6', '2026-03-08 15:30:00'),
                                                                                                                ('15151515-1111-2222-3333-151515151515', '12121212-7777-1212-7777-121212127777', 'ORD-20260309-000010', 129999.00, 'COMPLETED', 'г. Екатеринбург, ул. Малышева, д. 7, кв. 7', '2026-03-09 10:15:00'),
                                                                                                                ('16161616-1111-2222-3333-161616161616', '13131313-8888-1313-8888-131313138888', 'ORD-20260311-000011', 28999.00, 'IN_PROGRESS', 'г. Екатеринбург, пр. Ленина, д. 8, кв. 8', '2026-03-11 09:30:00'),
                                                                                                                ('17171717-1111-2222-3333-171717171717', '14141414-9999-1414-9999-141414149999', 'ORD-20260307-000012', 59999.00, 'COMPLETED', 'г. Новосибирск, ул. Красный проспект, д. 9, кв. 9', '2026-03-07 14:45:00'),
                                                                                                                ('18181818-1111-2222-3333-181818181818', '15151515-0000-1515-0000-151515150000', 'ORD-20260310-000013', 109998.00, 'IN_PROGRESS', 'г. Новосибирск, ул. Советская, д. 10, кв. 10', '2026-03-10 16:00:00');

-- Элементы заказов
INSERT INTO order_items (order_item_id, order_id, product_id, quantity, price) VALUES
                                                                                   ('aaaaaaaa-1111-aaaa-2222-aaaaaaaaaaaa', 'aaaaaaaa-1111-2222-3333-aaaaaaaaaaaa', '11111111-aaaa-1111-aaaa-111111111111', 1, 119999.00),
                                                                                   ('bbbbbbbb-1111-bbbb-2222-bbbbbbbbbbbb', 'bbbbbbbb-1111-2222-3333-bbbbbbbbbbbb', '12121212-dddd-1212-dddd-121212121212', 1, 32999.00),
                                                                                   ('cccccccc-1111-cccc-2222-cccccccccccc', 'cccccccc-1111-2222-3333-cccccccccccc', 'bbbbbbbb-eeee-bbbb-eeee-bbbbbbbbbbbb', 1, 149999.00),
                                                                                   ('dddddddd-1111-dddd-2222-dddddddddddd', 'cccccccc-1111-2222-3333-cccccccccccc', 'cccccccc-ffff-cccc-ffff-cccccccccccc', 1, 79999.00),
                                                                                   ('eeeeeeee-1111-eeee-2222-eeeeeeeeeeee', 'dddddddd-1111-2222-3333-dddddddddddd', '99999999-cccc-9999-cccc-999999999999', 1, 89999.00),
                                                                                   ('ffffffff-1111-ffff-2222-ffffffffffff', 'dddddddd-1111-2222-3333-dddddddddddd', '17171717-cccc-1717-cccc-171717171717', 1, 59999.00),
                                                                                   ('12121212-1111-1212-2222-121212121212', 'eeeeeeee-1111-2222-3333-eeeeeeeeeeee', '33333333-cccc-3333-cccc-333333333333', 1, 109999.00),
                                                                                   ('13131313-1111-1313-2222-131313131313', 'ffffffff-1111-2222-3333-ffffffffffff', 'dddddddd-aaaa-dddd-aaaa-dddddddddddd', 1, 89999.00),
                                                                                   ('14141414-1111-1414-2222-141414141414', 'ffffffff-1111-2222-3333-ffffffffffff', '19191919-eeee-1919-eeee-191919191919', 1, 7999.00),
                                                                                   ('15151515-1111-1515-2222-151515151515', '12121212-1111-2222-3333-121212121212', 'aaaaaaaa-dddd-aaaa-dddd-aaaaaaaaaaaa', 1, 129999.00),
                                                                                   ('16161616-1111-1616-2222-161616161616', '13131313-1111-2222-3333-131313131313', '55555555-eeee-5555-eeee-555555555555', 2, 28999.00),
                                                                                   ('17171717-1111-1717-2222-171717171717', '14141414-1111-2222-3333-141414141414', '14141414-ffff-1414-ffff-141414141414', 1, 15999.00),
                                                                                   ('18181818-1111-1818-2222-181818181818', '15151515-1111-2222-3333-151515151515', '99999999-cccc-9999-cccc-999999999999', 1, 89999.00),
                                                                                   ('19191919-1111-1919-2222-191919191919', '15151515-1111-2222-3333-151515151515', '17171717-cccc-1717-cccc-171717171717', 1, 59999.00),
                                                                                   ('20202020-1111-2020-2222-202020202020', '16161616-1111-2222-3333-161616161616', '55555555-eeee-5555-eeee-555555555555', 1, 28999.00),
                                                                                   ('21212121-1111-2121-2222-212121212121', '17171717-1111-2222-3333-171717171717', 'eeeeeeee-bbbb-eeee-bbbb-eeeeeeeeeeee', 1, 54999.00);


INSERT INTO order_items (order_item_id, order_id, product_id, quantity, price) VALUES
                                                                                   ('22222222-1111-2222-2222-222222222222', '18181818-1111-2222-3333-181818181818', '77777777-aaaa-7777-aaaa-777777777777', 2, 17999.00),
                                                                                   ('23232323-1111-2323-2222-232323232323', '18181818-1111-2222-3333-181818181818', '20202020-ffff-2020-ffff-202020202020', 1, 59999.00),
                                                                                   ('24242424-1111-2424-2222-242424242424', '18181818-1111-2222-3333-181818181818', '13131313-eeee-1313-eeee-131313131313', 1, 24999.00);

--changeset test-data:2
--comment: Add cost_price to products, update order_items with cost_price, add test supplies

-- ============================================================
-- 1. Устанавливаем себестоимость товаров (цена закупки)
-- ============================================================
UPDATE products SET cost_price = 89999.00 WHERE product_id = '11111111-aaaa-1111-aaaa-111111111111'; -- iPhone 15 Pro
UPDATE products SET cost_price = 69999.00 WHERE product_id = '22222222-bbbb-2222-bbbb-222222222222'; -- iPhone 15
UPDATE products SET cost_price = 83999.00 WHERE product_id = '33333333-cccc-3333-cccc-333333333333'; -- Samsung S24
UPDATE products SET cost_price = 28999.00 WHERE product_id = '44444444-dddd-4444-dddd-444444444444'; -- Samsung A54
UPDATE products SET cost_price = 20999.00 WHERE product_id = '55555555-eeee-5555-eeee-555555555555'; -- Xiaomi Note 13
UPDATE products SET cost_price = 67999.00 WHERE product_id = '66666666-ffff-6666-ffff-666666666666'; -- Xiaomi 14 Ultra
UPDATE products SET cost_price = 12999.00 WHERE product_id = '77777777-aaaa-7777-aaaa-777777777777'; -- Irbis NB261
UPDATE products SET cost_price = 17999.00 WHERE product_id = '88888888-bbbb-8888-bbbb-888888888888'; -- Irbis NB275
UPDATE products SET cost_price = 69999.00 WHERE product_id = '99999999-cccc-9999-cccc-999999999999'; -- Asus TUF
UPDATE products SET cost_price = 102999.00 WHERE product_id = 'aaaaaaaa-dddd-aaaa-dddd-aaaaaaaaaaaa'; -- MSI Katana
UPDATE products SET cost_price = 119999.00 WHERE product_id = 'bbbbbbbb-eeee-bbbb-eeee-bbbbbbbbbbbb'; -- iPad Pro
UPDATE products SET cost_price = 61999.00 WHERE product_id = 'cccccccc-ffff-cccc-ffff-cccccccccccc'; -- iPad Air
UPDATE products SET cost_price = 69999.00 WHERE product_id = 'dddddddd-aaaa-dddd-aaaa-dddddddddddd'; -- Samsung Tab S9
UPDATE products SET cost_price = 44999.00 WHERE product_id = 'eeeeeeee-bbbb-eeee-bbbb-eeeeeeeeeeee'; -- PS5
UPDATE products SET cost_price = 41999.00 WHERE product_id = 'ffffffff-cccc-ffff-cccc-ffffffffffff'; -- Xbox Series X
UPDATE products SET cost_price = 24999.00 WHERE product_id = '12121212-dddd-1212-dddd-121212121212'; -- Sony WH-1000XM5
UPDATE products SET cost_price = 18999.00 WHERE product_id = '13131313-eeee-1313-eeee-131313131313'; -- AirPods Pro 2
UPDATE products SET cost_price = 11999.00 WHERE product_id = '14141414-ffff-1414-ffff-141414141414'; -- Яндекс Станция 2
UPDATE products SET cost_price = 9999.00 WHERE product_id = '15151515-aaaa-1515-aaaa-151515151515'; -- SberBoom
UPDATE products SET cost_price = 35999.00 WHERE product_id = '16161616-bbbb-1616-bbbb-161616161616'; -- Canon EOS
UPDATE products SET cost_price = 45999.00 WHERE product_id = '17171717-cccc-1717-cccc-171717171717'; -- Холодильник LG
UPDATE products SET cost_price = 34999.00 WHERE product_id = '18181818-dddd-1818-dddd-181818181818'; -- Стиральная машина
UPDATE products SET cost_price = 5599.00 WHERE product_id = '19191919-eeee-1919-eeee-191919191919'; -- Микроволновка
UPDATE products SET cost_price = 46999.00 WHERE product_id = '20202020-ffff-2020-ffff-202020202020'; -- Dyson

-- ============================================================
-- 2. Добавляем себестоимость в существующие позиции заказов
--    (фиксируем ту же цену, что сейчас у товара)
-- ============================================================
UPDATE order_items SET cost_price = 89999.00 WHERE order_item_id = 'aaaaaaaa-1111-aaaa-2222-aaaaaaaaaaaa';
UPDATE order_items SET cost_price = 24999.00 WHERE order_item_id = 'bbbbbbbb-1111-bbbb-2222-bbbbbbbbbbbb';
UPDATE order_items SET cost_price = 119999.00 WHERE order_item_id = 'cccccccc-1111-cccc-2222-cccccccccccc';
UPDATE order_items SET cost_price = 61999.00 WHERE order_item_id = 'dddddddd-1111-dddd-2222-dddddddddddd';
UPDATE order_items SET cost_price = 69999.00 WHERE order_item_id = 'eeeeeeee-1111-eeee-2222-eeeeeeeeeeee';
UPDATE order_items SET cost_price = 45999.00 WHERE order_item_id = 'ffffffff-1111-ffff-2222-ffffffffffff';
UPDATE order_items SET cost_price = 83999.00 WHERE order_item_id = '12121212-1111-1212-2222-121212121212';
UPDATE order_items SET cost_price = 69999.00 WHERE order_item_id = '13131313-1111-1313-2222-131313131313';
UPDATE order_items SET cost_price = 5599.00 WHERE order_item_id = '14141414-1111-1414-2222-141414141414';
UPDATE order_items SET cost_price = 102999.00 WHERE order_item_id = '15151515-1111-1515-2222-151515151515';
UPDATE order_items SET cost_price = 20999.00 WHERE order_item_id = '16161616-1111-1616-2222-161616161616';
UPDATE order_items SET cost_price = 11999.00 WHERE order_item_id = '17171717-1111-1717-2222-171717171717';
UPDATE order_items SET cost_price = 69999.00 WHERE order_item_id = '18181818-1111-1818-2222-181818181818';
UPDATE order_items SET cost_price = 45999.00 WHERE order_item_id = '19191919-1111-1919-2222-191919191919';
UPDATE order_items SET cost_price = 20999.00 WHERE order_item_id = '20202020-1111-2020-2222-202020202020';
UPDATE order_items SET cost_price = 41999.00 WHERE order_item_id = '21212121-1111-2121-2222-212121212121';

-- Позиции заказа 18181818... (три товара)
UPDATE order_items SET cost_price = 12999.00 WHERE order_item_id = '22222222-1111-2222-2222-222222222222';
UPDATE order_items SET cost_price = 46999.00 WHERE order_item_id = '23232323-1111-2323-2222-232323232323';
UPDATE order_items SET cost_price = 18999.00 WHERE order_item_id = '24242424-1111-2424-2222-242424242424';

-- ============================================================
-- 3. Тестовые поставки
-- ============================================================

-- Поставка №1 (завершена, пополнила склад в прошлом)
INSERT INTO supplies (supply_id, supplier_id, supply_number, supply_date, status, notes)
VALUES ('aaaa1111-1111-aaaa-1111-aaaa11111111',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'SUP-20260305-00001',
        '2026-03-05 09:30:00',
        'COMPLETED',
        'Первая партия телефонов iPhone');

INSERT INTO supply_items (supply_item_id, supply_id, product_id, quantity, price_per_unit)
VALUES
    ('4ea37542-2177-4b77-8ea1-15bcfeac4d05', 'aaaa1111-1111-aaaa-1111-aaaa11111111', '11111111-aaaa-1111-aaaa-111111111111', 5, 89999.00),
    ('58eb2393-faaf-438e-a4a5-e13dce8d9b9d', 'aaaa1111-1111-aaaa-1111-aaaa11111111', '22222222-bbbb-2222-bbbb-222222222222', 3, 69999.00);

-- Поставка №2 (завершена, бытовая техника)
INSERT INTO supplies (supply_id, supplier_id, supply_number, supply_date, status, notes)
VALUES ('bbbb2222-2222-bbbb-2222-bbbb22222222',
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        'SUP-20260307-00002',
        '2026-03-07 11:00:00',
        'COMPLETED',
        'Поступление пылесосов и холодильников');

INSERT INTO supply_items (supply_item_id, supply_id, product_id, quantity, price_per_unit)
VALUES
    ('eaf8b63d-dd49-4816-a97f-236dd65eed5a', 'bbbb2222-2222-bbbb-2222-bbbb22222222', '17171717-cccc-1717-cccc-171717171717', 2, 45999.00),
    ('8c66a8c3-f07c-434e-b64e-d39926589bc7', 'bbbb2222-2222-bbbb-2222-bbbb22222222', '20202020-ffff-2020-ffff-202020202020', 4, 46999.00);

-- Поставка №3 (ожидает завершения)
INSERT INTO supplies (supply_id, supplier_id, supply_number, supply_date, status, notes)
VALUES ('cccc3333-3333-cccc-3333-cccc33333333',
        'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        'SUP-20260312-00003',
        '2026-03-12 14:15:00',
        'PENDING',
        'Ноутбуки и планшеты');

INSERT INTO supply_items (supply_item_id, supply_id, product_id, quantity, price_per_unit)
VALUES
    ('50a1ac87-6615-4c19-8335-b013e323f9bb', 'cccc3333-3333-cccc-3333-cccc33333333', '99999999-cccc-9999-cccc-999999999999', 3, 69999.00),
    ('eb2a3fe6-6c68-4fdf-a920-85c6f5b854f6', 'cccc3333-3333-cccc-3333-cccc33333333', 'bbbbbbbb-eeee-bbbb-eeee-bbbbbbbbbbbb', 2, 119999.00),
    ('17f80acf-4090-45f3-839d-caa83ed1d41a', 'cccc3333-3333-cccc-3333-cccc33333333', 'dddddddd-aaaa-dddd-aaaa-dddddddddddd', 5, 69999.00);

-- admin pass: admin123
-- manager pass: manager123
INSERT INTO users (user_id, username, password, role, full_name, enabled)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'admin', '$2a$10$50C3EUMQP/PZU9NshSksSuLtk4A7dixlfaOFHXvN9lnBV1J4rFp8S', 'ADMIN', 'Администратор', true),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'manager', '$2a$10$o3zMDO36IGPIZ7qmLUnTg.bX46ABl8Q6uweeAEn3ROzYK.neyUX3e', 'MANAGER', 'Менеджер', true);

INSERT INTO settings (id, shop_name, address, phone, email)
VALUES ('00000000-0000-0000-0000-000000000001', 'Магазин техники', 'г. Ваш Город, ул. Примерная, 1', '+7 (000) 000-00-00', 'info@shop.ru');

--changeset test-data:3
--comment: Add VAT rates to products and calculate VAT for order_items

-- ============================================================
-- 1. Устанавливаем ставки НДС для товаров
-- ============================================================
-- Большинство товаров – стандартная ставка 20%
UPDATE products SET vat_rate = 20
WHERE product_id IN (
                     '11111111-aaaa-1111-aaaa-111111111111', '22222222-bbbb-2222-bbbb-222222222222',
                     '33333333-cccc-3333-cccc-333333333333', '44444444-dddd-4444-dddd-444444444444',
                     '55555555-eeee-5555-eeee-555555555555', '66666666-ffff-6666-ffff-666666666666',
                     '77777777-aaaa-7777-aaaa-777777777777', '88888888-bbbb-8888-bbbb-888888888888',
                     '99999999-cccc-9999-cccc-999999999999', 'aaaaaaaa-dddd-aaaa-dddd-aaaaaaaaaaaa',
                     'bbbbbbbb-eeee-bbbb-eeee-bbbbbbbbbbbb', 'cccccccc-ffff-cccc-ffff-cccccccccccc',
                     'dddddddd-aaaa-dddd-aaaa-dddddddddddd', 'eeeeeeee-bbbb-eeee-bbbb-eeeeeeeeeeee',
                     'ffffffff-cccc-ffff-cccc-ffffffffffff', '16161616-bbbb-1616-bbbb-161616161616',
                     '17171717-cccc-1717-cccc-171717171717', '18181818-dddd-1818-dddd-181818181818',
                     '19191919-eeee-1919-eeee-191919191919', '20202020-ffff-2020-ffff-202020202020'
    );

-- Аудиотехника и наушники – льготная ставка 10% (пример разнообразия)
UPDATE products SET vat_rate = 10
WHERE product_id IN (
                     '12121212-dddd-1212-dddd-121212121212',  -- Sony WH-1000XM5
                     '13131313-eeee-1313-eeee-131313131313'   -- AirPods Pro 2
    );

-- Умные колонки – без НДС (0%) как пример необлагаемых товаров
UPDATE products SET vat_rate = 0
WHERE product_id IN (
                     '14141414-ffff-1414-ffff-141414141414',  -- Яндекс Станция 2
                     '15151515-aaaa-1515-aaaa-151515151515'   -- SberBoom
    );

-- ============================================================
-- 2. Рассчитываем НДС для всех позиций заказов
-- ============================================================
UPDATE order_items oi
SET
    vat_rate = p.vat_rate,
    vat_amount = CASE
                     WHEN p.vat_rate IS NOT NULL AND p.vat_rate > 0
                         THEN ROUND(oi.price * p.vat_rate / (100 + p.vat_rate), 2)
                     ELSE 0
        END
    FROM products p
WHERE oi.product_id = p.product_id;