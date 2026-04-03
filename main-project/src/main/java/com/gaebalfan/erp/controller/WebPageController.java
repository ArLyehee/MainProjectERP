package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.config.SystemSettings;
import com.gaebalfan.erp.service.*;
import com.gaebalfan.erp.service.TransactionStatementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.gaebalfan.erp.security.ErpUserDetails;
import org.springframework.security.core.Authentication;

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
    private final OrderService orderService;
    private final SaleService saleService;
    private final OperatingExpenseService expenseService;
    private final TransactionStatementService transactionStatementService;
    private final SystemSettings systemSettings;

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
                             BomService bomService,
                             SaleService saleService,
                             OperatingExpenseService expenseService,
                             TransactionStatementService transactionStatementService,
                             OrderService orderService,
                             SystemSettings systemSettings) {
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
        this.orderService = orderService;
        this.saleService = saleService;
        this.expenseService = expenseService;
        this.transactionStatementService = transactionStatementService;
        this.systemSettings = systemSettings;
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
    public String inventory(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "") String q,
                            Model model) {
        int size = 20;
        int total = inventoryService.count(q);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        var allList = inventoryService.findAll();
        int threshold = systemSettings.getLowStockThreshold();
        var shortageList = allList.stream()
                .filter(i -> i.getQuantity() < threshold)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("inventoryList", inventoryService.findAllPaged(page, size, q));
        model.addAttribute("shortageList", shortageList);
        model.addAttribute("lowStockThreshold", threshold);
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        model.addAttribute("q", q);
        return "inventory";
    }

    @GetMapping("/purchase-orders")
    public String purchaseOrders(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "") String q,
                                 Model model) {
        int size = 20;
        int total = purchaseOrderService.count(q);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        model.addAttribute("purchaseOrderList", purchaseOrderService.findAllPaged(page, size, q));
        model.addAttribute("supplierList", supplierService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        model.addAttribute("q", q);
        return "purchase-orders";
    }

    @GetMapping("/receipts")
    public String receipts(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 20;
        int total = receiptService.count();
        int totalPages = (int) Math.ceil((double) total / size);
        model.addAttribute("receiptList", receiptService.findAllPaged(page, size));
        model.addAttribute("purchaseOrderList", purchaseOrderService.findAll());
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        return "receipts";
    }

    @GetMapping("/shipments")
    public String shipments(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 20;
        int total = shipmentService.count();
        int totalPages = (int) Math.ceil((double) total / size);
        model.addAttribute("shipmentList", shipmentService.findAllPaged(page, size));
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        return "shipments";
    }

    // ── 기준정보 페이지 ────────────────────────────
    @GetMapping("/products")
    public String products(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "") String q, Model model) {
        int size = 20;
        int total = productService.count(q);
        int totalPages = (int) Math.ceil((double) total / size);
        model.addAttribute("productList", productService.findAllPaged(page, size, q));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        model.addAttribute("q", q);
        return "products";
    }

    @GetMapping("/suppliers")
    public String suppliers(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "") String q,
                            Model model) {
        int size = 20;
        int total = supplierService.count(q);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        model.addAttribute("supplierList", supplierService.findAllPaged(page, size, q));
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        model.addAttribute("q", q);
        return "suppliers";
    }

    @GetMapping("/warehouses")
    public String warehouses(Model model) {
        model.addAttribute("warehouseList", warehouseService.findAll());
        return "warehouses";
    }

    // ── 인사 페이지 ───────────────────────────────
    @GetMapping("/employees")
    public String employees(@RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "") String sort,
                            @RequestParam(defaultValue = "asc") String dir,
                            Model model) {
        int size = 20;
        int total = employeeService.count(q);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        model.addAttribute("employeeList", employeeService.findAllPaged(page, size, q, sort, dir));
        model.addAttribute("departmentList", employeeService.findAllDepartments());
        model.addAttribute("positionList", employeeService.findAllPositions());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "employees";
    }

    @GetMapping("/attendance")
    public String attendance(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 20;
        int total = attendanceService.count();
        int totalPages = (int) Math.ceil((double) total / size);
        model.addAttribute("attendanceList", attendanceService.findAllPaged(page, size));
        model.addAttribute("employeeList", employeeService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        return "attendance";
    }

    // ── 생산 페이지 ───────────────────────────────
    @GetMapping("/work-orders")
    public String workOrders(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 20;
        int total = workOrderService.count();
        int totalPages = (int) Math.ceil((double) total / size);
        model.addAttribute("workOrderList", workOrderService.findAllPaged(page, size));
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        return "work-orders";
    }

    @GetMapping("/bom")
    public String bom(Model model) {
        model.addAttribute("bomList", bomService.findAll());
        model.addAttribute("productList", productService.findAll());
        return "bom";
    }

    // ── 매출/비용 페이지 ──────────────────────────
    @GetMapping("/sales")
    public String sales(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 20;
        int total = saleService.count();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        model.addAttribute("saleList", saleService.findAllPaged(page, size));
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        return "sales";
    }

    @GetMapping("/expenses")
    public String expenses(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "") String q,
                           Model model) {
        int size = 20;
        int total = expenseService.count(q);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        model.addAttribute("expenseList", expenseService.findAllPaged(page, size, q));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        model.addAttribute("q", q);
        return "expenses";
    }

    // ── 재무제표 페이지 ───────────────────────────
    @GetMapping("/finance")
    public String finance(Model model) {
        model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
        return "finance";
    }   

    // ── 거래명세서 페이지 ─────────────────────────
    @GetMapping("/transaction-statements")
    public String transactionStatements(@RequestParam(defaultValue = "1") int page, Model model, Authentication authentication) {
        int size = 20;
        int total = transactionStatementService.count();
        int totalPages = (int) Math.ceil((double) total / size);
        model.addAttribute("statementList", transactionStatementService.findAllPaged(page, size));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        if (authentication != null && authentication.getPrincipal() instanceof ErpUserDetails u) {
            model.addAttribute("currentUserName", u.getDisplayName());
        }
        return "transaction-statements";
    }

    @GetMapping("/transaction-statements/{id}/print")
    public String printStatement(@PathVariable Long id, Model model) {
        model.addAttribute("statement", transactionStatementService.findById(id));
        return "transaction-statement-print";
    }

    // ── 주문 관리 페이지 ──────────────────────────
    @GetMapping("/orders")
    public String orders(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 20;
        int total = orderService.count();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / size));
        model.addAttribute("orderList", orderService.findAllPaged(page, size));
        model.addAttribute("productList", productService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageStart", Math.max(1, page - 5));
        model.addAttribute("pageEnd", Math.min(totalPages, page + 5));
        return "orders";
    }

    // ── 관리자 페이지 ─────────────────────────────
    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        return "admin/users";
    }

    @GetMapping("/admin/settings")
    public String adminSettings(Model model) {
        return "admin/settings";
    }
}
