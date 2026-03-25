package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface PurchaseOrderMapper {
    List<PurchaseOrder> findAll();
    List<PurchaseOrder> findAllPaged(@Param("offset") int offset, @Param("size") int size, @Param("q") String q);
    int count(@Param("q") String q);
    PurchaseOrder findById(@Param("poId") Long poId);
    void insert(PurchaseOrder po);
    void updateStatus(@Param("poId") Long poId, @Param("status") String status);
}