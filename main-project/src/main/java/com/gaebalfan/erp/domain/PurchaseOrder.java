package com.gaebalfan.erp.domain;

import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrder {
    private Integer       poId;        // purchase_orders.id (INT PK)
    private String        poCode;      // purchase_orders.po_id (VARCHAR)
    private Long          supplierId;
    private LocalDateTime orderDate;
    private String        status;   // PENDING, APPROVED, COMPLETED, CANCELLED
    private Integer       item;     // 품목 수량

    // 조인용
    private String        supplierName;
    private String        customerName;

    private Integer       totalQuantity; // 실제 주문 수량 합계

    // 발주 항목 (purchase_order_items)
    private List<PurchaseOrderItem> items;

    public Integer       getPoId()         { return poId; }
    public String        getPoCode()       { return poCode; }
    public Long          getSupplierId()   { return supplierId; }
    public LocalDateTime getOrderDate()    { return orderDate; }
    public String        getStatus()       { return status; }
    public Integer       getItem()         { return item; }
    public String        getSupplierName() { return supplierName; }
    public String        getCustomerName() { return customerName; }
    public Integer       getTotalQuantity() { return totalQuantity; }

    public void setPoId(Integer poId)                     { this.poId = poId; }
    public void setPoCode(String poCode)                  { this.poCode = poCode; }
    public void setSupplierId(Long supplierId)            { this.supplierId = supplierId; }
    public void setOrderDate(LocalDateTime orderDate)     { this.orderDate = orderDate; }
    public void setStatus(String status)                  { this.status = status; }
    public void setItem(Integer item)                     { this.item = item; }
    public void setSupplierName(String supplierName)      { this.supplierName = supplierName; }
    public void setCustomerName(String customerName)      { this.customerName = customerName; }
    public void setTotalQuantity(Integer totalQuantity)    { this.totalQuantity = totalQuantity; }

    public String getOrderDateStr() {
        return orderDate != null ? orderDate.toString().substring(0, 10) : "";
    }

    public List<PurchaseOrderItem> getItems() { return items; }
    public void setItems(List<PurchaseOrderItem> items) { this.items = items; }
}
