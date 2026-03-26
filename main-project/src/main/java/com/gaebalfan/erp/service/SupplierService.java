package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Supplier;
import com.gaebalfan.erp.mapper.SupplierMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierMapper mapper;

    public SupplierService(SupplierMapper mapper) {
        this.mapper = mapper;
    }

    public List<Supplier> findAll() {
        return mapper.findAll();
    }

    public List<Supplier> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }

    public int count(String q) {
        return mapper.count(q);
    }

    public Supplier findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Supplier obj) {
        mapper.insert(obj);
    }

    public void update(Supplier obj) {
        mapper.update(obj);
    }

    public void delete(Long id) {
        mapper.delete(id);
    }
}
