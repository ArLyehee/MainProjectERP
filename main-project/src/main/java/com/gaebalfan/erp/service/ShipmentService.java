package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.domain.Shipment;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.ShipmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ShipmentService {

    private final ShipmentMapper mapper;
    private final InventoryMapper inventoryMapper;

    public ShipmentService(ShipmentMapper mapper, InventoryMapper inventoryMapper) {
        this.mapper = mapper;
        this.inventoryMapper = inventoryMapper;
    }

    public List<Shipment> findAll() {
        return mapper.findAll();
    }

    public List<Shipment> findAllPaged(int page, int size) {
        return mapper.findAllPaged((page - 1) * size, size);
    }

    public int count() {
        return mapper.count();
    }

    public Shipment findById(Long id) {
        return mapper.findById(id);
    }

    @Transactional
    public void ship(Shipment obj) {
        // 재고 부족 검증
        Inventory stock = inventoryMapper.findByProductAndWarehouse(obj.getProductId(), obj.getWarehouseId());
        int currentQty = (stock != null) ? stock.getQuantity() : 0;
        if (currentQty < obj.getQuantity()) {
            throw new IllegalStateException("재고 부족: 현재 재고 " + currentQty + "개, 출고 요청 " + obj.getQuantity() + "개");
        }
        // 1. 출고 내역 저장
        mapper.insert(obj);
        // 2. 재고 차감
        inventoryMapper.updateQuantity(obj.getProductId(), obj.getWarehouseId(), -obj.getQuantity());
    }
}
