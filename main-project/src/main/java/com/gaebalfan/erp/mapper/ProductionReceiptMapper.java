package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.ProductionReceipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface ProductionReceiptMapper {
    List<ProductionReceipt> findAll();
    List<ProductionReceipt> findByWorkOrderId(@Param("workOrderId") Long workOrderId);
    void insert(ProductionReceipt receipt);
}