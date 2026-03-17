package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.FinancialStatement;
import com.gaebalfan.erp.mapper.FinancialStatementMapper;
import com.gaebalfan.erp.mapper.OperatingExpenseMapper;
import com.gaebalfan.erp.mapper.SaleMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
public class FinancialController {

    private final SaleMapper saleMapper;
    private final OperatingExpenseMapper expenseMapper;
    private final FinancialStatementMapper fsMapper;

    public FinancialController(SaleMapper saleMapper, OperatingExpenseMapper expenseMapper, FinancialStatementMapper fsMapper) {
        this.saleMapper = saleMapper;
        this.expenseMapper = expenseMapper;
        this.fsMapper = fsMapper;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestParam(defaultValue = "0") int year) {
        if (year == 0) year = LocalDate.now().getYear();

        Map<String, Object> saleSummary = saleMapper.findYearlySummary(year);
        Map<String, Object> expSummary  = expenseMapper.findYearlySummary(year);

        BigDecimal revenue     = toBD(saleSummary, "revenue");
        BigDecimal cogs        = toBD(saleSummary, "cogs");
        BigDecimal grossProfit = toBD(saleSummary, "gross_profit");
        BigDecimal expenses    = toBD(expSummary,  "total_expense");
        BigDecimal opProfit    = grossProfit.subtract(expenses);
        BigDecimal opMargin    = revenue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : opProfit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year",        year);
        result.put("revenue",     revenue);
        result.put("cogs",        cogs);
        result.put("grossProfit", grossProfit);
        result.put("expenses",    expenses);
        result.put("opProfit",    opProfit);
        result.put("opMargin",    opMargin);
        result.put("saleCount",   saleSummary != null ? saleSummary.get("sale_count") : 0);

        // 월별 데이터
        List<Map<String, Object>> monthlySales = saleMapper.findMonthlySummary(year);
        List<Map<String, Object>> monthlyExp   = expenseMapper.findMonthlySummary(year);
        List<Map<String, Object>> expByType    = expenseMapper.findSummaryByType(year);

        // 월별 통합 (1~12)
        Map<Integer, BigDecimal> expByMonth = new HashMap<>();
        for (Map<String, Object> m : monthlyExp) {
            int mo = toInt(m.get("month"));
            expByMonth.put(mo, toBDObj(m.get("total_expense")));
        }

        List<Map<String, Object>> monthly = new ArrayList<>();
        for (int mo = 1; mo <= 12; mo++) {
            BigDecimal rev = BigDecimal.ZERO, cg = BigDecimal.ZERO, gp = BigDecimal.ZERO;
            for (Map<String, Object> s : monthlySales) {
                if (toInt(s.get("month")) == mo) {
                    rev = toBDObj(s.get("revenue"));
                    cg  = toBDObj(s.get("cogs"));
                    gp  = toBDObj(s.get("gross_profit"));
                    break;
                }
            }
            BigDecimal exp = expByMonth.getOrDefault(mo, BigDecimal.ZERO);
            BigDecimal op  = gp.subtract(exp);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("month",       mo);
            row.put("revenue",     rev);
            row.put("cogs",        cg);
            row.put("grossProfit", gp);
            row.put("expenses",    exp);
            row.put("opProfit",    op);
            monthly.add(row);
        }

        result.put("monthly", monthly);
        result.put("expByType", expByType);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/statements")
    public ResponseEntity<List<FinancialStatement>> getStatements(
            @RequestParam(defaultValue = "0") int year) {
        if (year == 0) year = LocalDate.now().getYear();
        return ResponseEntity.ok(fsMapper.findByYear(String.valueOf(year)));
    }

    @PostMapping("/statements/save")
    public ResponseEntity<Void> saveStatements(
            @RequestParam(defaultValue = "0") int year) {
        if (year == 0) year = LocalDate.now().getYear();

        List<Map<String, Object>> monthlySales = saleMapper.findMonthlySummary(year);
        List<Map<String, Object>> monthlyExp   = expenseMapper.findMonthlySummary(year);

        Map<Integer, BigDecimal> expByMonth = new HashMap<>();
        for (Map<String, Object> m : monthlyExp) {
            expByMonth.put(toInt(m.get("month")), toBDObj(m.get("total_expense")));
        }

        for (int mo = 1; mo <= 12; mo++) {
            BigDecimal rev = BigDecimal.ZERO, cg = BigDecimal.ZERO, gp = BigDecimal.ZERO;
            for (Map<String, Object> s : monthlySales) {
                if (toInt(s.get("month")) == mo) {
                    rev = toBDObj(s.get("revenue"));
                    cg  = toBDObj(s.get("cogs"));
                    gp  = toBDObj(s.get("gross_profit"));
                    break;
                }
            }
            BigDecimal exp = expByMonth.getOrDefault(mo, BigDecimal.ZERO);
            BigDecimal op  = gp.subtract(exp);

            String monthStr = String.format("%d-%02d-01", year, mo);
            FinancialStatement fs = new FinancialStatement();
            fs.setMonth(monthStr);
            fs.setRevenue(rev);
            fs.setCogs(cg);
            fs.setGrossProfit(gp);
            fs.setExpenses(exp);
            fs.setOperatingProfit(op);
            fsMapper.upsert(fs);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(defaultValue = "0") int year) throws java.io.IOException {
        if (year == 0) year = LocalDate.now().getYear();

        List<Map<String, Object>> monthlySales = saleMapper.findMonthlySummary(year);
        List<Map<String, Object>> monthlyExp   = expenseMapper.findMonthlySummary(year);

        Map<Integer, BigDecimal> expByMonth = new HashMap<>();
        for (Map<String, Object> m : monthlyExp) {
            expByMonth.put(toInt(m.get("month")), toBDObj(m.get("total_expense")));
        }

        StringBuilder sb = new StringBuilder();
        sb.append('\uFEFF'); // BOM for Excel UTF-8
        sb.append("월,매출액,매출원가,매출총이익,판매비및관리비,영업이익,영업이익률\n");

        BigDecimal totalRev = BigDecimal.ZERO, totalCg = BigDecimal.ZERO, totalGp = BigDecimal.ZERO,
                   totalExp = BigDecimal.ZERO, totalOp = BigDecimal.ZERO;

        for (int mo = 1; mo <= 12; mo++) {
            BigDecimal rev = BigDecimal.ZERO, cg = BigDecimal.ZERO, gp = BigDecimal.ZERO;
            for (Map<String, Object> s : monthlySales) {
                if (toInt(s.get("month")) == mo) {
                    rev = toBDObj(s.get("revenue"));
                    cg  = toBDObj(s.get("cogs"));
                    gp  = toBDObj(s.get("gross_profit"));
                    break;
                }
            }
            BigDecimal exp = expByMonth.getOrDefault(mo, BigDecimal.ZERO);
            BigDecimal op  = gp.subtract(exp);
            String margin  = rev.compareTo(BigDecimal.ZERO) == 0 ? "0.00"
                    : op.divide(rev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toString();

            sb.append(year).append('-').append(String.format("%02d", mo)).append(',')
              .append(rev).append(',').append(cg).append(',').append(gp).append(',')
              .append(exp).append(',').append(op).append(',').append(margin).append('%').append('\n');

            totalRev = totalRev.add(rev); totalCg = totalCg.add(cg); totalGp = totalGp.add(gp);
            totalExp = totalExp.add(exp); totalOp = totalOp.add(op);
        }

        String totalMargin = totalRev.compareTo(BigDecimal.ZERO) == 0 ? "0.00"
                : totalOp.divide(totalRev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toString();
        sb.append("합계,").append(totalRev).append(',').append(totalCg).append(',').append(totalGp).append(',')
          .append(totalExp).append(',').append(totalOp).append(',').append(totalMargin).append('%').append('\n');

        byte[] rawBytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Python으로 정제 → xlsx 반환
        byte[] xlsxBytes = runPythonClean(rawBytes);
        if (xlsxBytes != null && xlsxBytes.length > 0) {
            String xlsxName = "finance_" + year + "_report.xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + xlsxName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(xlsxBytes);
        }

        // Python 실패 시 raw CSV 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"finance_" + year + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(rawBytes);
    }

    @GetMapping("/export/range")
    public ResponseEntity<byte[]> exportRange(
            @RequestParam int fromYear,
            @RequestParam int toYear) throws Exception {

        // 연도별 데이터를 JSON 배열로 구성
        StringBuilder json = new StringBuilder("[");
        for (int year = fromYear; year <= toYear; year++) {
            List<Map<String, Object>> monthlySales = saleMapper.findMonthlySummary(year);
            List<Map<String, Object>> monthlyExp   = expenseMapper.findMonthlySummary(year);

            Map<Integer, BigDecimal> expByMonth = new HashMap<>();
            for (Map<String, Object> m : monthlyExp)
                expByMonth.put(toInt(m.get("month")), toBDObj(m.get("total_expense")));

            json.append("{\"year\":").append(year).append(",\"months\":[");
            for (int mo = 1; mo <= 12; mo++) {
                BigDecimal rev = BigDecimal.ZERO, cg = BigDecimal.ZERO, gp = BigDecimal.ZERO;
                for (Map<String, Object> s : monthlySales) {
                    if (toInt(s.get("month")) == mo) {
                        rev = toBDObj(s.get("revenue"));
                        cg  = toBDObj(s.get("cogs"));
                        gp  = toBDObj(s.get("gross_profit"));
                        break;
                    }
                }
                BigDecimal exp    = expByMonth.getOrDefault(mo, BigDecimal.ZERO);
                BigDecimal op     = gp.subtract(exp);
                String     margin = rev.compareTo(BigDecimal.ZERO) == 0 ? "0.00"
                        : op.divide(rev, 4, RoundingMode.HALF_UP)
                           .multiply(BigDecimal.valueOf(100))
                           .setScale(2, RoundingMode.HALF_UP).toString();
                if (mo > 1) json.append(",");
                json.append("{\"month\":\"").append(year).append("-").append(String.format("%02d", mo)).append("\"")
                    .append(",\"revenue\":").append(rev)
                    .append(",\"cogs\":").append(cg)
                    .append(",\"gross_profit\":").append(gp)
                    .append(",\"expenses\":").append(exp)
                    .append(",\"operating_profit\":").append(op)
                    .append(",\"operating_margin\":").append(margin).append("}");
            }
            json.append("]}");
            if (year < toYear) json.append(",");
        }
        json.append("]");

        byte[] jsonBytes = json.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] xlsxBytes = runPythonRange(jsonBytes);

        if (xlsxBytes != null && xlsxBytes.length > 0) {
            String filename = "finance_" + fromYear + "-" + toYear + "_report.xlsx";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(xlsxBytes);
        }
        return ResponseEntity.internalServerError().build();
    }

    private byte[] runPythonRange(byte[] jsonData) {
        String scriptPath = System.getProperty("user.dir") + java.io.File.separator + "finance_range.py";
        java.util.logging.Logger log = java.util.logging.Logger.getLogger(getClass().getName());
        log.info("[Python] script path: " + scriptPath);
        log.info("[Python] file exists: " + new java.io.File(scriptPath).exists());

        // python / python3 둘 다 시도
        for (String cmd : new String[]{"python", "python3"}) {
            try {
                ProcessBuilder pb = new ProcessBuilder(cmd, scriptPath);
                pb.redirectErrorStream(false);
                Process process = pb.start();

                // JSON 데이터 stdin으로 전달
                process.getOutputStream().write(jsonData);
                process.getOutputStream().close();

                byte[] result = process.getInputStream().readAllBytes();
                byte[] err    = process.getErrorStream().readAllBytes();
                int    exit   = process.waitFor();

                if (err.length > 0) log.warning("[Python stderr] " + new String(err));
                log.info("[Python] exit=" + exit + " output=" + result.length + " bytes");

                if (exit == 0 && result.length > 0) return result;
            } catch (Exception e) {
                log.warning("[Python] cmd=" + cmd + " error=" + e.getMessage());
            }
        }
        return null;
    }

    private byte[] runPythonClean(byte[] rawCsv) {
        try {
            String scriptPath = System.getProperty("user.dir") + "/finance_clean.py";
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            // raw CSV를 Python stdin으로 전달
            process.getOutputStream().write(rawCsv);
            process.getOutputStream().close();

            // Python stdout에서 정제된 CSV 읽기
            byte[] cleaned = process.getInputStream().readAllBytes();
            process.waitFor();

            return cleaned.length > 0 ? cleaned : null;
        } catch (Exception e) {
            return null; // Python 실패 시 raw CSV 반환
        }
    }

    private BigDecimal toBD(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) return BigDecimal.ZERO;
        return toBDObj(map.get(key));
    }

    private BigDecimal toBDObj(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        return new BigDecimal(val.toString());
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        return Integer.parseInt(val.toString());
    }
}
