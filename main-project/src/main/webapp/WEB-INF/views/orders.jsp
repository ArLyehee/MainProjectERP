<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">
<title>주문 처리 현황 | 개발팬 ERP</title>
<link rel="stylesheet" href="/css/erp.css?v=18">
<style>
/* ── 주문 상태 배지 ── */
.badge-pending       { background:#fef3c7; color:#92400e; border:1px solid #f59e0b; }
.badge-hold          { background:#fee2e2; color:#991b1b; border:1px solid #f87171; }
.badge-in-production { background:#dbeafe; color:#1e3a8a; border:1px solid #60a5fa; }
.badge-ordered       { background:#ede9fe; color:#3730a3; border:1px solid #a78bfa; }
.badge-ready         { background:#d1fae5; color:#065f46; border:1px solid #34d399; }
.badge-shipped       { background:#e5e7eb; color:#374151; border:1px solid #9ca3af; }

/* ── 플로우 진행바 ── */
.flow-bar {
    display: flex;
    gap: 0;
    margin-bottom: 20px;
    border-radius: 10px;
    overflow: hidden;
    border: 1px solid #e2e8f0;
}
.flow-step {
    flex: 1;
    padding: 10px 6px;
    text-align: center;
    font-size: 11px;
    font-weight: 600;
    background: #f8fafc;
    color: #94a3b8;
    position: relative;
    cursor: default;
}
.flow-step.active  { background: #3b82f6; color: #fff; }
.flow-step.done    { background: #d1fae5; color: #065f46; }
.flow-step.hold-s  { background: #fee2e2; color: #991b1b; }
.flow-step span.count {
    display: block;
    font-size: 18px;
    font-weight: 700;
    line-height: 1.2;
}


/* ── 결과 토스트 ── */
#toast {
    position: fixed; bottom: 28px; right: 28px; z-index: 9999;
    padding: 12px 20px; border-radius: 8px;
    font-size: 13px; font-weight: 600;
    box-shadow: 0 4px 16px rgba(0,0,0,.15);
    display: none;
    max-width: 320px;
}
#toast.success { background:#16a34a; color:#fff; }
#toast.info    { background:#0ea5e9; color:#fff; }
#toast.warn    { background:#f59e0b; color:#fff; }
</style>
</head>
<body>
<div class="layout">
<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="orders"/>
</jsp:include>
<main class="main">

    <div class="page-header">
        <div class="page-title">
            <h2>주문 처리 현황</h2>
            <p>고객 주문 접수 → 검토 → 재고 확인 → 생산/출고 → 완료 전체 흐름을 관리합니다.</p>
        </div>
        <button onclick="openCreate()" class="btn btn-primary">+ 주문 등록</button>
    </div>

    <!-- 진행 현황 요약 -->
    <div class="flow-bar" id="flowBar">
        <div class="flow-step" id="fs-PENDING">
            <span class="count" id="cnt-PENDING">-</span>검토 대기
        </div>
        <div class="flow-step" id="fs-HOLD">
            <span class="count" id="cnt-HOLD">-</span>보류
        </div>
        <div class="flow-step" id="fs-IN_PRODUCTION">
            <span class="count" id="cnt-IN_PRODUCTION">-</span>생산 중
        </div>
        <div class="flow-step" id="fs-ORDERED">
            <span class="count" id="cnt-ORDERED">-</span>발주 처리
        </div>
        <div class="flow-step" id="fs-READY">
            <span class="count" id="cnt-READY">-</span>출고 준비
        </div>
        <div class="flow-step" id="fs-SHIPPED">
            <span class="count" id="cnt-SHIPPED">-</span>출고 완료
        </div>
    </div>

    <!-- 검색 -->
    <div class="search-bar">
        <input type="text" id="searchInput" placeholder="고객명, 제품명, 주문번호 검색..." oninput="filterTable()" class="search-input">
    </div>

    <!-- 주문 목록 -->
    <div class="card">
        <table>
            <thead>
                <tr>
                    <th class="sort-th" onclick="sortTable(this,0,'str')">주문번호<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
                    <th class="sort-th" onclick="sortTable(this,1,'str')">고객명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
                    <th class="sort-th" onclick="sortTable(this,2,'str')">제품<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
                    <th class="sort-th" onclick="sortTable(this,3,'num')">수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
                    <th class="sort-th" onclick="sortTable(this,4,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
                    <th class="sort-th" onclick="sortTable(this,5,'str')">등록일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
                    <th>액션</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty orderList}">
                <tr>
                    <td colspan="7" class="empty-state">등록된 주문이 없습니다.</td>
                </tr>
                </c:if>
                <c:forEach var="o" items="${orderList}">
                <tr data-status="${o.status}">
                    <td>${o.orderNo}</td>
                    <td>${o.customerName}</td>
                    <td>${o.productName != null ? o.productName : o.productId}</td>
                    <td class="qty">${o.quantity}</td>
                    <td>
                        <c:if test="${o.status == 'PENDING'}"><span class="badge badge-pending">검토 대기</span></c:if>
                        <c:if test="${o.status == 'HOLD'}"><span class="badge badge-hold">보류</span></c:if>
                        <c:if test="${o.status == 'IN_PRODUCTION'}"><span class="badge badge-in-production">생산 중</span></c:if>
                        <c:if test="${o.status == 'ORDERED'}"><span class="badge badge-ordered">발주 처리</span></c:if>
                        <c:if test="${o.status == 'READY'}"><span class="badge badge-ready">출고 준비</span></c:if>
                        <c:if test="${o.status == 'SHIPPED'}"><span class="badge badge-shipped">출고 완료</span></c:if>
                    </td>
                    <td>${o.createdAtStr}</td>
                    <td>
                        <!-- PENDING: 수락 / 보류 -->
                        <c:if test="${o.status == 'PENDING'}">
                        <button class="btn-action btn-approve" onclick="approveOrder(${o.orderId})">수락</button>
                        <button class="btn-action btn-hold" onclick="holdOrder(${o.orderId})">보류</button>
                        </c:if>

                        <!-- HOLD: 재검토 -->
                        <c:if test="${o.status == 'HOLD'}">
                        <button class="btn-action btn-reopen" onclick="reopenOrder(${o.orderId})">재검토</button>
                        </c:if>

                        <!-- IN_PRODUCTION: 취소만 가능 (출고 준비는 작업지시 완료 시 자동) -->
                        <c:if test="${o.status == 'IN_PRODUCTION'}">
                        <button class="btn-action btn-cancel-w" onclick="cancelOrder(${o.orderId})">취소</button>
                        </c:if>

                        <!-- ORDERED: 취소 -->
                        <c:if test="${o.status == 'ORDERED'}">
                        <button class="btn-action btn-cancel-w" onclick="cancelOrder(${o.orderId})">취소</button>
                        </c:if>

                        <!-- READY: 출고 처리 -->
                        <c:if test="${o.status == 'READY'}">
                        <button class="btn-action btn-ship" onclick="shipOrder(${o.orderId})">출고 처리</button>
                        </c:if>

                        <!-- 연계 정보 링크 -->
                        <c:if test="${o.workOrderId != null}">
                        <a href="/work-orders" class="btn-action btn-reopen">작업지시↗</a>
                        </c:if>
                        <c:if test="${o.purchaseOrderId != null}">
                        <a href="/purchase-orders" class="btn-action btn-auto-order">발주↗</a>
                        </c:if>
                        <c:if test="${o.shipmentId != null}">
                        <a href="/shipments" class="btn-action btn-ship">출고↗</a>
                        </c:if>
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 페이지네이션 -->
    <c:if test="${totalPages != null and totalPages > 1}">
    <div class="pagination">
        <a href="/orders?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a>
        <c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
            <a href="/orders?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
        </c:forEach>
        <a href="/orders?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a>
    </div>
    </c:if>

</main>
</div>

<!-- 주문 등록 모달 -->
<div class="modal-overlay" id="modalOverlay">
    <div class="modal">
        <h3>주문 등록</h3>
        <div class="form-group">
            <label>고객명 *</label>
            <input type="text" id="customerName" placeholder="고객사명">
        </div>
        <div class="form-group">
            <label>제품 *</label>
            <select id="productId">
                <option value="">제품 선택</option>
                <c:forEach var="p" items="${productList}"><option value="${p.productId}">${p.productName}</option></c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label>수량 *</label>
            <input type="number" id="quantity" placeholder="0" min="1">
        </div>
        <div class="form-group">
            <label>단가 (원)</label>
            <input type="number" id="unitPrice" placeholder="0">
        </div>
        <div class="form-group">
            <label>비고</label>
            <input type="text" id="notes" placeholder="메모">
        </div>
        <div class="modal-footer">
            <button class="btn-cancel" onclick="closeModal()">취소</button>
            <button class="btn-save" onclick="saveOrder()">저장</button>
        </div>
    </div>
</div>

<!-- 토스트 -->
<div id="toast"></div>

<script>
// ── 유틸 ──────────────────────────────────────────
function csrf()       { return document.querySelector('meta[name="_csrf"]').content; }
function csrfHeader() { return document.querySelector('meta[name="_csrf_header"]').content; }

function showToast(msg, type='success') {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.className = type;
    t.style.display = 'block';
    setTimeout(() => { t.style.display = 'none'; }, 3000);
}

// ── 현황 집계 ─────────────────────────────────────
(function buildSummary() {
    const counts = {};
    document.querySelectorAll('tbody tr[data-status]').forEach(tr => {
        const s = tr.dataset.status;
        counts[s] = (counts[s] || 0) + 1;
    });
    ['PENDING','HOLD','IN_PRODUCTION','ORDERED','READY','SHIPPED'].forEach(s => {
        const el = document.getElementById('cnt-' + s);
        if (el) el.textContent = counts[s] || 0;
    });
    // 활성 단계 강조
    const activeMap = {PENDING:'active', HOLD:'hold-s', IN_PRODUCTION:'active', ORDERED:'active', READY:'active', SHIPPED:'done'};
    Object.keys(counts).forEach(s => {
        const el = document.getElementById('fs-' + s);
        if (el && counts[s] > 0) el.classList.add(activeMap[s] || 'active');
    });
})();

// ── 검색 ──────────────────────────────────────────
function filterTable() {
    const q = document.getElementById('searchInput').value.toLowerCase();
    document.querySelectorAll('tbody tr').forEach(tr => {
        tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none';
    });
}

// ── 정렬 ──────────────────────────────────────────
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
        const cmp = type === 'num'
            ? (parseFloat(va.replace(/,/g,''))||0) - (parseFloat(vb.replace(/,/g,''))||0)
            : va.localeCompare(vb, 'ko');
        return _sort.asc ? cmp : -cmp;
    });
    rows.forEach(r => tbody.appendChild(r));
}

// ── 모달 ──────────────────────────────────────────
function openCreate() { document.getElementById('modalOverlay').classList.add('open'); }
function closeModal()  { document.getElementById('modalOverlay').classList.remove('open'); }

function saveOrder() {
    const data = {
        customerName: document.getElementById('customerName').value,
        productId:    document.getElementById('productId').value || null,
        quantity:     parseInt(document.getElementById('quantity').value) || null,
        unitPrice:    parseFloat(document.getElementById('unitPrice').value) || null,
        notes:        document.getElementById('notes').value
    };
    if (!data.customerName || !data.productId || !data.quantity) {
        showToast('고객명, 제품, 수량은 필수입니다.', 'warn'); return;
    }
    fetch('/api/orders', {
        method: 'POST',
        headers: {'Content-Type':'application/json', [csrfHeader()]: csrf()},
        body: JSON.stringify(data)
    }).then(r => {
        if (r.ok) location.reload();
        else showToast('저장 실패', 'warn');
    });
}

// ── 주문 액션 ─────────────────────────────────────
function approveOrder(id) {
    if (!confirm('주문을 수락하고 처리하시겠습니까?\n재고가 충분하면 즉시 출고, 부족하면 작업지시가 생성됩니다.')) return;
    fetch('/api/orders/' + id + '/approve', {
        method: 'PATCH',
        headers: {[csrfHeader()]: csrf()}
    }).then(r => r.json()).then(data => {
        const msgMap = {
            SHIPPED:       '재고 충분 → 즉시 출고 처리 완료! 매출이 자동 등록되었습니다.',
            IN_PRODUCTION: '재고 부족 → 작업지시가 생성되었습니다. 부족 부품은 작업지시에서 자동 발주하세요.'
        };
        showToast(msgMap[data.status] || '처리 완료', 'success');
        setTimeout(() => location.reload(), 1500);
    }).catch(() => showToast('처리 중 오류가 발생했습니다.', 'warn'));
}

function holdOrder(id) {
    fetch('/api/orders/' + id + '/hold', {
        method: 'PATCH',
        headers: {[csrfHeader()]: csrf()}
    }).then(r => { if (r.ok) location.reload(); });
}

function reopenOrder(id) {
    fetch('/api/orders/' + id + '/reopen', {
        method: 'PATCH',
        headers: {[csrfHeader()]: csrf()}
    }).then(r => { if (r.ok) location.reload(); });
}

function markReady(id) {
    if (!confirm('출고 준비 상태로 전환하시겠습니까?')) return;
    fetch('/api/orders/' + id + '/ready', {
        method: 'PATCH',
        headers: {[csrfHeader()]: csrf()}
    }).then(r => {
        if (r.ok) { showToast('출고 준비 상태로 전환되었습니다.', 'success'); setTimeout(() => location.reload(), 1500); }
        else showToast('처리 오류', 'warn');
    });
}

function cancelOrder(id) {
    if (!confirm('주문을 취소하시겠습니까?')) return;
    fetch('/api/orders/' + id + '/cancel', {
        method: 'PATCH',
        headers: {[csrfHeader()]: csrf()}
    }).then(r => {
        if (r.ok) { showToast('주문이 취소되었습니다.', 'info'); setTimeout(() => location.reload(), 1500); }
        else showToast('처리 오류', 'warn');
    });
}

function shipOrder(id) {
    if (!confirm('출고 처리하시겠습니까? 매출이 자동 등록됩니다.')) return;
    fetch('/api/orders/' + id + '/ship', {
        method: 'PATCH',
        headers: {[csrfHeader()]: csrf()}
    }).then(r => {
        if (r.ok) { showToast('출고 완료! 매출 자동 등록됨.', 'success'); setTimeout(() => location.reload(), 1500); }
        else r.json().then(err => showToast('오류: ' + (err.error || '처리 실패'), 'warn'))
                     .catch(() => showToast('처리 오류 (HTTP ' + r.status + ')', 'warn'));
    });
}
</script>
</body>
</html>
