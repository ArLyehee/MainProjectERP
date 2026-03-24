package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionStatement {
    private Long          statementId;
    private String        statementNo;
    private LocalDate     issueDate;
    private String        customerName;
    private String        supplyName;
    private String        customerAddr;
    private String        customerTel;
    private String        customerBizNo;
    private BigDecimal    totalAmount;
    private BigDecimal    taxAmount;
    private BigDecimal    grandTotal;
    private String        notes;
    private String        managerName;
    private LocalDateTime createdAt;
    private List<TransactionStatementItem> items;

    public Long getStatementId() { return statementId; }
    public String getStatementNo() { return statementNo; }
    public LocalDate getIssueDate() { return issueDate; }
    public String getCustomerName() { return customerName; }
    public String getSupplyName() { return supplyName; }
    public String getCustomerAddr() { return customerAddr; }
    public String getCustomerTel() { return customerTel; }
    public String getCustomerBizNo() { return customerBizNo; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public String getNotes() { return notes; }
    public String getManagerName() { return managerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<TransactionStatementItem> getItems() { return items; }

    public void setStatementId(Long statementId) { this.statementId = statementId; }
    public void setStatementNo(String statementNo) { this.statementNo = statementNo; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setSupplyName(String supplyName) { this.supplyName = supplyName; }
    public void setCustomerAddr(String customerAddr) { this.customerAddr = customerAddr; }
    public void setCustomerTel(String customerTel) { this.customerTel = customerTel; }
    public void setCustomerBizNo(String customerBizNo) { this.customerBizNo = customerBizNo; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setItems(List<TransactionStatementItem> items) { this.items = items; }
}
