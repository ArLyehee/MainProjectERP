<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>공급업체 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="suppliers"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>공급업체 관리</h2><p>부품 및 원자재 공급업체를 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 공급업체 등록</button></div><div class="search-bar"><input type="text" id="searchInput" value="${q}" placeholder="업체명, 연락처, 주소 검색..." class="search-input" onkeydown="if(event.key==='Enter'){location.href='/suppliers?q='+encodeURIComponent(this.value)+'&page=1';}"><button onclick="location.href='/suppliers?q='+encodeURIComponent(document.getElementById('searchInput').value)+'&page=1'" class="btn btn-secondary" style="margin-left:6px;">검색</button></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'num')">#<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">업체명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">연락처<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">주소<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty supplierList}"><tr><td colspan="5" class="empty-state">등록된 공급업체가 없습니다.</td></tr></c:if><c:forEach var="s" items="${supplierList}">
<tr><td>${s.supplierId}</td><td>${s.supplierName}</td><td class="phone">${s.phone}</td><td class="address">${s.address}</td><td><button class="btn-action btn-edit" onclick="openEdit(${s.supplierId})">수정</button><button class="btn-action btn-del"  onclick="confirmDelete(${s.supplierId})">삭제</button></td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/suppliers?page=${currentPage - 1}&q=${q}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/suppliers?page=${i}&q=${q}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/suppliers?page=${currentPage + 1}&q=${q}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><div class="modal-overlay" id="modalOverlay"><div class="modal"><h3 id="modalTitle">공급업체 등록</h3><input type="hidden" id="supplierId"><div style="display:grid;grid-template-columns:1fr 1fr;gap:0 16px;"><div class="form-group"><label>업체명 *</label><input type="text" id="supplierName" placeholder="(주)홍길동물산"></div><div class="form-group"><label>연락처</label><input type="text" id="phone" placeholder="02-0000-0000"></div><div class="form-group" style="grid-column:1/-1;"><label>주소</label><input type="text" id="address" placeholder="서울시 강남구 ..."></div></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveSupplier()">저장</button></div></div>
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
    document.getElementById('modalTitle').textContent = '공급업체 등록';
    document.getElementById('supplierId').value = '';
    document.getElementById('supplierName').value = '';
    document.getElementById('phone').value = '';
    document.getElementById('address').value = '';
    document.getElementById('modalOverlay').classList.add('open');
}
function openEdit(id) {
    fetch('/api/suppliers/' + id)
        .then(r => r.json())
        .then(s => {
            document.getElementById('modalTitle').textContent = '공급업체 수정';
            document.getElementById('supplierId').value = s.supplierId;
            document.getElementById('supplierName').value = s.supplierName || '';
            document.getElementById('phone').value = s.phone || '';
            document.getElementById('address').value = s.address || '';
            document.getElementById('modalOverlay').classList.add('open');
        });
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveSupplier() {
    const id = document.getElementById('supplierId').value;
    const data = {
        supplierName: document.getElementById('supplierName').value,
        phone: document.getElementById('phone').value,
        address: document.getElementById('address').value
    };
    if (!data.supplierName) { alert('업체명을 입력하세요.'); return; }
    const url = id ? '/api/suppliers/' + id : '/api/suppliers';
    const method = id ? 'PUT' : 'POST';
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch(url, { method, headers: {'Content-Type':'application/json', [csrfHeader]: csrf}, body: JSON.stringify(data) })
        .then(r => { if (r.ok) location.href = '/suppliers?page=1'; else r.text().then(t => alert('저장 실패: ' + t)); });
}
function confirmDelete(id) {
    if (confirm('정말 삭제하시겠습니까?')) {
        const csrf = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        fetch('/api/suppliers/' + id, { method: 'DELETE', headers: {[csrfHeader]: csrf} })
            .then(() => location.reload());
    }
}
</script>
</body>
</html>
