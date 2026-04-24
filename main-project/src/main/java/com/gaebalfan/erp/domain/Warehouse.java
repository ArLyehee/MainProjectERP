package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Warehouse {
    private Long          warehouseId;
    private String        warehouseName;
    private LocalDateTime createdAt;

    public Long          getWarehouseId()   { return warehouseId; }
    public String        getWarehouseName() { return warehouseName; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public String        getCreatedAtStr()  {
        return createdAt != null ? createdAt.toString().substring(0, 10) : "-";
    }

    public void setWarehouseId(Long warehouseId)          { this.warehouseId = warehouseId; }
    public void setWarehouseName(String warehouseName)    { this.warehouseName = warehouseName; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
}
