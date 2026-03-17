package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.mapper.DashboardMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardMapper mapper;

    public DashboardController(DashboardMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("productCount",   mapper.countProducts());
        stats.put("employeeCount",  mapper.countActiveEmployees());
        stats.put("pendingOrders",  mapper.countPendingOrders());
        stats.put("lowInventory",   mapper.countLowInventory());
        stats.put("monthlyWO",      mapper.countMonthlyWorkOrders());

        Map<String, Object> rev = mapper.getMonthlyRevenue();
        Object revenueVal = rev != null ? rev.get("revenue") : 0;
        Object saleCount  = rev != null ? rev.get("sale_count") : 0;
        stats.put("monthlyRevenue", revenueVal != null ? revenueVal : 0);
        stats.put("monthlySaleCount", saleCount != null ? saleCount : 0);

        stats.put("recentOrders",      mapper.getRecentOrders());
        stats.put("lowInventoryItems", mapper.getLowInventoryItems());
        stats.put("workOrderStatus",   mapper.getWorkOrderStatusCount());

        return ResponseEntity.ok(stats);
    }
}
