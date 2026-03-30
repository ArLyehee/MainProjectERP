<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>거래명세서 ${statement.statementNo}</title>
    <style>
        *{margin:0;padding:0;box-sizing:border-box;}
        body{font-family:'맑은 고딕','Malgun Gothic',sans-serif;font-size:12px;padding:20px;color:#111;}
        .print-wrap{max-width:800px;margin:0 auto;border:2px solid #000;padding:0;}
        .doc-title{text-align:center;font-size:24px;font-weight:700;padding:16px;border-bottom:2px solid #000;letter-spacing:4px;}
        .info-section{display:grid;grid-template-columns:1fr 1fr;border-bottom:1px solid #000;}
        .info-block{padding:12px 16px;}
        .info-block.border-right{border-right:1px solid #000;}
        .info-label{font-size:11px;color:#555;margin-bottom:2px;}
        .info-value{font-size:13px;font-weight:600;}
        .info-row{display:flex;margin-bottom:6px;}
        .info-row .lbl{width:70px;color:#555;font-size:11px;padding-top:1px;}
        .info-row .val{font-size:12px;font-weight:500;flex:1;}
        .summary-bar{display:grid;grid-template-columns:repeat(3,1fr);border-bottom:1px solid #000;}
        .summary-box{padding:10px 16px;text-align:center;}
        .summary-box+.summary-box{border-left:1px solid #000;}
        .summary-box .s-label{font-size:11px;color:#555;}
        .summary-box .s-val{font-size:16px;font-weight:700;color:#1e40af;margin-top:2px;}
        .items-table{width:100%;border-collapse:collapse;}
        .items-table th{background:#f3f4f6;text-align:center;padding:7px 8px;border-bottom:1px solid #ccc;border-right:1px solid #e5e7eb;font-size:11px;}
        .items-table td{padding:6px 8px;border-bottom:1px solid #e5e7eb;border-right:1px solid #e5e7eb;font-size:12px;}
        .items-table td:last-child,.items-table th:last-child{border-right:none;}
        .items-table tr:last-child td{border-bottom:none;}
        .text-right{text-align:right;}
        .text-center{text-align:center;}
        .notes-bar{padding:10px 16px;border-top:1px solid #000;font-size:12px;}
        .stamp-area{display:grid;grid-template-columns:1fr 1fr;border-top:1px solid #000;}
        .stamp-box{padding:12px 16px;}
        .stamp-box+.stamp-box{border-left:1px solid #000;}
        .stamp-title{font-size:11px;color:#555;margin-bottom:4px;}
        .stamp-line{height:50px;border-bottom:1px dashed #ccc;margin-bottom:4px;}
        .print-btn{position:fixed;top:20px;right:20px;background:#2563eb;color:#fff;border:none;border-radius:8px;padding:10px 20px;font-size:14px;cursor:pointer;box-shadow:0 2px 8px rgba(0,0,0,0.2);}
        .print-btn:hover{background:#1d4ed8;}
        @media print{.print-btn{display:none;}body{padding:0;}.print-wrap{border:1px solid #000;max-width:100%;}}
    </style>
</head>
<body>
<button class="print-btn" onclick="window.print()">🖨️ 인쇄</button>
<div class="print-wrap">
    <div class="doc-title">거 래 명 세 서</div>
    <div class="info-section">
        <div class="info-block border-right">
            <div class="info-label" style="font-size:13px;font-weight:700;margin-bottom:8px;">▶ 공급자</div>
            <div class="info-row"><span class="lbl">상호(법인명)</span><span class="val">${statement.supplyName}</span></div>
            <div class="info-row"><span class="lbl">사업자번호</span><span class="val">${statement.customerBizNo != null ? statement.customerBizNo : ''}</span></div>
            <div class="info-row"><span class="lbl">연락처</span><span class="val">${statement.customerTel != null ? statement.customerTel : ''}</span></div>
            <div class="info-row"><span class="lbl">주소</span><span class="val">${statement.customerAddr != null ? statement.customerAddr : ''}</span></div>
        </div>
        <div class="info-block">
            <div class="info-label" style="font-size:13px;font-weight:700;margin-bottom:8px;">▶ 공급받는자</div>
            <div class="info-row"><span class="lbl">상호(법인명)</span><span class="val">개발환기좀해 ERP</span></div>
            <div class="info-row"><span class="lbl">명세서번호</span><span class="val">${statement.statementNo}</span></div>
            <div class="info-row"><span class="lbl">발행일</span><span class="val">${statement.issueDate}</span></div>
            <div class="info-row"><span class="lbl">담당자</span><span class="val">${not empty statement.managerName ? statement.managerName : '-'}</span></div>
        </div>
    </div>
    <div class="summary-bar">
        <div class="summary-box"><div class="s-label">공급가액</div><div class="s-val"><fmt:formatNumber value="${statement.totalAmount}" type="number" groupingUsed="true"/>원</div></div>
        <div class="summary-box"><div class="s-label">세액 (10%)</div><div class="s-val"><fmt:formatNumber value="${statement.taxAmount}" type="number" groupingUsed="true"/>원</div></div>
        <div class="summary-box"><div class="s-label">합계금액</div><div class="s-val"><fmt:formatNumber value="${statement.grandTotal}" type="number" groupingUsed="true"/>원</div></div>
    </div>
    <div class="items-section">
        <table class="items-table">
            <thead><tr><th style="width:5%">No</th><th style="width:40%">품목명</th><th style="width:12%">수량</th><th style="width:20%">단가</th><th style="width:23%">금액</th></tr></thead>
            <tbody>
            <c:forEach var="item" items="${statement.items}" varStatus="idx">
            <tr>
                <td class="text-center">${idx.count}</td>
                <td>${item.itemName}</td>
                <td class="text-center">${item.quantity}</td>
                <td class="text-right"><fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/></td>
                <td class="text-right"><fmt:formatNumber value="${item.amount}" type="number" groupingUsed="true"/></td>
            </tr>
            </c:forEach>
            <c:if test="${statement.items.size() < 10}">
            <c:forEach begin="${statement.items.size() + 1}" end="10" var="i">
            <tr style="height:28px;"><td></td><td></td><td></td><td></td><td></td></tr>
            </c:forEach>
            </c:if>
            </tbody>
        </table>
    </div>
    <c:if test="${not empty statement.notes}">
    <div class="notes-bar"><span style="color:#555;">비고:</span> <span>${statement.notes}</span></div>
    </c:if>
    <div class="stamp-area">
        <div class="stamp-box"><div class="stamp-title">공급자 확인</div><div class="stamp-line"></div><div style="font-size:11px;color:#888;">서명 또는 날인</div></div>
        <div class="stamp-box"><div class="stamp-title">수령 확인</div><div class="stamp-line"></div><div style="font-size:11px;color:#888;">서명 또는 날인</div></div>
    </div>
</div>
</body>
</html>
