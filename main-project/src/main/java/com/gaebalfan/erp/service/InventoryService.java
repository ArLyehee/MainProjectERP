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

    public Inventory findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Inventory obj) {
        mapper.insert(obj);
    }
}
