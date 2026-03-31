package com.gaebalfan.erp.mapper;

import com.gaebalfan.erp.domain.CustomerOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<CustomerOrder> findAll();
    List<CustomerOrder> findAllPaged(@Param("offset") int offset, @Param("size") int size);
    int count();
    CustomerOrder findById(@Param("orderId") Long orderId);
    CustomerOrder findByPurchaseOrderId(@Param("purchaseOrderId") Integer purchaseOrderId);
    void insert(CustomerOrder order);
    void updateStatus(@Param("orderId") Long orderId, @Param("status") String status);
    void updateAfterApprove(@Param("orderId") Long orderId,
                            @Param("status") String status,
                            @Param("workOrderId") Long workOrderId,
                            @Param("purchaseOrderId") Integer purchaseOrderId,
                            @Param("shipmentId") Long shipmentId);
}
