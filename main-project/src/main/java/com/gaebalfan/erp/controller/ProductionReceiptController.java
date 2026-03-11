package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.ProductionReceipt;
import com.gaebalfan.erp.service.ProductionReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/production-receipts")
public class ProductionReceiptController {

    private final ProductionReceiptService service;

    public ProductionReceiptController(ProductionReceiptService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductionReceipt>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionReceipt> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody ProductionReceipt obj) {
        service.insert(obj);
        return ResponseEntity.ok().build();
    }
}
