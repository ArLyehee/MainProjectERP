package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Supplier;
import com.gaebalfan.erp.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Supplier obj) {
        service.insert(obj);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Supplier obj) {
        obj.setSupplierId(id);
        service.update(obj);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Map<String, Object>>> getProducts(@PathVariable Long id) {
        return ResponseEntity.ok(service.findProductsBySupplier(id));
    }

    @PostMapping("/{id}/products/{productId}")
    public ResponseEntity<Void> addProduct(@PathVariable Long id, @PathVariable Long productId) {
        service.addProduct(id, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long id, @PathVariable Long productId) {
        service.removeProduct(id, productId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/products/{productId}/primary")
    public ResponseEntity<Void> setPrimary(@PathVariable Long id, @PathVariable Long productId,
                                           @RequestBody Map<String, Boolean> body) {
        service.setPrimary(id, productId, Boolean.TRUE.equals(body.get("isPrimary")));
        return ResponseEntity.ok().build();
    }
}
