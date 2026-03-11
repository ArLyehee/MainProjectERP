package com.gaebalfan.erp.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Shipment {
    private Long          shipmentId;
    private Long          productId;
    private Long          warehouseId;
    private int           quantity;
    private LocalDate     shipmentDate;
    private String        destination;
    private LocalDateTime createdAt;

 // 조인용 필드 추가
    private String productName;
    private String warehouseName;

    public String getProductName()  { return productName; }
    public String getWarehouseName() { return warehouseName; }
    public void setProductName(String productName)    { this.productName = productName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
}
