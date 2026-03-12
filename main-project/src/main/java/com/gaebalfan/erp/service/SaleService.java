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
    public void insert(Sale obj) { mapper.insert(obj); }
}
