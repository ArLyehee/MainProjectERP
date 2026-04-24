-- 창고 구분 컬럼 추가 (완제품창고 / 자재창고)
ALTER TABLE warehouses
    ADD COLUMN warehouse_type VARCHAR(20) NOT NULL DEFAULT '완제품창고';

-- A창고(warehouse_id=1)를 자재창고로 지정
UPDATE warehouses SET warehouse_type = '자재창고' WHERE warehouse_id = 1;
