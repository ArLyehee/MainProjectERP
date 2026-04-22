package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.*;
import com.gaebalfan.erp.mapper.BomMapper;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.OrderMapper;
import com.gaebalfan.erp.mapper.ProductMapper;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import com.gaebalfan.erp.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkOrderService {

    private final WorkOrderMapper     mapper;
    private final BomMapper           bomMapper;
    private final InventoryMapper     inventoryMapper;
    private final OrderMapper         orderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final ProductMapper       productMapper;

    private static final long DEFAULT_WAREHOUSE_ID = 1L;

    public WorkOrderService(WorkOrderMapper mapper, BomMapper bomMapper,
                            InventoryMapper inventoryMapper, OrderMapper orderMapper,
                            PurchaseOrderMapper purchaseOrderMapper, ProductMapper productMapper) {
        this.mapper               = mapper;
        this.bomMapper            = bomMapper;
        this.inventoryMapper      = inventoryMapper;
        this.orderMapper          = orderMapper;
        this.purchaseOrderMapper  = purchaseOrderMapper;
        this.productMapper        = productMapper;
    }

    public List<WorkOrder> findAll() {
        return mapper.findAll();
    }

    public List<WorkOrder> findAllPaged(int page, int size) {
        return mapper.findAllPaged((page - 1) * size, size);
    }

    public int count() {
        return mapper.count();
    }

    public WorkOrder findById(Long id) {
        return mapper.findById(id);
    }

    /**
     * 부족 부품 자동 발주: 작업지시 기준으로 부족한 부품마다 PO 생성
     */
    @Transactional
    public int autoOrderShortages(Long workOrderId, String managerName) {
        WorkOrder wo = mapper.findById(workOrderId);
        if (wo == null) return 0;

        Bom bom = bomMapper.findByProductId(wo.getProductId());
        if (bom == null) return 0;

        List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
        int count = 0;
        for (BomItem item : items) {
            int needed    = item.getQuantity().multiply(BigDecimal.valueOf(wo.getQuantity())).intValue();
            int available = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
            int shortage  = needed - available;
            if (shortage <= 0) continue;

            Long supplierId = purchaseOrderMapper.findSupplierIdByProduct(item.getComponentProductId());

            PurchaseOrder po = new PurchaseOrder();
            po.setPoCode("PO-PARTS-" + workOrderId + "-" + item.getComponentProductId() + "-" + System.currentTimeMillis() % 10000);
            po.setSupplierId(supplierId);
            po.setOrderDate(LocalDateTime.now());
            po.setStatus("PENDING");
            po.setItem(1);
            po.setCustomerName(managerName);
            purchaseOrderMapper.insert(po);

            PurchaseOrderItem poItem = new PurchaseOrderItem();
            Product product = productMapper.findById(item.getComponentProductId());
            BigDecimal costPrice = (product != null && product.getCostPrice() != null)
                    ? product.getCostPrice() : BigDecimal.ZERO;

            poItem.setPoId(po.getPoCode());
            poItem.setProductId(item.getComponentProductId());
            poItem.setQuantity(shortage);
            poItem.setUnitPrice(costPrice);
            purchaseOrderMapper.insertItem(poItem);
            count++;
        }
        return count;
    }

    /**
     * 작업지시 상세: 기본 정보 + 자재 현황 + 연관 발주
     */
    public Map<String, Object> getDetail(Long id) {
        WorkOrder wo = mapper.findById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("wo", wo);
        if (wo != null) {
            result.put("materials", checkStock(wo.getProductId(), wo.getQuantity()));
            result.put("orders", purchaseOrderMapper.findByWorkOrderId(id));
        }
        return result;
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
            int rawAvailable = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
            int displayAvailable = Math.max(0, rawAvailable);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("componentName", item.getComponentProductName());
            m.put("needed", needed);
            m.put("available", displayAvailable);
            m.put("shortage", Math.max(0, needed - displayAvailable));
            result.add(m);
        }
        return result;
    }

    /**
     * 작업지시 등록: BOM 자재를 재고에서 차감.
     * 차감 후 모든 자재 재고가 0 이상이면 부족 없음 → 자동으로 IN_PROGRESS로 전환.
     */
    @Transactional
    public void insert(WorkOrder obj) {
        mapper.insert(obj);

        // 자재 차감은 완료 시점에만 수행 - 등록 시 재고 충분하면 자동 진행중
        boolean hasShortage = false;
        Bom bom = bomMapper.findByProductId(obj.getProductId());
        if (bom != null) {
            List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
            for (BomItem item : items) {
                int needed = item.getQuantity().multiply(BigDecimal.valueOf(obj.getQuantity())).intValue();
                int available = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
                if (available < needed) {
                    hasShortage = true;
                    break;
                }
            }
        }

        if (!hasShortage) {
            mapper.updateStatus(obj.getWorkOrderId(), "진행중");
            obj.setStatus("진행중");
        }
    }

    /**
     * 상태 변경: COMPLETED 시 완성품을 재고에 추가
     */
    @Transactional
    public void updateStatus(Long id, String status, Long warehouseId) {
        // 진행중 전환 시: 자재 재고 충분한지 확인
        if ("진행중".equals(status)) {
            WorkOrder wo = mapper.findById(id);
            if (wo != null) {
                Bom bom = bomMapper.findByProductId(wo.getProductId());
                if (bom != null) {
                    List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
                    for (BomItem item : items) {
                        int needed = item.getQuantity().multiply(BigDecimal.valueOf(wo.getQuantity())).intValue();
                        int available = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
                        if (available < needed) {
                            throw new IllegalStateException(
                                "부품 재고가 부족합니다: " + item.getComponentProductName()
                                + " (현재: " + available + "개, 필요: " + needed + "개). 입고 처리 후 시도해주세요.");
                        }
                    }
                }
            }
        }

        // 완료 시: 자재 차감 + 완성품 재고 추가
        if ("완료".equals(status)) {
            WorkOrder wo = mapper.findById(id);
            if (wo == null) return;

            Bom bom = bomMapper.findByProductId(wo.getProductId());
            if (bom != null) {
                List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
                for (BomItem item : items) {
                    int deductQty = item.getQuantity().multiply(BigDecimal.valueOf(wo.getQuantity())).intValue();
                    int available = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
                    if (available < deductQty) {
                        throw new IllegalStateException(
                            "부품 재고가 부족합니다: " + item.getComponentProductName()
                            + " (현재: " + available + "개, 필요: " + deductQty + "개). 입고 처리 후 시도해주세요.");
                    }
                    inventoryMapper.updateQuantity(item.getComponentProductId(), DEFAULT_WAREHOUSE_ID, -deductQty);
                }
            }

            mapper.updateStatus(id, status);

            Inventory finished = new Inventory();
            finished.setProductId(wo.getProductId());
            finished.setWarehouseId(warehouseId != null ? warehouseId : DEFAULT_WAREHOUSE_ID);
            finished.setQuantity(wo.getQuantity());
            inventoryMapper.insert(finished);

            CustomerOrder order = orderMapper.findByWorkOrderId(id);
            if (order != null && "ACCEPTED".equals(order.getStatus())) {
                orderMapper.updateStatus(order.getOrderId(), "출고준비");
            }
            return;
        }

        mapper.updateStatus(id, status);

        if ("취소".equals(status)) {
            purchaseOrderMapper.cancelByWorkOrderId(id);
            CustomerOrder order = orderMapper.findByWorkOrderId(id);
            if (order != null && "ACCEPTED".equals(order.getStatus())) {
                orderMapper.updateStatus(order.getOrderId(), "보류");
            }
        }
    }
}
