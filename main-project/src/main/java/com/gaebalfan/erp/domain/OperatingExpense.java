package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OperatingExpense {
    private Long       expenseId;
    private String     expenseType;
    private BigDecimal amount;
    private LocalDate  expenseDate;

    public Long       getExpenseId()   { return expenseId; }
    public String     getExpenseType() { return expenseType; }
    public BigDecimal getAmount()      { return amount; }
    public LocalDate  getExpenseDate() { return expenseDate; }

    public void setExpenseId(Long expenseId)           { this.expenseId = expenseId; }
    public void setExpenseType(String expenseType)     { this.expenseType = expenseType; }
    public void setAmount(BigDecimal amount)           { this.amount = amount; }
    public void setExpenseDate(LocalDate expenseDate)  { this.expenseDate = expenseDate; }
}
