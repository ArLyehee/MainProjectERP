package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class PurchaseOrder {
    private Long          poId;
    private Long          supplierId;
    private LocalDateTime orderDate;
    private String        status;   // PENDING, APPROVED, COMPLETED, CANCELLED

    // 조인용
    private String        supplierName;

    public Long          getPoId()         { return poId; }
    public Long          getSupplierId()   { return supplierId; }
    public LocalDateTime getOrderDate()    { return orderDate; }
    public String        getStatus()       { return status; }
    public String        getSupplierName() { return supplierName; }

    public void setPoId(Long poId)                        { this.poId = poId; }
    public void setSupplierId(Long supplierId)            { this.supplierId = supplierId; }
    public void setOrderDate(LocalDateTime orderDate)     { this.orderDate = orderDate; }
    public void setStatus(String status)                  { this.status = status; }
    public void setSupplierName(String supplierName)      { this.supplierName = supplierName; }
}
