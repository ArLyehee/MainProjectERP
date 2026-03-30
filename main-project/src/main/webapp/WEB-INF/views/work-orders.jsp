<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>작업지시 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="work-orders"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>작업지시 관리</h2><p>환풍기 생산 작업지시를 조회하고 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 작업지시 등록</button></div><div class="search-bar"><input type="text" id="searchInput" placeholder="제품명, 상태 검색..." oninput="filterTable()" class="search-input"></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'num')">생산 수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">시작일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty workOrderList}"><tr><td colspan="5" class="empty-state">등록된 작업지시가 없습니다.</td></tr></c:if><c:forEach var="w" items="${workOrderList}">
<tr><td>${w.productName != null ? w.productName : w.productId}</td><td class="qty">${w.quantity}</td><td>${w.startDate}</td><td><c:if test="${w.status == 'PENDING'}"><span class="badge badge-pending">대기</span></c:if><c:if test="${w.status == 'IN_PROGRESS'}"><span class="badge badge-inprogress">진행중</span></c:if><c:if test="${w.status == 'COMPLETED'}"><span class="badge badge-completed">완료</span></c:if><c:if test="${w.status == 'CANCELLED'}"><span class="badge badge-cancelled">취소</span></c:if></td><td><c:if test="${w.status == 'PENDING'}"><button class="btn-action btn-start" onclick="changeStatus(${w.workOrderId}, 'IN_PROGRESS')">시작</button></c:if><c:if test="${w.status == 'IN_PROGRESS'}"><button class="btn-action btn-complete" onclick="changeStatus(${w.workOrderId}, 'COMPLETED')">완료</button></c:if><c:if test="${w.status == 'IN_PROGRESS'}"><button class="btn-action btn-receive" onclick="openProdReceipt(${w.workOrderId},${w.productId})">생산입고</button></c:if><c:if test="${w.status == 'PENDING' or w.status == 'IN_PROGRESS'}"><button class="btn-action btn-cancel-w" onclick="changeStatus(${w.workOrderId}, 'CANCELLED')">취소</button></c:if></td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/work-orders?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/work-orders?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/work-orders?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- Hidden product select for JS -->
<select id="productSourceSelect" style="display:none"><option value="">제품 선택</option><c:forEach var="p" items="${productList}"><option value="${p.productId}">${p.productName}</option></c:forEach>
</select><!-- 생산 입고 처리 모달 -->
<div class="modal-overlay" id="prodReceiptModal"><div class="modal"><h3>생산 입고 처리</h3><input type="hidden" id="prWorkOrderId"><input type="hidden" id="prProductId"><div class="form-group"><label>수량 *</label><input type="number" id="prQuantity" placeholder="0"></div><div class="form-group"><label>입고 창고 *</label><select id="prWarehouseId"><option value="">선택</option><c:forEach var="w" items="${warehouseList}"><option value="${w.warehouseId}">${w.warehouseName}</option></c:forEach></select></div><div class="form-group"><label>입고일시</label><input type="datetime-local" id="prReceiptDate"></div><div class="modal-footer"><button class="btn-cancel" onclick="closePrModal()">취소</button><button class="btn-save" onclick="saveProdReceipt()">입고 처리</button></div></div>
</div><div class="modal-overlay" id="modalOverlay"><div class="modal"><h3>작업지시 등록</h3><div class="form-group"><label>제품 *</label><select id="productId" onchange="onProductOrQtyChange()"></select></div><div class="form-group"><label>생산 수량 *</label><input type="number" id="quantity" placeholder="0" min="1" oninput="onProductOrQtyChange()"></div><div id="stockCheckArea" style="margin:8px 0;display:none;"><div style="font-size:12px;font-weight:600;margin-bottom:6px;color:var(--text-muted);"> 자재 재고 현황</div><table style="width:100%;font-size:12px;border-collapse:collapse;" id="stockTable"><thead><tr style="background:var(--surface);"><th style="padding:4px 8px;text-align:left;">자재명</th><th style="padding:4px 8px;text-align:right;">필요</th><th style="padding:4px 8px;text-align:right;">보유</th><th style="padding:4px 8px;text-align:right;">부족</th></tr></thead><tbody id="stockTableBody"></tbody></table></div><div id="stockWarning" style="font-size:12px;color:#e74c3c;margin:4px 0 8px;display:none;"> 재고가 부족한 자재가 있습니다. 등록 시 재고가 음수가 될 수 있습니다.</div><div class="form-group"><label>시작일</label><input type="datetime-local" id="startDate"></div><div class="form-group"><label>상태</label><select id="status"><option value="PENDING">대기 (PENDING)</option><option value="IN_PROGRESS">진행중 (IN_PROGRESS)</option></select></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveWorkOrder()">저장</button></div></div>
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
let stockCheckTimer = null;
function onProductOrQtyChange() {
    clearTimeout(stockCheckTimer);
    stockCheckTimer = setTimeout(doStockCheck, 400);
}
function doStockCheck() {
    const productId = document.getElementById('productId').value;
    const quantity = parseInt(document.getElementById('quantity').value);
    const area = document.getElementById('stockCheckArea');
    const warning = document.getElementById('stockWarning');
    if (!productId || !quantity || quantity < 1) { area.style.display='none'; warning.style.display='none'; return; }
    fetch('/api/work-orders/check-stock?productId=' + productId + '&quantity=' + quantity)
        .then(r => r.json())
        .then(list => {
            if (!list || list.length === 0) { area.style.display='none'; warning.style.display='none'; return; }
            area.style.display='block';
            let hasShortage = false;
            document.getElementById('stockTableBody').innerHTML = list.map(item => {
                const short = item.shortage > 0;
                if (short) hasShortage = true;
                return `<tr style="${short ? 'background:rgba(231,76,60,0.08);' : ''}"><td style="padding:4px 8px;">${item.componentName}</td><td style="padding:4px 8px;text-align:right;">${item.needed}</td><td style="padding:4px 8px;text-align:right;">${item.available}</td><td style="padding:4px 8px;text-align:right;color:${short ? '#e74c3c' : '#27ae60'};font-weight:600;">${short ? '-' + item.shortage : '충족'}</td></tr>`;
            }).join('');
            warning.style.display = hasShortage ? 'block' : 'none';
        });
}
function openCreate() {
    const src = document.getElementById('productSourceSelect');
    const dst = document.getElementById('productId');
    dst.innerHTML = src.innerHTML;
    document.getElementById('quantity').value = '';
    document.getElementById('startDate').value = '';
    document.getElementById('status').value = 'PENDING';
    document.getElementById('stockCheckArea').style.display = 'none';
    document.getElementById('stockWarning').style.display = 'none';
    document.getElementById('modalOverlay').classList.add('open');
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveWorkOrder() {
    const data = {
        productId: document.getElementById('productId').value || null,
        quantity: document.getElementById('quantity').value || null,
        startDate: document.getElementById('startDate').value || null,
        status: document.getElementById('status').value
    };
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/work-orders', {
        method: 'POST',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify(data)
    }).then(() => location.reload());
}
function changeStatus(id, status) {
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/work-orders/' + id + '/status', {
        method: 'PATCH',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify({status: status})
    }).then(() => location.reload());
}
function openProdReceipt(workOrderId, productId) {
    document.getElementById('prWorkOrderId').value = workOrderId;
    document.getElementById('prProductId').value = productId;
    document.getElementById('prQuantity').value = '';
    document.getElementById('prWarehouseId').value = '';
    document.getElementById('prReceiptDate').value = '';
    document.getElementById('prodReceiptModal').classList.add('open');
}
function closePrModal() { document.getElementById('prodReceiptModal').classList.remove('open'); }
function saveProdReceipt() {
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    const data = {
        workOrderId: document.getElementById('prWorkOrderId').value,
        productId: document.getElementById('prProductId').value,
        quantity: document.getElementById('prQuantity').value,
        warehouseId: document.getElementById('prWarehouseId').value,
        receiptDate: document.getElementById('prReceiptDate').value
    };
    fetch('/api/production-receipts', { method:'POST', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify(data) })
        .then(() => location.reload());
}
</script>
</body>
</html>
