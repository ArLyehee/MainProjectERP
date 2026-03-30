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
    private static final int  LOW_STOCK_THRESHOLD  = 50;

    private final OrderMapper       orderMapper;
    private final InventoryMapper   inventoryMapper;
    private final WorkOrderMapper   workOrderMapper;
    private final BomMapper         bomMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final ShipmentMapper    shipmentMapper;
    private final SaleMapper        saleMapper;
    private final ProductMapper     productMapper;

    public OrderService(OrderMapper orderMapper,
                        InventoryMapper inventoryMapper,
                        WorkOrderMapper workOrderMapper,
                        BomMapper bomMapper,
                        PurchaseOrderMapper purchaseOrderMapper,
                        ShipmentMapper shipmentMapper,
                        SaleMapper saleMapper,
                        ProductMapper productMapper) {
        this.orderMapper          = orderMapper;
        this.inventoryMapper      = inventoryMapper;
        this.workOrderMapper      = workOrderMapper;
        this.bomMapper            = bomMapper;
        this.purchaseOrderMapper  = purchaseOrderMapper;
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
        orderMapper.updateStatus(orderId, "HOLD");
    }

    @Transactional
    public void reopen(Long orderId) {
        orderMapper.updateStatus(orderId, "PENDING");
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
            // ② 재고 충분 → 즉시 출고 + 매출 자동 등록
            Long shipmentId = doShipAndSale(order);
            orderMapper.updateAfterApprove(orderId, "SHIPPED", null, null, shipmentId);
            return "SHIPPED";
        }

        // ③ 재고 부족 → 부품 재고 확인
        Bom bom = bomMapper.findByProductId(order.getProductId());
        boolean partsOk = checkPartsStock(bom, order.getQuantity());

        if (partsOk) {
            // ④ 부품 있음 → 작업지시 생성
            Long workOrderId = createWorkOrder(order);
            orderMapper.updateAfterApprove(orderId, "IN_PRODUCTION", workOrderId, null, null);
            return "IN_PRODUCTION";
        } else {
            // ⑤ 부품 부족 → 발주 처리
            Integer poId = createPurchaseOrder(order);
            orderMapper.updateAfterApprove(orderId, "ORDERED", null, poId, null);
            return "ORDERED";
        }
    }

    /**
     * 출고 처리 (READY 상태 주문 수동 출고)
     */
    @Transactional
    public void ship(Long orderId) {
        CustomerOrder order = orderMapper.findById(orderId);
        if (order == null) throw new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId);
        Long shipmentId = doShipAndSale(order);
        orderMapper.updateAfterApprove(orderId, "SHIPPED", order.getWorkOrderId(), order.getPurchaseOrderId(), shipmentId);
    }

    // ─── 내부 헬퍼 ──────────────────────────────────────────────────────────

    private Long doShipAndSale(CustomerOrder order) {
        // 출고 등록
        Shipment shipment = new Shipment();
        shipment.setProductId(order.getProductId());
        shipment.setWarehouseId(DEFAULT_WAREHOUSE_ID);
        shipment.setQuantity(order.getQuantity());
        shipment.setShipmentDate(LocalDateTime.now());
        shipment.setDestination(order.getCustomerName());
        shipmentMapper.insert(shipment);

        // 재고 차감
        inventoryMapper.updateQuantity(order.getProductId(), DEFAULT_WAREHOUSE_ID, -order.getQuantity());

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

    private boolean checkPartsStock(Bom bom, int qty) {
        if (bom == null) return true; // BOM 없으면 그냥 작업지시
        List<BomItem> items = bomMapper.findItemsByBomId(bom.getBomId());
        for (BomItem item : items) {
            int needed = item.getQuantity().intValue() * qty;
            int have   = inventoryMapper.findTotalQuantityByProduct(item.getComponentProductId());
            if (have < needed) return false;
        }
        return true;
    }

    private Long createWorkOrder(CustomerOrder order) {
        WorkOrder wo = new WorkOrder();
        wo.setProductId(order.getProductId());
        wo.setQuantity(order.getQuantity());
        wo.setStartDate(LocalDateTime.now());
        wo.setStatus("PENDING");
        workOrderMapper.insert(wo);
        return wo.getWorkOrderId();
    }

    private Integer createPurchaseOrder(CustomerOrder order) {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoCode("PO-ORD-" + order.getOrderNo());
        po.setSupplierId(null); // 담당자가 나중에 지정
        po.setOrderDate(LocalDateTime.now());
        po.setStatus("PENDING");
        po.setItem(order.getQuantity());
        purchaseOrderMapper.insert(po);
        return po.getPoId();
    }
}
