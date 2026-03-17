package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Bom;
import com.gaebalfan.erp.domain.BomItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface BomMapper {
    List<Bom> findAll();
    Bom findById(@Param("bomId") Long bomId);
    Bom findByProductId(@Param("productId") Long productId);
    List<BomItem> findItemsByBomId(@Param("bomId") Long bomId);
    void insertBom(Bom bom);
    void insertBomItem(BomItem item);
    void deleteBomItem(@Param("bomItemId") Long bomItemId);
}