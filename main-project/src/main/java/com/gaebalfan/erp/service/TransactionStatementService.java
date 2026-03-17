package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.TransactionStatement;
import com.gaebalfan.erp.domain.TransactionStatementItem;
import com.gaebalfan.erp.mapper.TransactionStatementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionStatementService {

    private final TransactionStatementMapper mapper;

    public TransactionStatementService(TransactionStatementMapper mapper) {
        this.mapper = mapper;
    }

    public List<TransactionStatement> findAll() {
        return mapper.findAll();
    }

    public TransactionStatement findById(Long id) {
        TransactionStatement s = mapper.findById(id);
        if (s != null) {
            s.setItems(mapper.findItemsByStatementId(id));
        }
        return s;
    }

    @Transactional
    public TransactionStatement insert(TransactionStatement statement, List<TransactionStatementItem> items) {
        // 자동 채번: TS-YYYYMM-NNN
        String prefix = "TS-" + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        String maxNo = mapper.findMaxStatementNo(prefix);
        int seq = 1;
        if (maxNo != null) {
            try { seq = Integer.parseInt(maxNo.substring(prefix.length())) + 1; } catch (Exception ignored) {}
        }
        statement.setStatementNo(prefix + String.format("%03d", seq));
        mapper.insert(statement);
        for (TransactionStatementItem item : items) {
            item.setStatementId(statement.getStatementId());
            mapper.insertItem(item);
        }
        return statement;
    }

    public void delete(Long id) {
        mapper.delete(id);
    }
}
