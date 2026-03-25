package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.domain.Receipt;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import com.gaebalfan.erp.mapper.ReceiptMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReceiptService {

    private final ReceiptMapper mapper;
    private final InventoryMapper inventoryMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;

    public ReceiptService(ReceiptMapper mapper, InventoryMapper inventoryMapper, PurchaseOrderMapper purchaseOrderMapper) {
        this.mapper = mapper;
        this.inventoryMapper = inventoryMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    public List<Receipt> findAll() {
        return mapper.findAll();
    }

    public List<Receipt> findAllPaged(int page, int size) {
        return mapper.findAllPaged((page - 1) * size, size);
    }

    public int count() {
        return mapper.count();
    }

    @Transactional
    public void receive(Receipt receipt, Long warehouseId) {
        // 1. 입고 내역 저장
        mapper.insert(receipt);

        // 2. 재고 자동 반영
        Inventory inv = new Inventory();
        inv.setProductId(receipt.getProductId());
        inv.setWarehouseId(warehouseId);
        inv.setQuantity(receipt.getQuantity());
        inventoryMapper.insert(inv);

        // 3. 발주 상태 → COMPLETED
        try {
            purchaseOrderMapper.updateStatus(Long.parseLong(receipt.getPoId()), "COMPLETED");
        } catch (NumberFormatException ignored) {}
    }
}
