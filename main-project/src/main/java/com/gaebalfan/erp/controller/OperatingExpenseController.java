package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.OperatingExpense;
import com.gaebalfan.erp.service.OperatingExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class OperatingExpenseController {

    private final OperatingExpenseService service;

    public OperatingExpenseController(OperatingExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OperatingExpense>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Map<String, Object> body) {
        OperatingExpense expense = new OperatingExpense();
        expense.setExpenseType(body.get("expenseType").toString());
        expense.setAmount(new BigDecimal(body.get("amount").toString()));
        if (body.get("expenseDate") != null && !body.get("expenseDate").toString().isEmpty()) {
            expense.setExpenseDate(LocalDate.parse(body.get("expenseDate").toString()));
        } else {
            expense.setExpenseDate(LocalDate.now());
        }
        service.insert(expense);
        return ResponseEntity.ok().build();
    }
}
