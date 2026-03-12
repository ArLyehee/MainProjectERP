package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Sale;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.service.SaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService service;
    private final InventoryMapper inventoryMapper;

    public SaleController(SaleService service, InventoryMapper inventoryMapper) {
        this.service = service;
        this.inventoryMapper = inventoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<Sale>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Map<String, Object> body) {
        Sale sale = new Sale();
        sale.setProductId(Long.parseLong(body.get("productId").toString()));
        sale.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        sale.setUnitPrice(new BigDecimal(body.get("unitPrice").toString()));
        sale.setCostPrice(body.get("costPrice") != null ? new BigDecimal(body.get("costPrice").toString()) : BigDecimal.ZERO);
        sale.setTotalPrice(sale.getUnitPrice().multiply(BigDecimal.valueOf(sale.getQuantity())));
        if (body.get("saleDate") != null && !body.get("saleDate").toString().isEmpty()) {
            sale.setSaleDate(LocalDate.parse(body.get("saleDate").toString()));
        } else {
            sale.setSaleDate(LocalDate.now());
        }
        service.insert(sale);

        // 재고 차감 (창고 ID는 body에서 받거나 첫번째 창고 사용)
        if (body.get("warehouseId") != null) {
            inventoryMapper.updateQuantity(sale.getProductId(), Long.parseLong(body.get("warehouseId").toString()), -sale.getQuantity());
        }

        return ResponseEntity.ok().build();
    }
}
