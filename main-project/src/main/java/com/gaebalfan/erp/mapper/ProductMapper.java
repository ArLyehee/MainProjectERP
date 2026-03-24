package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface ProductMapper {
    List<Product> findAll();
    List<Product> findAllPaged(@Param("offset") int offset, @Param("size") int size);
    int count();
    Product findById(@Param("productId") Long productId);
    void insert(Product product);
    void update(Product product);
    void delete(@Param("productId") Long productId);
}