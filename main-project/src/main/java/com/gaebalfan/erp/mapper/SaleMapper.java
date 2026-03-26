package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Sale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
@Mapper
public interface SaleMapper {
    List<Sale> findAll();
    List<Sale> findAllPaged(@Param("offset") int offset, @Param("size") int size);
    int count();
    void insert(Sale sale);
    List<Map<String, Object>> findMonthlySummary(@Param("year") int year);
    Map<String, Object> findYearlySummary(@Param("year") int year);
    void refreshFinancial(@Param("month") String month);
}
