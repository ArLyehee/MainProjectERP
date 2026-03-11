package com.gaebalfan.erp.domain;

public class Position {
    private Long   positionId;
    private String positionName;

    public Long   getPositionId()   { return positionId; }
    public String getPositionName() { return positionName; }

    public void setPositionId(Long positionId)          { this.positionId = positionId; }
    public void setPositionName(String positionName)    { this.positionName = positionName; }
}
