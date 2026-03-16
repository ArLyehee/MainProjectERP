package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Shipment;
import com.gaebalfan.erp.mapper.ShipmentMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShipmentService {

    private final ShipmentMapper mapper;

    public ShipmentService(ShipmentMapper mapper) {
        this.mapper = mapper;
    }

    public List<Shipment> findAll() {
        return mapper.findAll();
    }

    public List<Shipment> findAllPaged(int page, int size) {
        return mapper.findAllPaged((page - 1) * size, size);
    }

    public int count() {
        return mapper.count();
    }

    public Shipment findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Shipment obj) {
        mapper.insert(obj);
    }
}
