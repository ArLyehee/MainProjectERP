package com.gaebalfan.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class WebPageController {

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
}
