package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerOrder {
    private Long          orderId;
    private String        orderNo;        // ORD-20260330-001
    private String        customerName;
    private Long          productId;
    private int           quantity;
    private BigDecimal    unitPrice;
    private String        status;
    // 대기, 보류, ACCEPTED(생산중), 발주, 출고준비, COMPLETED(출고완료)

    private Long          workOrderId;    // 생성된 작업지시 ID
    private Integer       purchaseOrderId; // 생성된 발주 ID
    private Long          shipmentId;     // 생성된 출고 ID

    private String        notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 조인용
    private String        productName;
    private String        statusLabel;    // 한글 상태명 (뷰 편의용)

    public Long          getOrderId()        { return orderId; }
    public String        getOrderNo()        { return orderNo; }
    public String        getCustomerName()   { return customerName; }
    public Long          getProductId()      { return productId; }
    public int           getQuantity()       { return quantity; }
    public BigDecimal    getUnitPrice()      { return unitPrice; }
    public String        getStatus()         { return status; }
    public Long          getWorkOrderId()    { return workOrderId; }
    public Integer       getPurchaseOrderId(){ return purchaseOrderId; }
    public Long          getShipmentId()     { return shipmentId; }
    public String        getNotes()          { return notes; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public String        getProductName()    { return productName; }
    public String        getCreatedAtStr() {
        return createdAt != null ? createdAt.toString().substring(0, 10) : "";
    }
    public String        getUpdatedAtStr() {
        return updatedAt != null ? updatedAt.toString().substring(0, 10) : "";
    }
    public String        getStatusLabel() {
        return status != null ? status : "";
    }

    public void setOrderId(Long orderId)               { this.orderId = orderId; }
    public void setOrderNo(String orderNo)             { this.orderNo = orderNo; }
    public void setCustomerName(String customerName)   { this.customerName = customerName; }
    public void setProductId(Long productId)           { this.productId = productId; }
    public void setQuantity(int quantity)              { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice)     { this.unitPrice = unitPrice; }
    public void setStatus(String status)               { this.status = status; }
    public void setWorkOrderId(Long workOrderId)       { this.workOrderId = workOrderId; }
    public void setPurchaseOrderId(Integer purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public void setShipmentId(Long shipmentId)         { this.shipmentId = shipmentId; }
    public void setNotes(String notes)                 { this.notes = notes; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)  { this.updatedAt = updatedAt; }
    public void setProductName(String productName)     { this.productName = productName; }
    public void setStatusLabel(String statusLabel)     { this.statusLabel = statusLabel; }
}
