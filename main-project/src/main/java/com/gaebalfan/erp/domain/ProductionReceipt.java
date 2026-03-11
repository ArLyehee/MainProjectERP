package com.gaebalfan.erp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductionReceipt {
    private Long          productionId;
    private Long          workOrderId;
    private Long          productId;
    private int           quantity;
    private LocalDate     receiptDate;
    private LocalDateTime createdAt;

    // 조인용
    private String        productName;

    public Long          getProductionId() { return productionId; }
    public Long          getWorkOrderId()  { return workOrderId; }
    public Long          getProductId()    { return productId; }
    public int           getQuantity()     { return quantity; }
    public LocalDate     getReceiptDate()  { return receiptDate; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public String        getProductName()  { return productName; }

    public void setProductionId(Long productionId)        { this.productionId = productionId; }
    public void setWorkOrderId(Long workOrderId)          { this.workOrderId = workOrderId; }
    public void setProductId(Long productId)              { this.productId = productId; }
    public void setQuantity(int quantity)                 { this.quantity = quantity; }
    public void setReceiptDate(LocalDate receiptDate)     { this.receiptDate = receiptDate; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setProductName(String productName)        { this.productName = productName; }
}
