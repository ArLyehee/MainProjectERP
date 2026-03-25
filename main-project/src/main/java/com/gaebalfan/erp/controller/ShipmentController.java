package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Shipment;
import com.gaebalfan.erp.service.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService service;

    public ShipmentController(ShipmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Map<String, Object> body) {
        Shipment obj = new Shipment();
        obj.setProductId(Long.parseLong(body.get("productId").toString()));
        obj.setWarehouseId(Long.parseLong(body.get("warehouseId").toString()));
        obj.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        obj.setDestination(body.get("destination") != null ? body.get("destination").toString() : "");
        if (body.get("shipmentDate") != null && !body.get("shipmentDate").toString().isEmpty()) {
            obj.setShipmentDate(java.time.LocalDate.parse(body.get("shipmentDate").toString()).atStartOfDay());
        } else {
            obj.setShipmentDate(LocalDateTime.now());
        }
        service.ship(obj);
        return ResponseEntity.ok().build();
    }
}
