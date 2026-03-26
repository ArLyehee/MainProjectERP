package com.gaebalfan.erp.service;
import com.gaebalfan.erp.domain.OperatingExpense;
import com.gaebalfan.erp.mapper.OperatingExpenseMapper;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class OperatingExpenseService {
    private final OperatingExpenseMapper mapper;
    public OperatingExpenseService(OperatingExpenseMapper mapper) { this.mapper = mapper; }
    public List<OperatingExpense> findAll() { return mapper.findAll(); }
    public List<OperatingExpense> findAllPaged(int page, int size, String q) {
        return mapper.findAllPaged((page - 1) * size, size, q);
    }
    public int count(String q) { return mapper.count(q); }
    public void insert(OperatingExpense obj) { mapper.insert(obj); }
}
