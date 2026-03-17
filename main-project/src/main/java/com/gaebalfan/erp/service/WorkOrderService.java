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

import java.util.List;

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

    public void insert(WorkOrder obj) {
        mapper.insert(obj);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);

        if ("COMPLETED".equals(status)) {
            WorkOrder wo = mapper.findById(id);
            if (wo == null) return;

            // BOM 조회
            Bom bom = bomMapper.findByProductId(wo.getProductId());
            if (bom != null) {
                List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
                for (BomItem item : items) {
                    // 자재 차감: 작업지시 수량 × BOM 소요량
                    int deductQty = item.getQuantity().multiply(
                            java.math.BigDecimal.valueOf(wo.getQuantity())
                    ).intValue();
                    inventoryMapper.updateQuantity(
                            item.getComponentProductId(),
                            DEFAULT_WAREHOUSE_ID,
                            -deductQty
                    );
                }
            }

            // 완제품 재고 증가
            Inventory finished = new Inventory();
            finished.setProductId(wo.getProductId());
            finished.setWarehouseId(DEFAULT_WAREHOUSE_ID);
            finished.setQuantity(wo.getQuantity());
            inventoryMapper.insert(finished);
        }
    }
}
