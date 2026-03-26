package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.ProductionReceipt;
import com.gaebalfan.erp.service.ProductionReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody Map<String, Object> body) {
        ProductionReceipt pr = new ProductionReceipt();
        pr.setWorkOrderId(Long.parseLong(body.get("workOrderId").toString()));
        pr.setProductId(Long.parseLong(body.get("productId").toString()));
        pr.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        if (body.get("receiptDate") != null && !body.get("receiptDate").toString().isEmpty()) {
            String dateStr = body.get("receiptDate").toString();
            if (dateStr.contains("T")) {
                dateStr = dateStr.substring(0, dateStr.indexOf("T"));
            }
            pr.setReceiptDate(java.time.LocalDate.parse(dateStr).atStartOfDay());
        } else {
            pr.setReceiptDate(java.time.LocalDateTime.now());
        }
        service.receive(pr, Long.parseLong(body.get("warehouseId").toString()));
        return ResponseEntity.ok().build();
    }
}