package com.gaebalfan.erp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaebalfan.erp.domain.TransactionStatement;
import com.gaebalfan.erp.domain.TransactionStatementItem;
import com.gaebalfan.erp.service.TransactionStatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private static final Logger log = LoggerFactory.getLogger(OcrController.class);
    private final TransactionStatementService statementService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ocr.python.cmd:python}")
    private String pythonCmd;

    @Value("${ocr.project.path}")
    private String ocrProjectPath;

    public OcrController(TransactionStatementService statementService) {
        this.statementService = statementService;
    }

    @PostMapping("/transaction-statement")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> processOcr(@RequestParam("file") MultipartFile file) {
        File tempFile = null;
        try {
            // 1. 파일을 OCR input 폴더에 임시 저장
            File inputDir = new File(ocrProjectPath, "input");
            inputDir.mkdirs();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            tempFile = new File(inputDir, fileName);
            file.transferTo(tempFile);

            // 2. Python OCR 실행
            String mainPy = new File(ocrProjectPath, "src/main.py").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder(pythonCmd, mainPy, "--erp-mode", tempFile.getAbsolutePath());
            pb.directory(new File(ocrProjectPath));
            pb.redirectErrorStream(false);
            Process process = pb.start();
            String stdout = new String(process.getInputStream().readAllBytes(), "UTF-8").trim();
            String stderr = new String(process.getErrorStream().readAllBytes(), "UTF-8").trim();
            process.waitFor();

            if (!stderr.isEmpty()) {
                log.debug("OCR stderr:\n{}", stderr);
            }
            if (stdout.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "OCR 실행 실패: " + stderr));
            }

            // stdout에서 JSON 라인만 추출 (마지막 { 로 시작하는 줄)
            String jsonLine = stdout;
            for (String line : stdout.split("\n")) {
                if (line.trim().startsWith("{")) jsonLine = line.trim();
            }

            // 3. JSON 파싱
            Map<String, Object> result = objectMapper.readValue(jsonLine, Map.class);
            if (Boolean.FALSE.equals(result.get("success"))) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", result.get("error")));
            }
            log.debug("OCR 파싱 결과: {}", jsonLine);

            Map<String, Object> data = (Map<String, Object>) result.get("data");

            // 4. TransactionStatement 생성
            TransactionStatement stmt = new TransactionStatement();
            String issueDateStr = str(data, "issue_date");
            stmt.setIssueDate(issueDateStr != null ? LocalDate.parse(issueDateStr) : LocalDate.now());
            String supplyName = str(data, "customer_name");
            stmt.setSupplyName(supplyName != null ? supplyName : "(OCR 미인식 - 수정 필요)");
            stmt.setCustomerName("개발환기좀해 ERP");
            stmt.setCustomerBizNo(str(data, "customer_biz_no"));
            stmt.setCustomerTel(str(data, "customer_tel"));
            stmt.setCustomerAddr(str(data, "customer_addr"));
            stmt.setManagerName(str(data, "manager_name"));
            stmt.setNotes(str(data, "notes"));
            stmt.setTotalAmount(decimal(data, "total_amount"));
            stmt.setTaxAmount(decimal(data, "tax_amount"));
            stmt.setGrandTotal(decimal(data, "grand_total"));

            // 5. 품목 생성
            List<TransactionStatementItem> items = new ArrayList<>();
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) data.get("items");
            if (itemList != null) {
                for (Map<String, Object> item : itemList) {
                    TransactionStatementItem ti = new TransactionStatementItem();
                    ti.setItemName(str(item, "item_name"));
                    ti.setQuantity(((Number) item.getOrDefault("quantity", 1)).intValue());
                    ti.setUnitPrice(decimal(item, "unit_price"));
                    ti.setAmount(decimal(item, "amount"));
                    items.add(ti);
                }
            }
            // 품목 없으면 기본 항목 추가
            if (items.isEmpty()) {
                TransactionStatementItem ti = new TransactionStatementItem();
                ti.setItemName("(OCR 인식 항목)");
                ti.setQuantity(1);
                BigDecimal tot = stmt.getTotalAmount() != null ? stmt.getTotalAmount() : BigDecimal.ZERO;
                ti.setUnitPrice(tot);
                ti.setAmount(tot);
                items.add(ti);
            }

            // 6. DB 저장
            statementService.insert(stmt, items);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    private BigDecimal decimal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).longValue());
        try { return new BigDecimal(v.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }
}
