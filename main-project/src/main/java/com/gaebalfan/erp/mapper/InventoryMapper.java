package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface InventoryMapper {
    List<Inventory> findAll();
    Inventory findById(@Param("inventoryId") Long inventoryId);
    void insert(Inventory inventory);
    void updateQuantity(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("quantity") int quantity);
}