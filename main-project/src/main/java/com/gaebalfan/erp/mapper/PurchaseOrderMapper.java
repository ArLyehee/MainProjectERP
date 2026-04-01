package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Product;
import com.gaebalfan.erp.domain.PurchaseOrder;
import com.gaebalfan.erp.domain.PurchaseOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface PurchaseOrderMapper {
    List<PurchaseOrder> findAll();
    List<PurchaseOrder> findAllPaged(@Param("offset") int offset, @Param("size") int size, @Param("q") String q);
    int count(@Param("q") String q);
    PurchaseOrder findById(@Param("poId") Long poId);
    PurchaseOrder findByPoCode(@Param("poCode") String poCode);
    void insert(PurchaseOrder po);
    void updateStatus(@Param("poId") Long poId, @Param("status") String status);
    void updateSupplier(@Param("poId") Long poId, @Param("supplierId") Long supplierId);
    void cancelByWorkOrderId(@Param("workOrderId") Long workOrderId);
    List<PurchaseOrder> findByWorkOrderId(@Param("workOrderId") Long workOrderId);
    void insertItem(PurchaseOrderItem item);
    void updateItemPrice(@Param("poItemId") Long poItemId, @Param("unitPrice") java.math.BigDecimal unitPrice);
    List<PurchaseOrderItem> findItemsByPoCode(@Param("poCode") String poCode);
    Long findSupplierIdByProduct(@Param("productId") Long productId);
    List<Product> findProductsBySupplier(@Param("supplierId") Long supplierId);
}