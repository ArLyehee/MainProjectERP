package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class ProductionReceipt {
    private Long          productionId;
    private Long          workOrderId;
    private Long          productId;
    private int           quantity;
    private LocalDateTime receiptDate;

    // 조인용
    private String        productName;

    public Long          getProductionId() { return productionId; }
    public Long          getWorkOrderId()  { return workOrderId; }
    public Long          getProductId()    { return productId; }
    public int           getQuantity()     { return quantity; }
    public LocalDateTime getReceiptDate()  { return receiptDate; }
    public String        getProductName()  { return productName; }

    public void setProductionId(Long productionId)            { this.productionId = productionId; }
    public void setWorkOrderId(Long workOrderId)              { this.workOrderId = workOrderId; }
    public void setProductId(Long productId)                  { this.productId = productId; }
    public void setQuantity(int quantity)                     { this.quantity = quantity; }
    public void setReceiptDate(LocalDateTime receiptDate)     { this.receiptDate = receiptDate; }
    public void setProductName(String productName)            { this.productName = productName; }
}
