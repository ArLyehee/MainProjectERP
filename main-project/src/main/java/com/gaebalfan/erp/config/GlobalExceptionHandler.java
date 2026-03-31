package com.gaebalfan.erp.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 정적 리소스 없음(favicon.ico 등)은 404로 조용히 처리
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public Object handleAll(Exception ex, HttpServletRequest request) {
        log.error("=== ERROR [{}] ===", request.getRequestURI(), ex);
        // /api/** 요청은 JSON 에러 반환
        if (request.getRequestURI().startsWith("/api/")) {
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
            return ResponseEntity.status(500).body(Map.of("error", msg));
        }
        // 일반 페이지는 기존 에러 뷰 반환
        ModelAndView mav = new ModelAndView("error-debug");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("causeMessage", ex.getCause() != null ? ex.getCause().getMessage() : "없음");
        mav.addObject("stackTrace", getStackTrace(ex));
        return mav;
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString()).append("\n");
        for (StackTraceElement e : ex.getStackTrace()) {
            sb.append("  at ").append(e).append("\n");
            if (sb.length() > 3000) { sb.append("..."); break; }
        }
        if (ex.getCause() != null) {
            sb.append("Caused by: ").append(ex.getCause().toString()).append("\n");
            for (StackTraceElement e : ex.getCause().getStackTrace()) {
                sb.append("  at ").append(e).append("\n");
                if (sb.length() > 5000) { sb.append("..."); break; }
            }
        }
        return sb.toString();
    }
}
