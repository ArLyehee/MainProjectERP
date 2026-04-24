-- ① 창고명 변경 (HeidiSQL에서 실행)
USE gaebalfan_erp;

UPDATE warehouses SET warehouse_name = 'A창고' WHERE warehouse_id = 1;
UPDATE warehouses SET warehouse_name = 'B창고' WHERE warehouse_id = 2;
UPDATE warehouses SET warehouse_name = 'C창고' WHERE warehouse_id = 3;

-- ② inventory에 created_at 컬럼 추가 (처음 입고된 날짜)
ALTER TABLE inventory
    ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP;

-- 기존 데이터는 last_update 값으로 채움
UPDATE inventory SET created_at = last_update WHERE created_at IS NULL;

-- 확인
SELECT * FROM warehouses;
SELECT inventory_id, product_id, warehouse_id, quantity, last_update, created_at FROM inventory LIMIT 10;
