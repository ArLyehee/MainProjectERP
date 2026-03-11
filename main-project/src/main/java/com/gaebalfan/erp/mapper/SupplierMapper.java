package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface SupplierMapper {
    List<Supplier> findAll();
    Supplier findById(@Param("supplierId") Long supplierId);
    void insert(Supplier supplier);
    void update(Supplier supplier);
    void delete(@Param("supplierId") Long supplierId);
}