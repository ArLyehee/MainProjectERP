package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Receipt {
    private Long          receiptId;
    private Long          poId;
    private Long          productId;
    private int           quantity;
    private LocalDateTime receiptDate;

    // 조인용
    private String        productName;

    public Long          getReceiptId()   { return receiptId; }
    public Long          getPoId()        { return poId; }
    public Long          getProductId()   { return productId; }
    public int           getQuantity()    { return quantity; }
    public LocalDateTime getReceiptDate() { return receiptDate; }
    public String        getProductName() { return productName; }

    public void setReceiptId(Long receiptId)                  { this.receiptId = receiptId; }
    public void setPoId(Long poId)                            { this.poId = poId; }
    public void setProductId(Long productId)                  { this.productId = productId; }
    public void setQuantity(int quantity)                     { this.quantity = quantity; }
    public void setReceiptDate(LocalDateTime receiptDate)     { this.receiptDate = receiptDate; }
    public void setProductName(String productName)            { this.productName = productName; }
}
