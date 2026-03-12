package com.gaebalfan.erp.domain;

import java.math.BigDecimal;

public class FinancialStatement {
    private String     month;
    private BigDecimal revenue;
    private BigDecimal cogs;
    private BigDecimal grossProfit;
    private BigDecimal expenses;
    private BigDecimal operatingProfit;

    public String     getMonth()           { return month; }
    public BigDecimal getRevenue()         { return revenue; }
    public BigDecimal getCogs()            { return cogs; }
    public BigDecimal getGrossProfit()     { return grossProfit; }
    public BigDecimal getExpenses()        { return expenses; }
    public BigDecimal getOperatingProfit() { return operatingProfit; }

    public void setMonth(String month)                         { this.month = month; }
    public void setRevenue(BigDecimal revenue)                 { this.revenue = revenue; }
    public void setCogs(BigDecimal cogs)                       { this.cogs = cogs; }
    public void setGrossProfit(BigDecimal grossProfit)         { this.grossProfit = grossProfit; }
    public void setExpenses(BigDecimal expenses)               { this.expenses = expenses; }
    public void setOperatingProfit(BigDecimal operatingProfit) { this.operatingProfit = operatingProfit; }
}
