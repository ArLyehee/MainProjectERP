package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.domain.Receipt;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import com.gaebalfan.erp.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService service;
    private final InventoryMapper inventoryMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;

    public ReceiptController(ReceiptService service, InventoryMapper inventoryMapper, PurchaseOrderMapper purchaseOrderMapper) {
        this.service = service;
        this.inventoryMapper = inventoryMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    @GetMapping
    public ResponseEntity<List<Receipt>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody Map<String, Object> body) {
        Receipt receipt = new Receipt();
        receipt.setPoId(Long.parseLong(body.get("poId").toString()));
        receipt.setProductId(Long.parseLong(body.get("productId").toString()));
        receipt.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        if (body.get("receiptDate") != null && !body.get("receiptDate").toString().isEmpty()) {
            receipt.setReceiptDate(java.time.LocalDate.parse(body.get("receiptDate").toString()));
        } else {
            receipt.setReceiptDate(java.time.LocalDate.now());
        }
        service.insert(receipt);

        // 재고 자동 반영
        Inventory inv = new Inventory();
        inv.setProductId(receipt.getProductId());
        inv.setWarehouseId(Long.parseLong(body.get("warehouseId").toString()));
        inv.setQuantity(receipt.getQuantity());
        inventoryMapper.insert(inv);

        // 발주 상태 → RECEIVED
        purchaseOrderMapper.updateStatus(receipt.getPoId(), "RECEIVED");

        return ResponseEntity.ok().build();
    }
}
