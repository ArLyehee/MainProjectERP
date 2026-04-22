package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.*;
import com.gaebalfan.erp.mapper.InventoryMapper;
import com.gaebalfan.erp.mapper.OrderMapper;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import com.gaebalfan.erp.mapper.ReceiptMapper;
import com.gaebalfan.erp.mapper.TransactionStatementMapper;
import com.gaebalfan.erp.mapper.WorkOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseOrderService {

    private static final long DEFAULT_WAREHOUSE_ID = 1L;

    private final PurchaseOrderMapper        mapper;
    private final OrderMapper                orderMapper;
    private final WorkOrderMapper            workOrderMapper;
    private final ReceiptMapper              receiptMapper;
    private final TransactionStatementMapper statementMapper;
    private final InventoryMapper            inventoryMapper;

    public PurchaseOrderService(PurchaseOrderMapper mapper,
                                OrderMapper orderMapper,
                                WorkOrderMapper workOrderMapper,
                                ReceiptMapper receiptMapper,
                                TransactionStatementMapper statementMapper,
                                InventoryMapper inventoryMapper) {
        this.mapper          = mapper;
        this.orderMapper     = orderMapper;
        this.workOrderMapper = workOrderMapper;
        this.receiptMapper   = receiptMapper;
        this.statementMapper = statementMapper;
        this.inventoryMapper = inventoryMapper;
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

        // COMPLETED 시 거래명세서 담당자명 업데이트
        if ("COMPLETED".equals(status)) {
            PurchaseOrder po = mapper.findById(id);
            if (po != null && po.getCustomerName() != null && !po.getCustomerName().isBlank()) {
                statementMapper.updateManagerNameByPoCode(po.getPoCode(), po.getCustomerName());
            }
        }

        // 발주 승인(COMPLETED) 시 연결된 고객주문 → 작업지시 자동 생성
        if ("COMPLETED".equals(status)) {
            CustomerOrder order = orderMapper.findByPurchaseOrderId(id.intValue());
            if (order != null && "발주".equals(order.getStatus())) {
                WorkOrder wo = new WorkOrder();
                wo.setProductId(order.getProductId());
                wo.setQuantity(order.getQuantity());
                wo.setStartDate(LocalDateTime.now());
                wo.setStatus("대기");
                workOrderMapper.insert(wo);

                orderMapper.updateAfterApprove(
                    order.getOrderId(), "ACCEPTED",
                    wo.getWorkOrderId(), order.getPurchaseOrderId(), null
                );
            }
        }
    }

    public void updateSupplier(Long id, Long supplierId) {
        mapper.updateSupplier(id, supplierId);
    }

    public void updateItemPrice(Long poItemId, java.math.BigDecimal unitPrice) {
        mapper.updateItemPrice(poItemId, unitPrice);
    }

    public Map<String, Object> getDetailByCode(String poCode) {
        PurchaseOrder po = mapper.findByPoCode(poCode);
        Map<String, Object> result = new HashMap<>();
        result.put("po", po);
        if (po != null) {
            result.put("items",    mapper.findItemsByPoCode(po.getPoCode()));
            result.put("receipts", receiptMapper.findByPoId(po.getPoCode()));
        }
        return result;
    }

    public Map<String, Object> getDetail(Long id) {
        PurchaseOrder po = mapper.findById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("po", po);
        if (po != null) {
            result.put("items",    mapper.findItemsByPoCode(po.getPoCode()));
            result.put("receipts", receiptMapper.findByPoId(po.getPoCode()));
        }
        return result;
    }

    // 작업2: 거래처별 부품 조회
    public List<Product> findProductsBySupplier(Long supplierId) {
        return mapper.findProductsBySupplier(supplierId);
    }
}
