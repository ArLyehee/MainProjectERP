package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.*;
import com.gaebalfan.erp.mapper.OrderMapper;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import com.gaebalfan.erp.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderMapper mapper;
    private final OrderMapper         orderMapper;
    private final WorkOrderMapper     workOrderMapper;

    public PurchaseOrderService(PurchaseOrderMapper mapper,
                                OrderMapper orderMapper,
                                WorkOrderMapper workOrderMapper) {
        this.mapper          = mapper;
        this.orderMapper     = orderMapper;
        this.workOrderMapper = workOrderMapper;
    }

    public List<PurchaseOrder> findAll() {
        return mapper.findAll();
    }

    public List<PurchaseOrder> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }

    public int count(String q) {
        return mapper.count(q);
    }

    public PurchaseOrder findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(PurchaseOrder obj) {
        if (obj.getPoCode() == null || obj.getPoCode().isBlank()) {
            obj.setPoCode("PO-" + System.currentTimeMillis());
        }
        mapper.insert(obj);
        // 작업1: purchase_order_items 저장
        if (obj.getItems() != null) {
            for (PurchaseOrderItem item : obj.getItems()) {
                item.setPoId(obj.getPoCode());
                mapper.insertItem(item);
            }
        }
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);

        // 발주 승인(COMPLETED) 시 연결된 고객주문 → 작업지시 자동 생성
        if ("COMPLETED".equals(status)) {
            CustomerOrder order = orderMapper.findByPurchaseOrderId(id.intValue());
            if (order != null && "ORDERED".equals(order.getStatus())) {
                WorkOrder wo = new WorkOrder();
                wo.setProductId(order.getProductId());
                wo.setQuantity(order.getQuantity());
                wo.setStartDate(LocalDateTime.now());
                wo.setStatus("PENDING");
                workOrderMapper.insert(wo);

                orderMapper.updateAfterApprove(
                    order.getOrderId(), "IN_PRODUCTION",
                    wo.getWorkOrderId(), order.getPurchaseOrderId(), null
                );
            }
        }
    }

    public void updateSupplier(Long id, Long supplierId) {
        mapper.updateSupplier(id, supplierId);
    }

    // 작업2: 거래처별 부품 조회
    public List<Product> findProductsBySupplier(Long supplierId) {
        return mapper.findProductsBySupplier(supplierId);
    }
}
