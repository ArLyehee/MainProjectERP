package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Warehouse;
import com.gaebalfan.erp.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseMapper mapper;

    public WarehouseService(WarehouseMapper mapper) {
        this.mapper = mapper;
    }

    public List<Warehouse> findAll() {
        return mapper.findAll();
    }

    public Warehouse findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Warehouse obj) {
        mapper.insert(obj);
    }
}
