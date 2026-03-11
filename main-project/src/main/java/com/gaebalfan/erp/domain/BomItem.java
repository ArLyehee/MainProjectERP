package com.gaebalfan.erp.domain;

public class BomItem {
    private Long   bomItemId;
    private Long   bomId;
    private String componentName;
    private int    quantity;

    public Long   getBomItemId()      { return bomItemId; }
    public Long   getBomId()          { return bomId; }
    public String getComponentName()  { return componentName; }
    public int    getQuantity()       { return quantity; }

    public void setBomItemId(Long bomItemId)              { this.bomItemId = bomItemId; }
    public void setBomId(Long bomId)                      { this.bomId = bomId; }
    public void setComponentName(String componentName)    { this.componentName = componentName; }
    public void setQuantity(int quantity)                 { this.quantity = quantity; }
}
