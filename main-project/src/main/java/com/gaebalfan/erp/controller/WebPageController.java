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

    public WebPageController(InventoryService inventoryService,
                             PurchaseOrderService purchaseOrderService,
                             ReceiptService receiptService,
                             ShipmentService shipmentService) {
        this.inventoryService = inventoryService;
        this.purchaseOrderService = purchaseOrderService;
        this.receiptService = receiptService;
        this.shipmentService = shipmentService;
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
        return "purchase-orders";
    }

    @GetMapping("/receipts")
    public String receipts(Model model) {
        model.addAttribute("receiptList", receiptService.findAll());
        return "receipts";
    }

    @GetMapping("/shipments")
    public String shipments(Model model) {
        model.addAttribute("shipmentList", shipmentService.findAll());
        return "shipments";
    }
}