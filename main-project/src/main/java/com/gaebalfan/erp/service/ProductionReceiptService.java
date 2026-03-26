package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.domain.ProductionReceipt;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.ProductionReceiptMapper;
import com.gaebalfan.erp.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductionReceiptService {

    private final ProductionReceiptMapper mapper;
    private final InventoryMapper inventoryMapper;
    private final WorkOrderMapper workOrderMapper;

    public ProductionReceiptService(ProductionReceiptMapper mapper, InventoryMapper inventoryMapper, WorkOrderMapper workOrderMapper) {
        this.mapper = mapper;
        this.inventoryMapper = inventoryMapper;
        this.workOrderMapper = workOrderMapper;
    }

    public List<ProductionReceipt> findAll() {
        return mapper.findAll();
    }

    @Transactional
    public void receive(ProductionReceipt pr, Long warehouseId) {
        // 1. 생산입고 내역 저장
        mapper.insert(pr);
        // 2. 완제품 재고 자동 반영
        Inventory inv = new Inventory();
        inv.setProductId(pr.getProductId());
        inv.setWarehouseId(warehouseId);
        inv.setQuantity(pr.getQuantity());
        inventoryMapper.insert(inv);
        // 3. 작업지시 상태 → COMPLETED
        workOrderMapper.updateStatus(pr.getWorkOrderId(), "COMPLETED");
    }
}
