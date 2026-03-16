package com.gaebalfan.erp.mapper;

import com.gaebalfan.erp.domain.TransactionStatement;
import com.gaebalfan.erp.domain.TransactionStatementItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TransactionStatementMapper {
    List<TransactionStatement> findAll();
    TransactionStatement findById(@Param("statementId") Long statementId);
    List<TransactionStatementItem> findItemsByStatementId(@Param("statementId") Long statementId);
    void insert(TransactionStatement statement);
    void insertItem(TransactionStatementItem item);
    void delete(@Param("statementId") Long statementId);
    String findMaxStatementNo(@Param("prefix") String prefix);
}
