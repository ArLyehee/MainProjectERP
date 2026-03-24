package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Product;
import com.gaebalfan.erp.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductMapper mapper;

    public ProductService(ProductMapper mapper) {
        this.mapper = mapper;
    }

    public List<Product> findAll() {
        return mapper.findAll();
    }

    public List<Product> findAllPaged(int page, int size) {
        return mapper.findAllPaged((page - 1) * size, size);
    }

    public int count() {
        return mapper.count();
    }

    public Product findById(Long id) {
        return mapper.findById(id);
    }

    public void insert(Product obj) {
        mapper.insert(obj);
    }

    public void update(Product obj) {
        mapper.update(obj);
    }

    public void delete(Long id) {
        mapper.delete(id);
    }
}
