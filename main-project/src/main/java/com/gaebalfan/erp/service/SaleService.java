package com.gaebalfan.erp.service;
import com.gaebalfan.erp.domain.Sale;
import com.gaebalfan.erp.mapper.SaleMapper;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class SaleService {
    private final SaleMapper mapper;
    public SaleService(SaleMapper mapper) { this.mapper = mapper; }
    public List<Sale> findAll() { return mapper.findAll(); }
    public List<Sale> findAllPaged(int page, int size) { return mapper.findAllPaged((page - 1) * size, size); }
    public int count() { return mapper.count(); }
    public void insert(Sale obj) { mapper.insert(obj); }
}
