package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface InventoryMapper {
    List<Inventory> findAll();
    List<Inventory> findAllPaged(@Param("offset") int offset, @Param("size") int size, @Param("q") String q);
    int count(@Param("q") String q);
    Inventory findById(@Param("inventoryId") Long inventoryId);
    Inventory findByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);
    void insert(Inventory inventory);
    void update(Inventory inventory);
    void delete(@Param("inventoryId") Long inventoryId);
    void updateQuantity(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId, @Param("quantity") int quantity);
    int findTotalQuantityByProduct(@Param("productId") Long productId);
    List<Inventory> findByProduct(@Param("productId") Long productId);
    List<Inventory> findByWarehouse(@Param("warehouseId") Long warehouseId);
}