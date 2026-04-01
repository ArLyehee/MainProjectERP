<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>재고 현황 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="inventory"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>재고 현황</h2><p>창고별 제품 재고를 확인합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 재고 등록</button></div><!-- 재고 부족 알림 배너 --><c:if test="${!empty shortageList}">
<div style="background:rgba(231,76,60,0.08);border:1px solid rgba(231,76,60,0.3);border-radius:8px;padding:14px 18px;margin-bottom:16px;"><div style="font-size:13px;font-weight:700;color:#c0392b;margin-bottom:8px;">재고 부족 항목 <span style="background:#e74c3c;color:#fff;border-radius:10px;padding:1px 8px;font-size:11px;">${fn:length(shortageList)}</span><span style="font-size:11px;font-weight:400;color:#888;margin-left:8px;">기준: ${lowStockThreshold}개 미만</span></div><div style="display:flex;flex-wrap:wrap;gap:8px;"><c:forEach var="s" items="${shortageList}">
<span style="${s.quantity <= 0 ? 'background:#e74c3c;color:#fff;font-size:12px;padding:3px 10px;border-radius:12px;' : 'background:rgba(231,76,60,0.12);color:#c0392b;font-size:12px;padding:3px 10px;border-radius:12px;border:1px solid rgba(231,76,60,0.4);'}">${s.productName}(${s.warehouseName})
                    <b>${s.quantity}</b>개
                    <c:if test="${s.quantity <= 0}"><span style="font-weight:700;"> 음수</span></c:if></span></c:forEach></div></div>
</c:if><div class="search-bar"><input type="text" id="searchInput" value="${q}" placeholder="제품명, 창고 검색..." class="search-input" onkeydown="if(event.key==='Enter'){location.href='/inventory?q='+encodeURIComponent(this.value)+'&page=1';}"><button onclick="location.href='/inventory?q='+encodeURIComponent(document.getElementById('searchInput').value)+'&page=1'" class="btn btn-secondary" style="margin-left:6px;">검색</button></div><div class="card"><table id="inventoryTable"><thead><tr><th class="sort-th" onclick="sortTable(this,0,'num')">#<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">창고<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'num')">수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,4,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,5,'str')">최종 수정<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody id="inventoryTableBody"><c:if test="${empty inventoryList}"><tr><td colspan="7" class="empty-state">등록된 재고가 없습니다.</td></tr></c:if><c:forEach var="inv" items="${inventoryList}">
<tr style="${inv.quantity <= 0 ? 'background:rgba(231,76,60,0.07);' : (inv.quantity < lowStockThreshold ? 'background:rgba(255,183,77,0.08);' : '')}"><td>${inv.inventoryId}</td><td>${inv.productName != null ? inv.productName : inv.productId}</td><td>${inv.warehouseName != null ? inv.warehouseName : inv.warehouseId}</td><td class="qty" style="${inv.quantity <= 0 ? 'color:#e74c3c;font-weight:700;' : ''}">${inv.quantity}</td><td><c:if test="${inv.quantity <= 0}"><span class="badge badge-empty">재고없음</span></c:if><c:if test="${inv.quantity > 0 and inv.quantity < lowStockThreshold}"><span class="badge badge-low">부족</span></c:if><c:if test="${inv.quantity >= lowStockThreshold}"><span class="badge badge-ok">정상</span></c:if></td><td>${inv.lastUpdate}</td><td><button class="btn-action btn-edit" onclick="openEdit(${inv.inventoryId})">수정</button><button class="btn-action btn-del"  onclick="confirmDelete(${inv.inventoryId})">삭제</button></td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/inventory?page=${currentPage - 1}&q=${q}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/inventory?page=${i}&q=${q}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/inventory?page=${currentPage + 1}&q=${q}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- 재고 등록 모달 -->
<div class="modal-overlay" id="modalOverlay"><div class="modal"><h3 id="modalTitle">재고 등록</h3><input type="hidden" id="inventoryId"><div class="form-group"><label>제품 *</label><select id="invProductId"><option value="">제품 선택</option><c:forEach var="p" items="${productList}"><option value="${p.productId}">${p.productName}</option></c:forEach></select></div><div class="form-group"><label>창고 *</label><select id="invWarehouseId"><option value="">창고 선택</option><c:forEach var="w" items="${warehouseList}"><option value="${w.warehouseId}">${w.warehouseName}</option></c:forEach></select></div><div class="form-group"><label>수량 *</label><input type="number" id="invQuantity" placeholder="수량 입력" min="0"></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveInventory()">저장</button></div></div>
</div><script>
const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.content;
const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.content;

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
    document.querySelectorAll('#inventoryTableBody tr').forEach(tr => {
        tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
}

function openCreate() {
    document.getElementById('modalTitle').textContent = '재고 등록';
    document.getElementById('inventoryId').value = '';
    document.getElementById('invProductId').value = '';
    document.getElementById('invWarehouseId').value = '';
    document.getElementById('invQuantity').value = '';
    document.getElementById('modalOverlay').classList.add('open');
}

function openEdit(id) {
    fetch('/api/inventory/' + id)
        .then(r => r.json())
        .then(d => {
            document.getElementById('modalTitle').textContent = '재고 수정';
            document.getElementById('inventoryId').value = d.inventoryId;
            document.getElementById('invProductId').value = d.productId;
            document.getElementById('invWarehouseId').value = d.warehouseId;
            document.getElementById('invQuantity').value = d.quantity;
            document.getElementById('modalOverlay').classList.add('open');
        });
}

function closeModal() {
    document.getElementById('modalOverlay').classList.remove('open');
}

function saveInventory() {
    const id = document.getElementById('inventoryId').value;
    const productId   = document.getElementById('invProductId').value;
    const warehouseId = document.getElementById('invWarehouseId').value;
    const quantity    = document.getElementById('invQuantity').value;
    if (!productId || !warehouseId || quantity === '') { alert('제품, 창고, 수량을 모두 입력하세요.'); return; }
    const body = {
        productId:   parseInt(productId),
        warehouseId: parseInt(warehouseId),
        quantity:    parseInt(quantity)
    };
    const url    = id ? '/api/inventory/' + id : '/api/inventory';
    const method = id ? 'PUT' : 'POST';
    fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json', [CSRF_HEADER]: CSRF_TOKEN },
        body: JSON.stringify(body)
    }).then(r => {
        if (r.ok) { location.href = '/inventory?page=1'; }
        else { r.text().then(t => alert('저장 실패: ' + r.status + '\n' + t)); }
    }).catch(e => alert('네트워크 오류: ' + e));
}

function confirmDelete(id) {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    fetch('/api/inventory/' + id, {
        method: 'DELETE',
        headers: { [CSRF_HEADER]: CSRF_TOKEN }
    }).then(r => { if (r.ok) location.reload(); });
}
</script>
</body>
</html>
