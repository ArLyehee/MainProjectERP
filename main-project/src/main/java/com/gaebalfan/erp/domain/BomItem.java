package com.gaebalfan.erp.domain;

import java.math.BigDecimal;

public class BomItem {
    private Long       bomItemId;
    private Long       bomId;
    private Long       componentProductId;
    private BigDecimal quantity;
    private String     componentProductName;

    public Long       getBomItemId()           { return bomItemId; }
    public Long       getBomId()               { return bomId; }
    public Long       getComponentProductId()  { return componentProductId; }
    public BigDecimal getQuantity()            { return quantity; }
    public String     getComponentProductName() { return componentProductName; }

    public void setBomItemId(Long bomItemId)                          { this.bomItemId = bomItemId; }
    public void setBomId(Long bomId)                                  { this.bomId = bomId; }
    public void setComponentProductId(Long componentProductId)        { this.componentProductId = componentProductId; }
    public void setQuantity(BigDecimal quantity)                       { this.quantity = quantity; }
    public void setComponentProductName(String componentProductName)  { this.componentProductName = componentProductName; }
}
