package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.ProductionReceipt;
import com.gaebalfan.erp.mapper.ProductionReceiptMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductionReceiptService {

    private final ProductionReceiptMapper mapper;

    public ProductionReceiptService(ProductionReceiptMapper mapper) {
        this.mapper = mapper;
    }

    public List<ProductionReceipt> findAll() {
        return mapper.findAll();
    }

    public ProductionReceipt findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(ProductionReceipt obj) {
        mapper.insert(obj);
    }
}
