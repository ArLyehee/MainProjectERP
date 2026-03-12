package com.gaebalfan.erp.mapper;
import com.gaebalfan.erp.domain.Shipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface ShipmentMapper {
    List<Shipment> findAll();
    Shipment findById(@Param("shipmentId") Long shipmentId);
    void insert(Shipment shipment);

}
