package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<List<Inventory>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.findByProduct(productId));
    }

    @GetMapping("/by-warehouse/{warehouseId}")
    public ResponseEntity<List<Inventory>> findByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(service.findByWarehouse(warehouseId));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Inventory obj) {
        service.insert(obj);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Inventory obj) {
        obj.setInventoryId(id);
        service.update(obj);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
