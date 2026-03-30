package com.gaebalfan.erp.domain;

import java.math.BigDecimal;

public class PurchaseOrderItem {
    private Long       poItemId;
    private String     poId;       // purchase_orders.po_id (VARCHAR)
    private Long       productId;
    private int        quantity;
    private BigDecimal unitPrice;

    // 조인용
    private String     productName;

    public Long       getPoItemId()    { return poItemId; }
    public String     getPoId()        { return poId; }
    public Long       getProductId()   { return productId; }
    public int        getQuantity()    { return quantity; }
    public BigDecimal getUnitPrice()   { return unitPrice; }
    public String     getProductName() { return productName; }

    public void setPoItemId(Long poItemId)           { this.poItemId = poItemId; }
    public void setPoId(String poId)                 { this.poId = poId; }
    public void setProductId(Long productId)         { this.productId = productId; }
    public void setQuantity(int quantity)            { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice)   { this.unitPrice = unitPrice; }
    public void setProductName(String productName)   { this.productName = productName; }
}
