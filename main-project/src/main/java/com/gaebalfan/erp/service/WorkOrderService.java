package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Bom;
import com.gaebalfan.erp.domain.BomItem;
import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.domain.WorkOrder;
import com.gaebalfan.erp.mapper.BomMapper;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkOrderService {

    private final WorkOrderMapper mapper;
    private final BomMapper bomMapper;
    private final InventoryMapper inventoryMapper;

    // 생산 완료 시 입고되는 기본 창고 ID (1번 창고)
    private static final long DEFAULT_WAREHOUSE_ID = 1L;

    public WorkOrderService(WorkOrderMapper mapper, BomMapper bomMapper, InventoryMapper inventoryMapper) {
        this.mapper = mapper;
        this.bomMapper = bomMapper;
        this.inventoryMapper = inventoryMapper;
    }

    public List<WorkOrder> findAll() {
        return mapper.findAll();
    }

    public WorkOrder findById(Long id) {
        return mapper.findById(id);
    }

    /**
     * 재고 부족 체크: 제품의 BOM을 기준으로 생산 수량에 필요한 자재 재고를 확인
     */
    public List<Map<String, Object>> checkStock(Long productId, int quantity) {
        List<Map<String, Object>> result = new ArrayList<>();
        Bom bom = bomMapper.findByProductId(productId);
        if (bom == null) return result;

        List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
        for (BomItem item : items) {
            int needed = item.getQuantity().multiply(BigDecimal.valueOf(quantity)).intValue();
            int available = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("componentName", item.getComponentProductName());
            m.put("needed", needed);
            m.put("available", available);
            m.put("shortage", Math.max(0, needed - available));
            result.add(m);
        }
        return result;
    }

    /**
     * 작업지시 등록: BOM 자재를 재고에서 차감
     */
    @Transactional
    public void insert(WorkOrder obj) {
        mapper.insert(obj);

        // BOM 기반 자재 재고 차감
        Bom bom = bomMapper.findByProductId(obj.getProductId());
        if (bom != null) {
            List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
            for (BomItem item : items) {
                int deductQty = item.getQuantity().multiply(BigDecimal.valueOf(obj.getQuantity())).intValue();
                inventoryMapper.updateQuantity(item.getComponentProductId(), DEFAULT_WAREHOUSE_ID, -deductQty);
            }
        }
    }

    /**
     * 상태 변경: COMPLETED 시 완성품을 재고에 추가
     */
    @Transactional
    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);

        if ("COMPLETED".equals(status)) {
            WorkOrder wo = mapper.findById(id);
            if (wo == null) return;

            // 완성품 재고 증가
            Inventory finished = new Inventory();
            finished.setProductId(wo.getProductId());
            finished.setWarehouseId(DEFAULT_WAREHOUSE_ID);
            finished.setQuantity(wo.getQuantity());
            inventoryMapper.insert(finished);
        }
    }
}
