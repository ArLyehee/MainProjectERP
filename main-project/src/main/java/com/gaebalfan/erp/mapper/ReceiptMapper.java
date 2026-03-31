package com.gaebalfan.erp.mapper;

import com.gaebalfan.erp.domain.Receipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReceiptMapper {
    List<Receipt> findAll();
    List<Receipt> findAllPaged(@Param("offset") int offset, @Param("size") int size);
    int count();
    List<Receipt> findByPoId(@Param("poId") String poId);
    void insert(Receipt receipt);
}