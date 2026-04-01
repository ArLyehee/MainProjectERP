<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>매출 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="sales"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>매출 관리</h2><p>제품 매출 현황을 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 매출 등록</button></div><div class="search-bar"><input type="text" id="searchInput" placeholder="제품명, 판매일 검색..." oninput="filterTable()" class="search-input"></div><div class="card"><table id="salesTable"><thead><tr><th class="sort-th" onclick="sortTable(this,0,'str')">판매일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'num')">수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'num')">단가 (원)<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,4,'num')">총액 (원)<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,5,'num')">원가 (원)<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th></tr></thead><tbody><c:if test="${empty saleList}"><tr><td colspan="6" class="empty-state">등록된 매출이 없습니다.</td></tr></c:if><c:forEach var="s" items="${saleList}">
<tr><td>${s.saleDate}</td><td>${s.productName != null ? s.productName : s.productId}</td><td>${s.quantity}</td><td class="price"><fmt:formatNumber value="${s.unitPrice}" type="number" groupingUsed="true"/></td><td class="price"><fmt:formatNumber value="${s.totalPrice}" type="number" groupingUsed="true"/></td><td><fmt:formatNumber value="${s.costPrice}" type="number" groupingUsed="true"/></td></tr>
</c:forEach></tbody></table></div><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/sales?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/sales?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/sales?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><div class="modal-overlay" id="modalOverlay"><div class="modal"><h3>매출 등록</h3><div class="form-group"><label>제품 *</label><select id="productId" onchange="onProductChange()"><option value="">선택</option><c:forEach var="p" items="${productList}"><option value="${p.productId}" data-cost="${p.costPrice}">${p.productName}</option></c:forEach></select></div><div class="form-group"><label>수량 *</label><input type="number" id="quantity" placeholder="0" min="1"></div><div class="form-group"><label>단가 (원) *</label><input type="number" id="unitPrice" placeholder="0"></div><div class="form-group"><label>원가 (원)</label><input type="number" id="costPrice" placeholder="자동 입력"></div><div class="form-group"><label>창고</label><select id="warehouseId"><option value="">선택 안함</option><c:forEach var="w" items="${warehouseList}"><option value="${w.warehouseId}">${w.warehouseName}</option></c:forEach></select></div><div class="form-group"><label>판매일</label><input type="date" id="saleDate"></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveSale()">저장</button></div></div>
</div><script>
const csrf = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

function openCreate() {
    document.getElementById('productId').value = '';
    document.getElementById('quantity').value = '';
    document.getElementById('unitPrice').value = '';
    document.getElementById('costPrice').value = '';
    document.getElementById('warehouseId').value = '';
    document.getElementById('saleDate').value = '';
    document.getElementById('modalOverlay').classList.add('open');
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }

function onProductChange() {
    const sel = document.getElementById('productId');
    const opt = sel.options[sel.selectedIndex];
    const cost = opt ? opt.getAttribute('data-cost') : '';
    if (cost) document.getElementById('costPrice').value = cost;
}

function saveSale() {
    const data = {
        productId: document.getElementById('productId').value,
        quantity: document.getElementById('quantity').value,
        unitPrice: document.getElementById('unitPrice').value,
        costPrice: document.getElementById('costPrice').value || '0',
        warehouseId: document.getElementById('warehouseId').value || null,
        saleDate: document.getElementById('saleDate').value
    };
    fetch('/api/sales', { method:'POST', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify(data) })
        .then(res => {
            if (!res.ok) {
                const msg = res.headers.get('X-Error-Message') || '판매 등록에 실패했습니다.';
                alert(msg);
            } else {
                location.reload();
            }
        });
}

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
</script>
</body>
</html>
