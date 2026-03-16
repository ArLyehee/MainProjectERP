package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Shipment {
    private Long          shipmentId;
    private Long          productId;
    private Long          warehouseId;
    private int           quantity;
    private LocalDateTime shipmentDate;
    private String        destination;
    private String        productName;
    private String        warehouseName;

    public Long          getShipmentId()    { return shipmentId; }
    public Long          getProductId()     { return productId; }
    public Long          getWarehouseId()   { return warehouseId; }
    public int           getQuantity()      { return quantity; }
    public LocalDateTime getShipmentDate()  { return shipmentDate; }
    public String        getDestination()   { return destination; }
    public String        getProductName()   { return productName; }
    public String        getWarehouseName() { return warehouseName; }

    public void setShipmentId(Long shipmentId)              { this.shipmentId = shipmentId; }
    public void setProductId(Long productId)                { this.productId = productId; }
    public void setWarehouseId(Long warehouseId)            { this.warehouseId = warehouseId; }
    public void setQuantity(int quantity)                   { this.quantity = quantity; }
    public void setShipmentDate(LocalDateTime shipmentDate) { this.shipmentDate = shipmentDate; }
    public void setDestination(String destination)          { this.destination = destination; }
    public void setProductName(String productName)          { this.productName = productName; }
    public void setWarehouseName(String warehouseName)      { this.warehouseName = warehouseName; }
}
