package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;

public class Supplier {
    private Long          supplierId;
    private String        supplierName;
    private String        phone;
    private String        address;
    private LocalDateTime createdAt;

    public Long          getSupplierId()   { return supplierId; }
    public String        getSupplierName() { return supplierName; }
    public String        getPhone()        { return phone; }
    public String        getAddress()      { return address; }
    public LocalDateTime getCreatedAt()    { return createdAt; }

    public void setSupplierId(Long supplierId)            { this.supplierId = supplierId; }
    public void setSupplierName(String supplierName)      { this.supplierName = supplierName; }
    public void setPhone(String phone)                    { this.phone = phone; }
    public void setAddress(String address)                { this.address = address; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
}
