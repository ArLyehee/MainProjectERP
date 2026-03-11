package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Warehouse;
import com.gaebalfan.erp.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Warehouse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Warehouse obj) {
        service.insert(obj);
        return ResponseEntity.ok().build();
    }
}
