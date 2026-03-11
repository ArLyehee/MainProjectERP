package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Receipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface ReceiptMapper {
    List<Receipt> findAll();
    List<Receipt> findByPoId(@Param("poId") Long poId);
    void insert(Receipt receipt);
}