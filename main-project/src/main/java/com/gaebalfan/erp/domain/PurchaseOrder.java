package com.gaebalfan.erp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseOrder {
    private Long          poId;
    private Long          supplierId;
    private LocalDate     orderDate;
    private String        status;   // PENDING, APPROVED, COMPLETED, CANCELLED
    private LocalDateTime createdAt;

    // 조인용
    private String        supplierName;

    public Long          getPoId()         { return poId; }
    public Long          getSupplierId()   { return supplierId; }
    public LocalDate     getOrderDate()    { return orderDate; }
    public String        getStatus()       { return status; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public String        getSupplierName() { return supplierName; }

    public void setPoId(Long poId)                        { this.poId = poId; }
    public void setSupplierId(Long supplierId)            { this.supplierId = supplierId; }
    public void setOrderDate(LocalDate orderDate)         { this.orderDate = orderDate; }
    public void setStatus(String status)                  { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setSupplierName(String supplierName)      { this.supplierName = supplierName; }
}
