package com.gaebalfan.erp.config;

import org.springframework.stereotype.Component;

@Component
public class SystemSettings {
    private int lowStockThreshold = 10;

    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int v) { this.lowStockThreshold = v; }
}
