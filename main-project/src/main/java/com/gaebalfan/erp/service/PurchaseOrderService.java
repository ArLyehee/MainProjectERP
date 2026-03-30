package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Product;
import com.gaebalfan.erp.domain.PurchaseOrder;
import com.gaebalfan.erp.domain.PurchaseOrderItem;
import com.gaebalfan.erp.mapper.PurchaseOrderMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderMapper mapper;

    public PurchaseOrderService(PurchaseOrderMapper mapper) {
        this.mapper = mapper;
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

    public void updateStatus(Long id, String status) {
        mapper.updateStatus(id, status);
    }

    // 작업2: 거래처별 부품 조회
    public List<Product> findProductsBySupplier(Long supplierId) {
        return mapper.findProductsBySupplier(supplierId);
    }
}
