package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.domain.Shipment;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.service.ShipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService service;
    private final InventoryMapper inventoryMapper;

    public ShipmentController(ShipmentService service, InventoryMapper inventoryMapper) {
        this.service = service;
        this.inventoryMapper = inventoryMapper;
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
            obj.setShipmentDate(LocalDate.parse(body.get("shipmentDate").toString()));
        } else {
            obj.setShipmentDate(LocalDate.now());
        }
        // 재고 부족 검증
        Inventory stock = inventoryMapper.findByProductAndWarehouse(obj.getProductId(), obj.getWarehouseId());
        int currentQty = (stock != null) ? stock.getQuantity() : 0;
        if (currentQty < obj.getQuantity()) {
            return ResponseEntity.badRequest()
                    .header("X-Error-Message", "재고 부족: 현재 재고 " + currentQty + "개, 출고 요청 " + obj.getQuantity() + "개")
                    .build();
        }

        service.insert(obj);

        // 재고 차감
        inventoryMapper.updateQuantity(obj.getProductId(), obj.getWarehouseId(), -obj.getQuantity());

        return ResponseEntity.ok().build();
    }
}
