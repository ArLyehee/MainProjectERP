package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Inventory {
    private Long          inventoryId;
    private Long          productId;
    private Long          warehouseId;
    private int           quantity;
    private LocalDateTime lastUpdate;
    private LocalDateTime createdAt;

    // 조인용 (Mapper에서 활용)
    private String        productName;
    private String        warehouseName;

    public Long          getInventoryId()   { return inventoryId; }
    public Long          getProductId()     { return productId; }
    public Long          getWarehouseId()   { return warehouseId; }
    public int           getQuantity()      { return quantity; }
    public LocalDateTime getLastUpdate()    { return lastUpdate; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public String        getProductName()   { return productName; }
    public String        getWarehouseName() { return warehouseName; }
    public String        getCreatedAtStr()  {
        return createdAt != null ? createdAt.toString().substring(0, 10) : "";
    }

    public void setInventoryId(Long inventoryId)          { this.inventoryId = inventoryId; }
    public void setProductId(Long productId)              { this.productId = productId; }
    public void setWarehouseId(Long warehouseId)          { this.warehouseId = warehouseId; }
    public void setQuantity(int quantity)                 { this.quantity = quantity; }
    public void setLastUpdate(LocalDateTime lastUpdate)   { this.lastUpdate = lastUpdate; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setProductName(String productName)        { this.productName = productName; }
    public void setWarehouseName(String warehouseName)    { this.warehouseName = warehouseName; }
}
