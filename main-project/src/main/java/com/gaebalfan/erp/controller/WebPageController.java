package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class WebPageController {

    private final InventoryService inventoryService;
    private final PurchaseOrderService purchaseOrderService;
    private final ReceiptService receiptService;
    private final ShipmentService shipmentService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final WarehouseService warehouseService;
    private final EmployeeService employeeService;
    private final WorkOrderService workOrderService;
    private final AttendanceService attendanceService;
    private final BomService bomService;

    public WebPageController(InventoryService inventoryService,
                             PurchaseOrderService purchaseOrderService,
                             ReceiptService receiptService,
                             ShipmentService shipmentService,
                             ProductService productService,
                             SupplierService supplierService,
                             WarehouseService warehouseService,
                             EmployeeService employeeService,
                             WorkOrderService workOrderService,
                             AttendanceService attendanceService,
                             BomService bomService) {
        this.inventoryService = inventoryService;
        this.purchaseOrderService = purchaseOrderService;
        this.receiptService = receiptService;
        this.shipmentService = shipmentService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.warehouseService = warehouseService;
        this.employeeService = employeeService;
        this.workOrderService = workOrderService;
        this.attendanceService = attendanceService;
        this.bomService = bomService;
    }

    // ── 로그인 페이지 ──────────────────────────────
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            Model model) {

        if (error != null)  model.addAttribute("errorMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
        if (logout != null) model.addAttribute("logoutMsg", "로그아웃 되었습니다.");
        return "auth/login";
    }

    // ── 대시보드 ───────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "dashboard";
    }

    // ── 루트 → 대시보드 리다이렉트 ──────────────────
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    // ── 재고/물류 페이지 ───────────────────────────
    @GetMapping("/inventory")
    public String inventory(Model model) {
        model.addAttribute("inventoryList", inventoryService.findAll());
        return "inventory";
    }

    @GetMapping("/purchase-orders")
    public String purchaseOrders(Model model) {
        model.addAttribute("purchaseOrderList", purchaseOrderService.findAll());
        model.addAttribute("supplierList", supplierService.findAll());
        return "purchase-orders";
    }

    @GetMapping("/receipts")
    public String receipts(Model model) {
        model.addAttribute("receiptList", receiptService.findAll());
        model.addAttribute("purchaseOrderList", purchaseOrderService.findAll());
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        return "receipts";
    }

    @GetMapping("/shipments")
    public String shipments(Model model) {
        model.addAttribute("shipmentList", shipmentService.findAll());
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        return "shipments";
    }

    // ── 기준정보 페이지 ────────────────────────────
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("productList", productService.findAll());
        return "products";
    }

    @GetMapping("/suppliers")
    public String suppliers(Model model) {
        model.addAttribute("supplierList", supplierService.findAll());
        return "suppliers";
    }

    @GetMapping("/warehouses")
    public String warehouses(Model model) {
        model.addAttribute("warehouseList", warehouseService.findAll());
        return "warehouses";
    }

    // ── 인사 페이지 ───────────────────────────────
    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employeeList", employeeService.findAll());
        model.addAttribute("departmentList", employeeService.findAllDepartments());
        model.addAttribute("positionList", employeeService.findAllPositions());
        return "employees";
    }

    @GetMapping("/attendance")
    public String attendance(Model model) {
        model.addAttribute("attendanceList", attendanceService.findAll());
        model.addAttribute("employeeList", employeeService.findAll());
        return "attendance";
    }

    // ── 생산 페이지 ───────────────────────────────
    @GetMapping("/work-orders")
    public String workOrders(Model model) {
        model.addAttribute("workOrderList", workOrderService.findAll());
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        return "work-orders";
    }

    @GetMapping("/bom")
    public String bom(Model model) {
        model.addAttribute("bomList", bomService.findAll());
        model.addAttribute("productList", productService.findAll());
        return "bom";
    }

    // ── 관리자 페이지 ─────────────────────────────
    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        return "admin/users";
    }
}
