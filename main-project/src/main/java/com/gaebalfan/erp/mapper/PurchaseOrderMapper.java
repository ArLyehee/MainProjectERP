package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface PurchaseOrderMapper {
    List<PurchaseOrder> findAll();
    PurchaseOrder findById(@Param("poId") Long poId);
    void insert(PurchaseOrder po);
    void updateStatus(@Param("poId") Long poId, @Param("status") String status);
}