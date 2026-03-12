package com.gaebalfan.erp.controller;

import com.gaebalfan.erp.domain.FinancialStatement;
import com.gaebalfan.erp.mapper.FinancialStatementMapper;
import com.gaebalfan.erp.mapper.OperatingExpenseMapper;
import com.gaebalfan.erp.mapper.SaleMapper;
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

            String monthStr = String.format("%d-%02d", year, mo);
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
