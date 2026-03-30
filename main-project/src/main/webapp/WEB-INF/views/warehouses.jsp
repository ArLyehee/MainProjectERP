<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>창고 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="warehouses"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>창고 관리</h2><p>제품 보관 창고 현황을 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 창고 등록</button></div><div class="search-bar"><input type="text" id="searchInput" placeholder="창고명, 위치 검색..." oninput="filterTable()" class="search-input"></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'num')">#<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">창고명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">위치<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty warehouseList}"><tr><td colspan="4" class="empty-state">등록된 창고가 없습니다.</td></tr></c:if><c:forEach var="w" items="${warehouseList}">
<tr><td>${w.warehouseId}</td><td>${w.warehouseName}</td><td class="location">${w.location}</td><td><button class="btn-action btn-edit" onclick="openEdit(${w.warehouseId})">수정</button><button class="btn-action btn-del"  onclick="confirmDelete(${w.warehouseId})">삭제</button></td></tr>
</c:forEach></tbody></table></div></main>
</div><div class="modal-overlay" id="modalOverlay"><div class="modal"><h3 id="modalTitle">창고 등록</h3><input type="hidden" id="warehouseId"><div class="form-group"><label>창고명 *</label><input type="text" id="warehouseName" placeholder="창고명 입력"></div><div class="form-group"><label>위치</label><input type="text" id="location" placeholder="위치 입력"></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveWarehouse()">저장</button></div></div>
</div><script>
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
function openCreate() {
    document.getElementById('modalTitle').textContent = '창고 등록';
    document.getElementById('warehouseId').value = '';
    document.getElementById('warehouseName').value = '';
    document.getElementById('location').value = '';
    document.getElementById('modalOverlay').classList.add('open');
}
function openEdit(id) {
    fetch('/api/warehouses/' + id)
        .then(r => r.json())
        .then(w => {
            document.getElementById('modalTitle').textContent = '창고 수정';
            document.getElementById('warehouseId').value = w.warehouseId;
            document.getElementById('warehouseName').value = w.warehouseName || '';
            document.getElementById('location').value = w.location || '';
            document.getElementById('modalOverlay').classList.add('open');
        });
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveWarehouse() {
    const id = document.getElementById('warehouseId').value;
    const data = {
        warehouseName: document.getElementById('warehouseName').value,
        location: document.getElementById('location').value
    };
    const url = id ? '/api/warehouses/' + id : '/api/warehouses';
    const method = id ? 'PUT' : 'POST';
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch(url, { method, headers: {'Content-Type':'application/json', [csrfHeader]: csrf}, body: JSON.stringify(data) })
        .then(() => location.reload());
}
function confirmDelete(id) {
    if (confirm('정말 삭제하시겠습니까?')) {
        const csrf = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        fetch('/api/warehouses/' + id, { method: 'DELETE', headers: {[csrfHeader]: csrf} })
            .then(() => location.reload());
    }
}
</script>
</body>
</html>
