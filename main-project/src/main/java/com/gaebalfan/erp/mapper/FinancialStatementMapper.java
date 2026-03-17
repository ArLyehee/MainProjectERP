package com.gaebalfan.erp.mapper;

import com.gaebalfan.erp.domain.FinancialStatement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FinancialStatementMapper {
    List<FinancialStatement> findAll();
    List<FinancialStatement> findByYear(@Param("year") String year);
    void upsert(FinancialStatement fs);
    void deleteByMonth(@Param("month") String month);
}
