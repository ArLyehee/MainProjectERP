package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Product;
import com.gaebalfan.erp.domain.PurchaseOrder;
import com.gaebalfan.erp.domain.PurchaseOrderItem;
import com.gaebalfan.erp.domain.Receipt;
import com.gaebalfan.erp.service.PurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody PurchaseOrder obj) {
        service.insert(obj);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            service.updateStatus(id, body.get("status"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage() + (e.getCause() != null ? " / " + e.getCause().getMessage() : ""));
        }
    }

    @PatchMapping("/items/{poItemId}/price")
    public ResponseEntity<Void> updateItemPrice(@PathVariable Long poItemId, @RequestBody Map<String, Object> body) {
        java.math.BigDecimal price = new java.math.BigDecimal(body.get("unitPrice").toString());
        service.updateItemPrice(poItemId, price);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/supplier")
    public ResponseEntity<Void> updateSupplier(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        service.updateSupplier(id, body.get("supplierId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<Map<String, Object>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDetail(id));
    }

    @GetMapping("/by-code/{poCode}/detail")
    public ResponseEntity<Map<String, Object>> getDetailByCode(@PathVariable String poCode) {
        return ResponseEntity.ok(service.getDetailByCode(poCode));
    }

    // 작업2: 거래처별 취급 부품 조회
    @GetMapping("/supplier/{supplierId}/products")
    public ResponseEntity<List<Product>> findProductsBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(service.findProductsBySupplier(supplierId));
    }
}
