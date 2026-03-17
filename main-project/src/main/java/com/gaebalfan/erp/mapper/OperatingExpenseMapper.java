package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.OperatingExpense;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
@Mapper
public interface OperatingExpenseMapper {
    List<OperatingExpense> findAll();
    void insert(OperatingExpense expense);
    List<Map<String, Object>> findMonthlySummary(@Param("year") int year);
    Map<String, Object> findYearlySummary(@Param("year") int year);
    List<Map<String, Object>> findSummaryByType(@Param("year") int year);
}
