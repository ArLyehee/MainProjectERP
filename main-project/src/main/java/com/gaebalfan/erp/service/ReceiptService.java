package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.Receipt;
import com.gaebalfan.erp.mapper.ReceiptMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReceiptService {

    private final ReceiptMapper mapper;

    public ReceiptService(ReceiptMapper mapper) {
        this.mapper = mapper;
    }

    public List<Receipt> findAll() {
        return mapper.findAll();
    }

    

    public void insert(Receipt obj) {
        mapper.insert(obj);
    }
}
