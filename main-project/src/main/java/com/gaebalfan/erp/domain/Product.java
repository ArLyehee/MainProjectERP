package com.gaebalfan.erp.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long          productId;
    private String        productName;
    private String        model;
    private BigDecimal    price;
    private String        description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long          getProductId()   { return productId; }
    public String        getProductName() { return productName; }
    public String        getModel()       { return model; }
    public BigDecimal    getPrice()       { return price; }
    public String        getDescription() { return description; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public LocalDateTime getUpdatedAt()   { return updatedAt; }

    public void setProductId(Long productId)              { this.productId = productId; }
    public void setProductName(String productName)        { this.productName = productName; }
    public void setModel(String model)                    { this.model = model; }
    public void setPrice(BigDecimal price)                { this.price = price; }
    public void setDescription(String description)        { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)     { this.updatedAt = updatedAt; }
}
