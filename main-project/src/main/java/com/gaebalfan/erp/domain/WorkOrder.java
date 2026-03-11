package com.gaebalfan.erp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WorkOrder {
    private Long          workOrderId;
    private Long          productId;
    private int           quantity;
    private LocalDate     startDate;
    private String        status;  // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private LocalDateTime createdAt;

    // 조인용
    private String        productName;

    public Long          getWorkOrderId() { return workOrderId; }
    public Long          getProductId()   { return productId; }
    public int           getQuantity()    { return quantity; }
    public LocalDate     getStartDate()   { return startDate; }
    public String        getStatus()      { return status; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public String        getProductName() { return productName; }

    public void setWorkOrderId(Long workOrderId)          { this.workOrderId = workOrderId; }
    public void setProductId(Long productId)              { this.productId = productId; }
    public void setQuantity(int quantity)                 { this.quantity = quantity; }
    public void setStartDate(LocalDate startDate)         { this.startDate = startDate; }
    public void setStatus(String status)                  { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setProductName(String productName)        { this.productName = productName; }
}
