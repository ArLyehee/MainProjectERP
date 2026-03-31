package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.WorkOrder;
import com.gaebalfan.erp.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService service;

    public WorkOrderController(WorkOrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<WorkOrder>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/check-stock")
    public ResponseEntity<List<Map<String, Object>>> checkStock(
            @RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(service.checkStock(productId, quantity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrder> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody WorkOrder obj) {
        service.insert(obj);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        service.updateStatus(id, body.get("status"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/auto-order-parts")
    public ResponseEntity<Map<String, Object>> autoOrderParts(@PathVariable Long id) {
        int count = service.autoOrderShortages(id);
        return ResponseEntity.ok(Map.of("created", count));
    }
}
