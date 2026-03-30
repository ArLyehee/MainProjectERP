package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.CustomerOrder;
import com.gaebalfan.erp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody CustomerOrder order) {
        service.insert(order);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Map<String, String>> approve(@PathVariable Long id) {
        String resultStatus = service.approve(id);
        return ResponseEntity.ok(Map.of("status", resultStatus));
    }

    @PatchMapping("/{id}/hold")
    public ResponseEntity<Void> hold(@PathVariable Long id) {
        service.hold(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<Void> reopen(@PathVariable Long id) {
        service.reopen(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ship")
    public ResponseEntity<Void> ship(@PathVariable Long id) {
        service.ship(id);
        return ResponseEntity.ok().build();
    }
}
