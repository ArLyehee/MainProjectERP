package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface WarehouseMapper {
    List<Warehouse> findAll();
    Warehouse findById(@Param("warehouseId") Long warehouseId);
    void insert(Warehouse warehouse);
    void update(Warehouse warehouse);
    void delete(@Param("warehouseId") Long warehouseId);
}