<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>입고 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="receipts"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>입고 관리</h2><p>발주 기반 입고 현황을 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 입고 등록</button></div><div class="search-bar"><input type="text" id="searchInput" placeholder="제품명, 입고일 검색..." oninput="filterTable()" class="search-input"></div><div class="card"><table id="receiptTable"><thead><tr><th class="sort-th" onclick="sortTable(this,0,'str')">발주번호<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'num')">수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">입고일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th></tr></thead><tbody><c:if test="${empty receiptList}"><tr><td colspan="4" class="empty-state">등록된 입고 내역이 없습니다.</td></tr></c:if><c:forEach var="r" items="${receiptList}">
<tr style="cursor:pointer;" onclick="openPoDetail('${r.poId}')"><td><span style="font-size:11px;color:#6366f1;">${r.poId != null ? r.poId : '-'}</span></td><td>${r.productName != null ? r.productName : r.productId}</td><td class="qty">${r.quantity}</td><td>${r.receiptDate}</td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/receipts?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/receipts?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/receipts?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- 발주 상세 모달 -->
<div class="modal-overlay" id="poDetailModal">
<div class="modal" style="max-width:620px;">
  <h3 id="detailTitle">발주 상세</h3>
  <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px 24px;margin-bottom:16px;font-size:13px;" id="detailMeta"></div>
  <div style="margin-bottom:8px;font-weight:600;font-size:13px;">📦 발주 품목</div>
  <table style="width:100%;border-collapse:collapse;font-size:12px;margin-bottom:16px;">
    <thead><tr style="background:#f3f4f6;"><th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">품목명</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">수량</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">단가</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">금액</th></tr></thead>
    <tbody id="detailItems"></tbody>
  </table>
  <div style="margin-bottom:8px;font-weight:600;font-size:13px;">✅ 입고 내역</div>
  <table style="width:100%;border-collapse:collapse;font-size:12px;">
    <thead><tr style="background:#f3f4f6;"><th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">품목명</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">입고 수량</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">입고일</th></tr></thead>
    <tbody id="detailReceipts"></tbody>
  </table>
  <div class="modal-footer"><button class="btn-cancel-modal" onclick="document.getElementById('poDetailModal').classList.remove('open')">닫기</button></div>
</div>
</div>

<!-- 입고 등록 모달 -->
<div class="modal-overlay" id="receiptModal"><div class="modal"><h3>입고 등록</h3><div class="form-group"><label>발주 번호 *</label><select id="rPoId"><option value="">선택</option><c:forEach var="po" items="${purchaseOrderList}"><c:if test="${po.status == 'APPROVED'}"><option value="${po.poId}" data-item="${po.item}">${po.poCode != null ? po.poCode : 'PO-'.concat(po.poId)} (${po.supplierName})</option></c:if></c:forEach></select></div><div class="form-group"><label>제품 *</label><select id="rProductId"><option value="">선택</option><c:forEach var="p" items="${productList}"><option value="${p.productId}">${p.productName}</option></c:forEach></select></div><div class="form-group"><label>수량 *</label><input type="number" id="rQuantity" placeholder="0" min="1"></div><div class="form-group"><label>입고 창고 *</label><select id="rWarehouseId"><option value="">선택</option><c:forEach var="w" items="${warehouseList}"><option value="${w.warehouseId}">${w.warehouseName}</option></c:forEach></select></div><div class="form-group"><label>입고일</label><input type="date" id="rReceiptDate"></div><div class="modal-footer"><button class="btn-cancel-modal" onclick="closeReceiptModal()">취소</button><button class="btn-save" onclick="saveReceipt()">저장</button></div></div>
</div><script>
const csrf = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const _sort = {col:-1, asc:true};
function sortTable(th, col, type) {
    document.querySelectorAll('.sort-th').forEach(t => t.classList.remove('sort-asc','sort-desc'));
    _sort.asc = _sort.col === col ? !_sort.asc : true;
    _sort.col = col;
    th.classList.add(_sort.asc ? 'sort-asc' : 'sort-desc');
    const tbody = document.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr')).filter(tr => tr.querySelectorAll('td').length > 1);
    rows.sort((a, b) => {
        const va = a.querySelectorAll('td')[col].textContent.trim();
        const vb = b.querySelectorAll('td')[col].textContent.trim();
        const cmp = type === 'num' ? (parseFloat(va.replace(/,/g,''))||0) - (parseFloat(vb.replace(/,/g,''))||0) : va.localeCompare(vb, 'ko');
        return _sort.asc ? cmp : -cmp;
    });
    rows.forEach(r => tbody.appendChild(r));
}
function filterTable() {
    const q = document.getElementById('searchInput').value.toLowerCase();
    document.querySelectorAll('tbody tr').forEach(tr => {
        tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
}
document.getElementById('rPoId').addEventListener('change', function() {
    const selected = this.options[this.selectedIndex];
    const item = selected.dataset.item;
    if (item) document.getElementById('rQuantity').value = item;
    else document.getElementById('rQuantity').value = '';
});
function openPoDetail(poCode) {
    if (!poCode || poCode === 'null') return;
    fetch('/api/purchase-orders/by-code/' + encodeURIComponent(poCode) + '/detail')
        .then(r => r.json())
        .then(data => {
            const po = data.po || {};
            document.getElementById('detailTitle').textContent = '발주 상세 — ' + poCode;
            document.getElementById('detailMeta').innerHTML =
                '<div><span style="color:#6b7280;">발주일</span><br><b>' + (po.orderDateStr || '-') + '</b></div>' +
                '<div><span style="color:#6b7280;">상태</span><br><b>' + (po.status || '-') + '</b></div>';
            const items = data.items || [];
            const itBody = document.getElementById('detailItems');
            itBody.innerHTML = items.length === 0
                ? '<tr><td colspan="4" style="padding:10px;color:#9ca3af;text-align:center;">품목 없음</td></tr>'
                : items.map(i => '<tr><td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;">' + (i.productName || i.productId) + '</td><td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (i.quantity||0).toLocaleString() + '</td><td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (i.unitPrice||0).toLocaleString() + '원</td><td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + ((i.unitPrice||0)*(i.quantity||0)).toLocaleString() + '원</td></tr>').join('');
            const receipts = data.receipts || [];
            const rcBody = document.getElementById('detailReceipts');
            rcBody.innerHTML = receipts.length === 0
                ? '<tr><td colspan="3" style="padding:10px;color:#9ca3af;text-align:center;">입고 내역 없음</td></tr>'
                : receipts.map(r => '<tr><td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;">' + (r.productName||r.productId) + '</td><td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (r.quantity||0).toLocaleString() + '</td><td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (r.receiptDate||'-') + '</td></tr>').join('');
            document.getElementById('poDetailModal').classList.add('open');
        });
}

function openCreate() { document.getElementById('receiptModal').classList.add('open'); }
function closeReceiptModal() { document.getElementById('receiptModal').classList.remove('open'); }
function saveReceipt() {
    const data = {
        poId: document.getElementById('rPoId').value,
        productId: document.getElementById('rProductId').value,
        quantity: document.getElementById('rQuantity').value,
        warehouseId: document.getElementById('rWarehouseId').value,
        receiptDate: document.getElementById('rReceiptDate').value
    };
    fetch('/api/receipts', { method:'POST', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify(data) })
        .then(res => {
            if (!res.ok) {
                const msg = res.headers.get('X-Error-Message') || '입고 등록에 실패했습니다.';
                alert(msg);
            } else {
                location.reload();
            }
        });
}
</script>
</body>
</html>
