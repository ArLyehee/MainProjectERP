package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Sale {
    private Long       saleId;
    private Long       productId;
    private int        quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal costPrice;
    private LocalDate  saleDate;
    private String     productName;

    public Long       getSaleId()       { return saleId; }
    public Long       getProductId()    { return productId; }
    public int        getQuantity()     { return quantity; }
    public BigDecimal getUnitPrice()    { return unitPrice; }
    public BigDecimal getTotalPrice()   { return totalPrice; }
    public BigDecimal getCostPrice()    { return costPrice; }
    public LocalDate  getSaleDate()     { return saleDate; }
    public String     getProductName()  { return productName; }

    public void setSaleId(Long saleId)               { this.saleId = saleId; }
    public void setProductId(Long productId)         { this.productId = productId; }
    public void setQuantity(int quantity)             { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice)   { this.unitPrice = unitPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setCostPrice(BigDecimal costPrice)   { this.costPrice = costPrice; }
    public void setSaleDate(LocalDate saleDate)      { this.saleDate = saleDate; }
    public void setProductName(String productName)   { this.productName = productName; }
}
