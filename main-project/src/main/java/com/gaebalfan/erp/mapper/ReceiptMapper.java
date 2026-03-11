package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Receipt;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ReceiptMapper {
    List<Receipt> findAll();
    void insert(Receipt receipt);
}