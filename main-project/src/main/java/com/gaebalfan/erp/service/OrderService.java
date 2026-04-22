package com.gaebalfan.erp.service;

import com.gaebalfan.erp.domain.*;
import com.gaebalfan.erp.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    private static final long DEFAULT_WAREHOUSE_ID = 1L;

    private final OrderMapper       orderMapper;
    private final InventoryMapper   inventoryMapper;
    private final WorkOrderService  workOrderService;
    private final ShipmentMapper    shipmentMapper;
    private final SaleMapper        saleMapper;
    private final ProductMapper     productMapper;

    public OrderService(OrderMapper orderMapper,
                        InventoryMapper inventoryMapper,
                        WorkOrderService workOrderService,
                        ShipmentMapper shipmentMapper,
                        SaleMapper saleMapper,
                        ProductMapper productMapper) {
        this.orderMapper          = orderMapper;
        this.inventoryMapper      = inventoryMapper;
        this.workOrderService     = workOrderService;
        this.shipmentMapper       = shipmentMapper;
        this.saleMapper           = saleMapper;
        this.productMapper        = productMapper;
    }

    public List<CustomerOrder> findAll() { return orderMapper.findAll(); }

    public List<CustomerOrder> findAllPaged(int page, int size) {
        return orderMapper.findAllPaged((page - 1) * size, size);
    }

    public int count() { return orderMapper.count(); }

    public CustomerOrder findById(Long id) { return orderMapper.findById(id); }

    @Transactional
    public void insert(CustomerOrder order) {
        // 주문번호 자동 생성
        if (order.getOrderNo() == null || order.getOrderNo().isBlank()) {
            String no = "ORD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-" + System.currentTimeMillis() % 10000;
            order.setOrderNo(no);
        }
        orderMapper.insert(order);
    }

    @Transactional
    public void hold(Long orderId) {
        orderMapper.updateStatus(orderId, "보류");
    }

    @Transactional
    public void reopen(Long orderId) {
        orderMapper.updateStatus(orderId, "대기");
    }

    @Transactional
    public void markReady(Long orderId) {
        orderMapper.updateStatus(orderId, "출고준비");
    }

    @Transactional
    public void cancel(Long orderId) {
        orderMapper.updateStatus(orderId, "보류");
    }

    /**
     * 주문 수락: 재고 확인 → 즉시 출고 or 작업지시 or 발주 처리
     */
    @Transactional
    public String approve(Long orderId) {
        CustomerOrder order = orderMapper.findById(orderId);
        if (order == null) throw new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId);

        // ① 완제품 재고 확인
        int stock = inventoryMapper.findTotalQuantityByProduct(order.getProductId());

        if (stock >= order.getQuantity()) {
            // ② 재고 충분 → 즉시 출고준비 (출고는 주문처리현황에서 수동 처리)
            orderMapper.updateAfterApprove(orderId, "출고준비", null, null, null);
            return "출고준비";
        }

        // ③ 재고 부족 → 작업지시 생성 (부품 재고 차감 포함)
        Long workOrderId = createWorkOrder(order);
        orderMapper.updateAfterApprove(orderId, "ACCEPTED", workOrderId, null, null);
        return "ACCEPTED";
    }

    /**
     * 출고 처리 (READY 상태 주문 수동 출고)
     */
    @Transactional
    public void ship(Long orderId, Long warehouseId) {
        CustomerOrder order = orderMapper.findById(orderId);
        if (order == null) throw new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId);
        long wh = warehouseId != null ? warehouseId : DEFAULT_WAREHOUSE_ID;
        // 선택한 창고 재고 검증
        com.gaebalfan.erp.domain.Inventory stock = inventoryMapper.findByProductAndWarehouse(order.getProductId(), wh);
        int currentQty = (stock != null) ? stock.getQuantity() : 0;
        if (currentQty < order.getQuantity()) {
            throw new IllegalStateException("선택한 창고에 재고가 부족합니다. 현재 " + currentQty + "개, 필요 " + order.getQuantity() + "개");
        }
        Long shipmentId = doShipAndSale(order, wh);
        orderMapper.updateAfterApprove(orderId, "COMPLETED", order.getWorkOrderId(), order.getPurchaseOrderId(), shipmentId);
    }

    @Transactional
    public boolean shipByWorkOrder(Long workOrderId, Long warehouseId) {
        CustomerOrder order = orderMapper.findByWorkOrderId(workOrderId);
        if (order == null || !"출고준비".equals(order.getStatus())) return false;
        long wh = warehouseId != null ? warehouseId : DEFAULT_WAREHOUSE_ID;
        Long shipmentId = doShipAndSale(order, wh);
        orderMapper.updateAfterApprove(order.getOrderId(), "COMPLETED", workOrderId, order.getPurchaseOrderId(), shipmentId);
        return true;
    }

    // ─── 내부 헬퍼 ──────────────────────────────────────────────────────────

    private Long doShipAndSale(CustomerOrder order, long warehouseId) {
        // 출고 등록
        Shipment shipment = new Shipment();
        shipment.setProductId(order.getProductId());
        shipment.setWarehouseId(warehouseId);
        shipment.setQuantity(order.getQuantity());
        shipment.setShipmentDate(LocalDateTime.now());
        shipment.setDestination(order.getCustomerName());
        shipmentMapper.insert(shipment);

        // 재고 차감
        inventoryMapper.updateQuantity(order.getProductId(), warehouseId, -order.getQuantity());

        // 매출 자동 등록
        Product product = productMapper.findById(order.getProductId());
        Sale sale = new Sale();
        sale.setProductId(order.getProductId());
        sale.setQuantity(order.getQuantity());
        BigDecimal price = (order.getUnitPrice() != null && order.getUnitPrice().compareTo(BigDecimal.ZERO) > 0)
                ? order.getUnitPrice()
                : (product != null && product.getSalePrice() != null ? product.getSalePrice() : BigDecimal.ZERO);
        sale.setUnitPrice(price);
        sale.setTotalPrice(price.multiply(BigDecimal.valueOf(order.getQuantity())));
        sale.setCostPrice(product != null && product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO);
        sale.setSaleDate(LocalDate.now());
        saleMapper.insert(sale);

        return shipment.getShipmentId();
    }

    private Long createWorkOrder(CustomerOrder order) {
        WorkOrder wo = new WorkOrder();
        wo.setProductId(order.getProductId());
        wo.setQuantity(order.getQuantity());
        wo.setStartDate(LocalDateTime.now());
        wo.setStatus("대기");
        workOrderService.insert(wo); // 재고 차감 + 부족 없으면 자동 진행중 처리

        return wo.getWorkOrderId();
    }
}
