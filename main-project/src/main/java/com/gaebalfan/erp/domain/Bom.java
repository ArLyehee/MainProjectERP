package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Bom {
    private Long          bomId;
    private Long          productId;
    private String        description;
    private LocalDateTime createdAt;

    // 조인용
    private String        productName;

    public Long          getBomId()       { return bomId; }
    public Long          getProductId()   { return productId; }
    public String        getDescription() { return description; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public String        getProductName() { return productName; }

    public void setBomId(Long bomId)                      { this.bomId = bomId; }
    public void setProductId(Long productId)              { this.productId = productId; }
    public void setDescription(String description)        { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setProductName(String productName)        { this.productName = productName; }
}
