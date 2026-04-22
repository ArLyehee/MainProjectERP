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
    public ResponseEntity<Map<String, Object>> insert(@RequestBody Map<String, Object> body) {
        if (body.get("productId") == null || body.get("productId").toString().isBlank())
            return ResponseEntity.badRequest().body(Map.of("message", "제품을 선택하세요."));
        int qty = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : 0;
        if (qty <= 0)
            return ResponseEntity.badRequest().body(Map.of("message", "수량은 1 이상이어야 합니다."));
        if (body.get("unitPrice") == null || body.get("unitPrice").toString().isBlank() ||
            new BigDecimal(body.get("unitPrice").toString()).compareTo(BigDecimal.ZERO) <= 0)
            return ResponseEntity.badRequest().body(Map.of("message", "단가는 0보다 커야 합니다."));

        Sale sale = new Sale();
        sale.setProductId(Long.parseLong(body.get("productId").toString()));
        sale.setQuantity(qty);
        sale.setUnitPrice(new BigDecimal(body.get("unitPrice").toString()));
        sale.setCostPrice(body.get("costPrice") != null ? new BigDecimal(body.get("costPrice").toString()) : BigDecimal.ZERO);
        sale.setTotalPrice(sale.getUnitPrice().multiply(BigDecimal.valueOf(sale.getQuantity())));
        if (body.get("saleDate") != null && !body.get("saleDate").toString().isEmpty()) {
            sale.setSaleDate(LocalDate.parse(body.get("saleDate").toString()));
        } else {
            sale.setSaleDate(LocalDate.now());
        }

        // 재고 부족 검증
        if (body.get("warehouseId") != null && !body.get("warehouseId").toString().isBlank()) {
            Long warehouseId = Long.parseLong(body.get("warehouseId").toString());
            com.gaebalfan.erp.domain.Inventory stock =
                    inventoryMapper.findByProductAndWarehouse(sale.getProductId(), warehouseId);
            int currentQty = (stock != null) ? stock.getQuantity() : 0;
            if (currentQty < qty)
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "재고 부족: 현재 재고 " + currentQty + "개, 판매 요청 " + qty + "개"));
            service.insert(sale);
            inventoryMapper.updateQuantity(sale.getProductId(), warehouseId, -qty);
        } else {
            service.insert(sale);
        }

        return ResponseEntity.ok(Map.of("success", true));
    }
}
