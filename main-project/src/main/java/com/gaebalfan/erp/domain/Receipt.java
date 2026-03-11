package com.gaebalfan.erp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Receipt {
    private Long          receiptId;
    private Long          poId;
    private Long          productId;
    private int           quantity;
    private LocalDate     receiptDate;
    private LocalDateTime createdAt;

    // 조인용
    private String        productName;

    public Long          getReceiptId()   { return receiptId; }
    public Long          getPoId()        { return poId; }
    public Long          getProductId()   { return productId; }
    public int           getQuantity()    { return quantity; }
    public LocalDate     getReceiptDate() { return receiptDate; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public String        getProductName() { return productName; }

    public void setReceiptId(Long receiptId)              { this.receiptId = receiptId; }
    public void setPoId(Long poId)                        { this.poId = poId; }
    public void setProductId(Long productId)              { this.productId = productId; }
    public void setQuantity(int quantity)                 { this.quantity = quantity; }
    public void setReceiptDate(LocalDate receiptDate)     { this.receiptDate = receiptDate; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setProductName(String productName)        { this.productName = productName; }
}
