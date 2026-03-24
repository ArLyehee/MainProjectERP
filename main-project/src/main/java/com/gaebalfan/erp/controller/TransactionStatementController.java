package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.TransactionStatement;
import com.gaebalfan.erp.domain.TransactionStatementItem;
import com.gaebalfan.erp.service.TransactionStatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction-statements")
public class TransactionStatementController {

    private final TransactionStatementService service;

    public TransactionStatementController(TransactionStatementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TransactionStatement>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionStatement> findById(@PathVariable Long id) {
        TransactionStatement s = service.findById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(s);
    }

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<TransactionStatement> insert(@RequestBody Map<String, Object> body) {
        if (body.get("customerName") == null || body.get("customerName").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "거래처명을 입력하세요.").build();

        TransactionStatement stmt = new TransactionStatement();
        stmt.setCustomerName(body.get("customerName").toString());
        stmt.setSupplyName(body.get("supplyName") != null ? body.get("supplyName").toString() : "");
        stmt.setCustomerAddr(body.get("customerAddr") != null ? body.get("customerAddr").toString() : "");
        stmt.setCustomerTel(body.get("customerTel") != null ? body.get("customerTel").toString() : "");
        stmt.setCustomerBizNo(body.get("customerBizNo") != null ? body.get("customerBizNo").toString() : "");
        stmt.setNotes(body.get("notes") != null ? body.get("notes").toString() : "");
        stmt.setManagerName(body.get("managerName") != null ? body.get("managerName").toString() : "");

        if (body.get("issueDate") != null && !body.get("issueDate").toString().isEmpty()) {
            stmt.setIssueDate(LocalDate.parse(body.get("issueDate").toString()));
        } else {
            stmt.setIssueDate(LocalDate.now());
        }

        List<TransactionStatementItem> items = new ArrayList<>();
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) body.get("items");
        if (rawItems == null || rawItems.isEmpty())
            return ResponseEntity.badRequest().header("X-Error-Message", "품목을 1개 이상 입력하세요.").build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> raw : rawItems) {
            TransactionStatementItem item = new TransactionStatementItem();
            item.setItemName(raw.get("itemName").toString());
            item.setQuantity(Integer.parseInt(raw.get("quantity").toString()));
            item.setUnitPrice(new BigDecimal(raw.get("unitPrice").toString()));
            item.setAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getAmount());
            items.add(item);
        }
        BigDecimal taxAmount = body.get("taxAmount") != null
                ? new BigDecimal(body.get("taxAmount").toString()).setScale(0, java.math.RoundingMode.HALF_UP)
                : totalAmount.multiply(new BigDecimal("0.1")).setScale(0, java.math.RoundingMode.HALF_UP);
        stmt.setTotalAmount(totalAmount);
        stmt.setTaxAmount(taxAmount);
        stmt.setGrandTotal(totalAmount.add(taxAmount));

        TransactionStatement saved = service.insert(stmt, items);
        return ResponseEntity.ok(saved);
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        if (body.get("customerName") == null || body.get("customerName").toString().isBlank())
            return ResponseEntity.badRequest().header("X-Error-Message", "거래처명을 입력하세요.").build();

        TransactionStatement stmt = new TransactionStatement();
        stmt.setCustomerName(body.get("customerName").toString());
        stmt.setSupplyName(body.get("supplyName") != null ? body.get("supplyName").toString() : "");
        stmt.setCustomerAddr(body.get("customerAddr") != null ? body.get("customerAddr").toString() : "");
        stmt.setCustomerTel(body.get("customerTel") != null ? body.get("customerTel").toString() : "");
        stmt.setCustomerBizNo(body.get("customerBizNo") != null ? body.get("customerBizNo").toString() : "");
        stmt.setNotes(body.get("notes") != null ? body.get("notes").toString() : "");
        stmt.setManagerName(body.get("managerName") != null ? body.get("managerName").toString() : "");
        if (body.get("issueDate") != null && !body.get("issueDate").toString().isEmpty()) {
            stmt.setIssueDate(LocalDate.parse(body.get("issueDate").toString()));
        } else {
            stmt.setIssueDate(LocalDate.now());
        }

        List<TransactionStatementItem> items = new ArrayList<>();
        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) body.get("items");
        if (rawItems == null || rawItems.isEmpty())
            return ResponseEntity.badRequest().header("X-Error-Message", "품목을 1개 이상 입력하세요.").build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> raw : rawItems) {
            TransactionStatementItem item = new TransactionStatementItem();
            item.setItemName(raw.get("itemName").toString());
            item.setQuantity(Integer.parseInt(raw.get("quantity").toString()));
            item.setUnitPrice(new BigDecimal(raw.get("unitPrice").toString()));
            item.setAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getAmount());
            items.add(item);
        }
        BigDecimal taxAmount = body.get("taxAmount") != null
                ? new BigDecimal(body.get("taxAmount").toString()).setScale(0, java.math.RoundingMode.HALF_UP)
                : totalAmount.multiply(new BigDecimal("0.1")).setScale(0, java.math.RoundingMode.HALF_UP);
        stmt.setTotalAmount(totalAmount);
        stmt.setTaxAmount(taxAmount);
        stmt.setGrandTotal(totalAmount.add(taxAmount));

        service.update(id, stmt, items);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
