package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Product;
import com.gaebalfan.erp.domain.PurchaseOrder;
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
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        service.updateStatus(id, body.get("status"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/supplier")
    public ResponseEntity<Void> updateSupplier(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        service.updateSupplier(id, body.get("supplierId"));
        return ResponseEntity.ok().build();
    }

    // 작업2: 거래처별 취급 부품 조회
    @GetMapping("/supplier/{supplierId}/products")
    public ResponseEntity<List<Product>> findProductsBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(service.findProductsBySupplier(supplierId));
    }
}
