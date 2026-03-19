package com.gaebalfan.erp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAll(Exception ex) {
        log.error("=== PAGE ERROR ===", ex);
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
