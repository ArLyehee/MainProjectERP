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

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrder> findById(@PathVariable Long id) {
        CustomerOrder order = service.findById(id);
        if (order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}/complete-shipment")
    public ResponseEntity<Map<String, Object>> completeShipment(@PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Long warehouseId = body != null && body.get("warehouseId") != null
                    ? Long.valueOf(body.get("warehouseId").toString()) : null;
            String customerName    = body != null ? (String) body.get("customerName")    : null;
            String deliveryAddress = body != null ? (String) body.get("deliveryAddress") : null;
            String notes           = body != null ? (String) body.get("notes")           : null;
            service.completeShipment(id, warehouseId, customerName, deliveryAddress, notes);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/delivery-info")
    public ResponseEntity<Void> updateDeliveryInfo(@PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String customerName    = body != null ? (String) body.get("customerName")    : null;
        String deliveryAddress = body != null ? (String) body.get("deliveryAddress") : null;
        String notes           = body != null ? (String) body.get("notes")           : null;
        service.updateDeliveryInfo(id, customerName, deliveryAddress, notes);
        return ResponseEntity.ok().build();
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

    @PatchMapping("/{id}/ready")
    public ResponseEntity<Void> ready(@PathVariable Long id) {
        service.markReady(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<Void> reopen(@PathVariable Long id) {
        service.reopen(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ship")
    public ResponseEntity<Map<String, Object>> ship(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long warehouseId = body != null && body.get("warehouseId") != null
                    ? Long.valueOf(body.get("warehouseId").toString()) : null;
            service.ship(id, warehouseId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/ship-by-work-order/{workOrderId}")
    public ResponseEntity<Map<String, Object>> shipByWorkOrder(
            @PathVariable Long workOrderId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long warehouseId = body != null && body.get("warehouseId") != null
                ? Long.valueOf(body.get("warehouseId").toString()) : null;
        boolean shipped = service.shipByWorkOrder(workOrderId, warehouseId);
        return ResponseEntity.ok(Map.of("shipped", shipped));
    }
}
