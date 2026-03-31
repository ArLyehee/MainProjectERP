package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Supplier;
import com.gaebalfan.erp.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
@Mapper
public interface SupplierMapper {
    List<Supplier> findAll();
    List<Supplier> findAllPaged(@Param("offset") int offset, @Param("size") int size, @Param("q") String q);
    int count(@Param("q") String q);
    Supplier findById(@Param("supplierId") Long supplierId);
    void insert(Supplier supplier);
    void update(Supplier supplier);
    void delete(@Param("supplierId") Long supplierId);

    // 취급 품목
    List<Map<String, Object>> findProductsBySupplier(@Param("supplierId") Long supplierId);
    void addProduct(@Param("supplierId") Long supplierId, @Param("productId") Long productId);
    void removeProduct(@Param("supplierId") Long supplierId, @Param("productId") Long productId);
    void setPrimary(@Param("supplierId") Long supplierId, @Param("productId") Long productId);
    void clearPrimary(@Param("supplierId") Long supplierId, @Param("productId") Long productId);
}