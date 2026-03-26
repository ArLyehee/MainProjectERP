package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.Receipt;
import com.gaebalfan.erp.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService service;

    public ReceiptController(ReceiptService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Receipt>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody Map<String, Object> body) {
        if (body.get("poId") == null || body.get("poId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "발주 번호를 선택하세요.").build();
        if (body.get("productId") == null || body.get("productId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "제품을 선택하세요.").build();
        if (body.get("warehouseId") == null || body.get("warehouseId").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "입고 창고를 선택하세요.").build();
        int qty;
        try {
            qty = body.get("quantity") != null && !body.get("quantity").toString().isBlank()
                    ? Integer.parseInt(body.get("quantity").toString()) : 0;
        } catch (NumberFormatException e) {
            qty = 0;
        }
        if (qty <= 0)
            return ResponseEntity.badRequest().header("X-Error-Message", "수량을 입력하세요. (1 이상)").build();

        Receipt receipt = new Receipt();
        receipt.setPoId(body.get("poId").toString());
        receipt.setProductId(Long.parseLong(body.get("productId").toString()));
        receipt.setQuantity(qty);
        if (body.get("receiptDate") != null && !body.get("receiptDate").toString().isEmpty()) {
            receipt.setReceiptDate(java.time.LocalDate.parse(body.get("receiptDate").toString()).atStartOfDay());
        } else {
            receipt.setReceiptDate(java.time.LocalDateTime.now());
        }

        service.receive(receipt, Long.parseLong(body.get("warehouseId").toString()));
        return ResponseEntity.ok().build();
    }
}
