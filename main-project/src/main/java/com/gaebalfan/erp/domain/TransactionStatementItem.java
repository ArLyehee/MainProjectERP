package com.gaebalfan.erp.domain;

import java.math.BigDecimal;

public class TransactionStatementItem {
    private Long       itemId;
    private Long       statementId;
    private String     itemName;
    private int        quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;

    public Long getItemId() { return itemId; }
    public Long getStatementId() { return statementId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getAmount() { return amount; }

    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setStatementId(Long statementId) { this.statementId = statementId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
