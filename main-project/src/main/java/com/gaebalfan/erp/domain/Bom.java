package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Bom {
    private Long          bomId;
    private Long          productId;
    private String        bomName;
    private String        version;
    private LocalDateTime createdAt;
    private String        productName;

    public Long          getBomId()       { return bomId; }
    public Long          getProductId()   { return productId; }
    public String        getBomName()     { return bomName; }
    public String        getVersion()     { return version; }
    public LocalDateTime getCreatedAt()   { return createdAt; }
    public String        getProductName() { return productName; }

    public void setBomId(Long bomId)                      { this.bomId = bomId; }
    public void setProductId(Long productId)              { this.productId = productId; }
    public void setBomName(String bomName)                { this.bomName = bomName; }
    public void setVersion(String version)                { this.version = version; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setProductName(String productName)        { this.productName = productName; }
}
