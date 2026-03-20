package com.gaebalfan.erp.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {
    // 통계 카드
    int countProducts();
    int countActiveEmployees();
    int countPendingOrders();      // purchase_orders PENDING+APPROVED
    int countLowInventory(int threshold);
    int countMonthlyWorkOrders();  // work_orders COMPLETED this month

    // 이번달 매출
    Map<String, Object> getMonthlyRevenue();

    // 최근 발주 목록 (5개)
    List<Map<String, Object>> getRecentOrders();

    // 재고 부족 목록 (5개)
    List<Map<String, Object>> getLowInventoryItems(int threshold);

    // 작업지시 상태별 카운트
    List<Map<String, Object>> getWorkOrderStatusCount();
}
