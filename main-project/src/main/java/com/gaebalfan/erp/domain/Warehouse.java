package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Warehouse {
    private Long          warehouseId;
    private String        warehouseName;
    private String        location;
    private LocalDateTime createdAt;

    public Long          getWarehouseId()   { return warehouseId; }
    public String        getWarehouseName() { return warehouseName; }
    public String        getLocation()      { return location; }
    public LocalDateTime getCreatedAt()     { return createdAt; }

    public void setWarehouseId(Long warehouseId)          { this.warehouseId = warehouseId; }
    public void setWarehouseName(String warehouseName)    { this.warehouseName = warehouseName; }
    public void setLocation(String location)              { this.location = location; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
}
