package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.OperatingExpense;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
@Mapper
public interface OperatingExpenseMapper {
    List<OperatingExpense> findAll();
    List<OperatingExpense> findAllPaged(@Param("offset") int offset, @Param("size") int size, @Param("q") String q);
    int count(@Param("q") String q);
    void insert(OperatingExpense expense);
    List<Map<String, Object>> findMonthlySummary(@Param("year") int year);
    Map<String, Object> findYearlySummary(@Param("year") int year);
    List<Map<String, Object>> findSummaryByType(@Param("year") int year);
}
