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
        if (body.get("poId") == null || body.get("poId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "발주 번호를 선택하세요.").build();
        if (body.get("productId") == null || body.get("productId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "제품을 선택하세요.").build();
        if (body.get("warehouseId") == null || body.get("warehouseId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "입고 창고를 선택하세요.").build();
        int qty = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : 0;
        if (qty <= 0)
            return ResponseEntity.badRequest().header("X-Error-Message", "수량은 1 이상이어야 합니다.").build();

        Receipt receipt = new Receipt();
        receipt.setPoId(body.get("poId").toString());
        receipt.setProductId(Long.parseLong(body.get("productId").toString()));
        receipt.setQuantity(qty);
        if (body.get("receiptDate") != null && !body.get("receiptDate").toString().isEmpty()) {
            receipt.setReceiptDate(java.time.LocalDate.parse(body.get("receiptDate").toString()).atStartOfDay());
        } else {
            receipt.setReceiptDate(java.time.LocalDateTime.now());
        }
        service.insert(receipt);

        // 재고 자동 반영
        Inventory inv = new Inventory();
        inv.setProductId(receipt.getProductId());
        inv.setWarehouseId(Long.parseLong(body.get("warehouseId").toString()));
        inv.setQuantity(receipt.getQuantity());
        inventoryMapper.insert(inv);

        // 발주 상태 → RECEIVED
        try {
            purchaseOrderMapper.updateStatus(Long.parseLong(receipt.getPoId()), "RECEIVED");
        } catch (NumberFormatException ignored) {}

        return ResponseEntity.ok().build();
    }
}
