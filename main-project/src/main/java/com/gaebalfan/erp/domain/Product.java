package com.gaebalfan.erp.domain;

import java.math.BigDecimal;

public class Product {
    private Long       productId;
    private String     productName;
    private String     model;
    private BigDecimal salePrice;
    private BigDecimal costPrice;
    private String     description;

    public Long       getProductId()   { return productId; }
    public String     getProductName() { return productName; }
    public String     getModel()       { return model; }
    public BigDecimal getSalePrice()   { return salePrice; }
    public BigDecimal getCostPrice()   { return costPrice; }
    public String     getDescription() { return description; }

    public void setProductId(Long productId)           { this.productId = productId; }
    public void setProductName(String productName)     { this.productName = productName; }
    public void setModel(String model)                 { this.model = model; }
    public void setSalePrice(BigDecimal salePrice)     { this.salePrice = salePrice; }
    public void setCostPrice(BigDecimal costPrice)     { this.costPrice = costPrice; }
    public void setDescription(String description)     { this.description = description; }
}
