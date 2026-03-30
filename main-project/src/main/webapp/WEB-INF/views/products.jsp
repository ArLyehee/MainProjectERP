<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>제품 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="products"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>제품 관리</h2><p>환풍기 제품 목록을 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 제품 등록</button></div><div class="search-bar"><input type="text" id="searchInput" placeholder="제품명, 모델명 검색..." oninput="filterTable()" class="search-input"></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'num')">#<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">모델명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'num')">판매가 (원)<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,4,'num')">원가 (원)<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,5,'str')">설명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty productList}"><tr><td colspan="7" class="empty-state">등록된 제품이 없습니다.</td></tr></c:if><c:forEach var="p" items="${productList}">
<tr><td>${p.productId}</td><td>${p.productName}</td><td class="model">${p.model}</td><td class="price"><fmt:formatNumber value="${p.salePrice}" type="number" groupingUsed="true"/></td><td class="price"><fmt:formatNumber value="${p.costPrice}" type="number" groupingUsed="true"/></td><td class="desc">${p.description}</td><td><button class="btn-action btn-edit" onclick="openEdit(${p.productId})">수정</button><button class="btn-action btn-del"  onclick="confirmDelete(${p.productId})">삭제</button></td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/products?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/products?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/products?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><div class="modal-overlay" id="modalOverlay"><div class="modal"><h3 id="modalTitle">제품 등록</h3><input type="hidden" id="productId"><div class="form-group"><label>제품명 *</label><input type="text" id="productName" placeholder="제품명 입력"></div><div class="form-group"><label>모델명</label><input type="text" id="model" placeholder="모델명 입력"></div><div class="form-group"><label>판매가 (원)</label><input type="number" id="salePrice" placeholder="0"></div><div class="form-group"><label>원가 (원)</label><input type="number" id="costPrice" placeholder="0"></div><div class="form-group"><label>설명</label><textarea id="description" placeholder="제품 설명 입력"></textarea></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveProduct()">저장</button></div></div>
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
function openCreate() {
    document.getElementById('modalTitle').textContent = '제품 등록';
    document.getElementById('productId').value = '';
    document.getElementById('productName').value = '';
    document.getElementById('model').value = '';
    document.getElementById('salePrice').value = '';
    document.getElementById('costPrice').value = '';
    document.getElementById('description').value = '';
    document.getElementById('modalOverlay').classList.add('open');
}
function openEdit(id) {
    fetch('/api/products/' + id)
        .then(r => r.json())
        .then(p => {
            document.getElementById('modalTitle').textContent = '제품 수정';
            document.getElementById('productId').value = p.productId;
            document.getElementById('productName').value = p.productName || '';
            document.getElementById('model').value = p.model || '';
            document.getElementById('salePrice').value = p.salePrice || '';
            document.getElementById('costPrice').value = p.costPrice || '';
            document.getElementById('description').value = p.description || '';
            document.getElementById('modalOverlay').classList.add('open');
        });
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveProduct() {
    const id = document.getElementById('productId').value;
    const data = {
        productName: document.getElementById('productName').value,
        model: document.getElementById('model').value,
        salePrice: document.getElementById('salePrice').value || null,
        costPrice: document.getElementById('costPrice').value || null,
        description: document.getElementById('description').value
    };
    const url = id ? '/api/products/' + id : '/api/products';
    const method = id ? 'PUT' : 'POST';
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch(url, { method, headers: {'Content-Type':'application/json', [csrfHeader]: csrf}, body: JSON.stringify(data) })
        .then(() => location.reload());
}
function filterTable() {
    const q = document.getElementById('searchInput').value.toLowerCase();
    document.querySelectorAll('tbody tr').forEach(tr => {
        tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
}
function confirmDelete(id) {
    if (confirm('정말 삭제하시겠습니까?')) {
        const csrf = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        fetch('/api/products/' + id, { method: 'DELETE', headers: {[csrfHeader]: csrf} })
            .then(() => location.reload());
    }
}
</script>
</body>
</html>
