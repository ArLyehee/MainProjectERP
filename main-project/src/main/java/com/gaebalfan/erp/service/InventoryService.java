package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Inventory;
import com.gaebalfan.erp.mapper.InventoryMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryMapper mapper;

    public InventoryService(InventoryMapper mapper) {
        this.mapper = mapper;
    }

    public List<Inventory> findAll() {
        return mapper.findAll();
    }

    public List<Inventory> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }

    public int count(String q) {
        return mapper.count(q);
    }

    public List<Inventory> findByProduct(Long productId) {
        return mapper.findByProduct(productId);
    }

    public Inventory findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Inventory obj) {
        mapper.insert(obj);
    }

    public void update(Inventory obj) {
        mapper.update(obj);
    }

    public void delete(Long id) {
        mapper.delete(id);
    }
}
