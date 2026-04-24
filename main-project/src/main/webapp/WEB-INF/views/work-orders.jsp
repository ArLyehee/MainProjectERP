<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>작업지시 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="work-orders"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>작업지시 관리</h2><p>환풍기 생산 작업지시를 조회하고 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 작업지시 등록</button></div><div class="search-bar"><input type="text" id="searchInput" placeholder="제품명, 상태 검색..." oninput="filterTable()" class="search-input"></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'num')">생산 수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">시작일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty workOrderList}"><tr><td colspan="5" class="empty-state">등록된 작업지시가 없습니다.</td></tr></c:if><c:forEach var="w" items="${workOrderList}">
<tr style="cursor:pointer;" onclick="openDetail(${w.workOrderId})">
  <td>${w.productName != null ? w.productName : w.productId}</td>
  <td class="qty">${w.quantity}</td>
  <td>${w.startDate}</td>
  <td><c:if test="${w.status == '대기'}"><span class="badge badge-pending">대기</span></c:if><c:if test="${w.status == '진행중'}"><span class="badge badge-inprogress">진행중</span></c:if><c:if test="${w.status == '완료'}"><span class="badge badge-completed">완료</span></c:if><c:if test="${w.status == '취소'}"><span class="badge badge-cancelled">취소</span></c:if></td>
  <td onclick="event.stopPropagation()">
    <c:if test="${w.status == '대기'}"><button class="btn-action btn-start" data-id="${w.workOrderId}" data-s="진행중" onclick="changeStatus(this.dataset.id, this.dataset.s)">시작</button><button class="btn-action btn-auto-order" onclick="autoOrderParts(${w.workOrderId})">부품 자동발주</button></c:if>
    <c:if test="${w.status == '진행중'}"><button class="btn-action btn-complete" onclick="openCompleteModal(${w.workOrderId})">생산완료</button></c:if>
    <c:if test="${w.status == '대기' or w.status == '진행중'}"><button class="btn-action btn-cancel-w" data-id="${w.workOrderId}" data-s="취소" onclick="changeStatus(this.dataset.id, this.dataset.s)">취소</button></c:if>
  </td>
</tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/work-orders?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/work-orders?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/work-orders?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- 작업지시 상세 모달 -->
<div class="modal-overlay" id="woDetailModal">
<div class="modal" style="max-width:640px;">
  <h3 id="woDetailTitle">작업지시 상세</h3>
  <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:8px 16px;margin-bottom:16px;font-size:13px;" id="woDetailMeta"></div>

  <div style="margin-bottom:8px;font-weight:600;font-size:13px;">📦 필요 자재 현황</div>
  <table style="width:100%;border-collapse:collapse;font-size:12px;margin-bottom:16px;">
    <thead><tr style="background:#f3f4f6;">
      <th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">자재명</th>
      <th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">필요수량</th>
      <th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">현재재고</th>
      <th style="padding:6px 8px;text-align:center;border-bottom:1px solid #e5e7eb;">상태</th>
    </tr></thead>
    <tbody id="woDetailMaterials"></tbody>
  </table>

  <div style="margin-bottom:8px;font-weight:600;font-size:13px;">🛒 연관 발주</div>
  <table style="width:100%;border-collapse:collapse;font-size:12px;">
    <thead><tr style="background:#f3f4f6;">
      <th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">발주코드</th>
      <th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">거래처</th>
      <th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">수량</th>
      <th style="padding:6px 8px;text-align:center;border-bottom:1px solid #e5e7eb;">상태</th>
    </tr></thead>
    <tbody id="woDetailOrders"></tbody>
  </table>

  <div class="modal-footer"><button class="btn-cancel-modal" onclick="closeDetailModal()">닫기</button></div>
</div>
</div>

<!-- 자동발주 담당자 입력 모달 -->
<div class="modal-overlay" id="autoOrderModal">
<div class="modal" style="max-width:360px;">
  <h3>부품 자동발주</h3>
  <div class="form-group">
    <label>담당자명 *</label>
    <input type="text" id="autoOrderManagerName" placeholder="담당자 이름 입력">
  </div>
  <div class="modal-footer">
    <button class="btn-cancel-modal" onclick="closeAutoOrderModal()">취소</button>
    <button class="btn-save" onclick="confirmAutoOrder()">발주 생성</button>
  </div>
</div>
</div>

<!-- 입고 창고 선택 모달 (생산완료 시 완성품 입고창고) -->
<div class="modal-overlay" id="shipModal">
<div class="modal" style="max-width:360px;">
  <h3>완성품 입고 창고 선택</h3>
  <p style="font-size:12px;color:#6b7280;margin-bottom:12px;">완성품을 입고할 창고를 선택하세요.</p>
  <div class="form-group">
    <label>입고 창고 *</label>
    <select id="shipWarehouseId">
      <option value="">창고 선택</option>
      <c:forEach var="w" items="${warehouseList}">
        <option value="${w.warehouseId}">${w.warehouseName}</option>
      </c:forEach>
    </select>
  </div>
  <div class="modal-footer">
    <button class="btn-cancel-modal" onclick="closeShipModal()">취소</button>
    <button class="btn-save" onclick="confirmShip()">입고 처리</button>
  </div>
</div>
</div>

<!-- Hidden product select for JS -->
<select id="productSourceSelect" style="display:none"><option value="">제품 선택</option><c:forEach var="p" items="${productList}"><option value="${p.productId}">${p.productName}</option></c:forEach>
</select><!-- 생산 입고 처리 모달 -->
<div class="modal-overlay" id="prodReceiptModal"><div class="modal"><h3>생산 입고 처리</h3><input type="hidden" id="prWorkOrderId"><input type="hidden" id="prProductId"><div class="form-group"><label>수량 *</label><input type="number" id="prQuantity" placeholder="0"></div><div class="form-group"><label>입고 창고 *</label><select id="prWarehouseId"><option value="">선택</option><c:forEach var="w" items="${warehouseList}"><option value="${w.warehouseId}">${w.warehouseName}</option></c:forEach></select></div><div class="form-group"><label>입고일시</label><input type="datetime-local" id="prReceiptDate"></div><div class="modal-footer"><button class="btn-cancel" onclick="closePrModal()">취소</button><button class="btn-save" onclick="saveProdReceipt()">입고 처리</button></div></div>
</div><div class="modal-overlay" id="modalOverlay"><div class="modal"><h3>작업지시 등록</h3><div class="form-group"><label>제품 *</label><select id="productId" onchange="onProductOrQtyChange()"></select></div><div class="form-group"><label>생산 수량 *</label><input type="number" id="quantity" placeholder="0" min="1" oninput="onProductOrQtyChange()"></div><div id="stockCheckArea" style="margin:8px 0;display:none;"><div style="font-size:12px;font-weight:600;margin-bottom:6px;color:var(--text-muted);"> 자재 재고 현황</div><table style="width:100%;font-size:12px;border-collapse:collapse;" id="stockTable"><thead><tr style="background:var(--surface);"><th style="padding:4px 8px;text-align:left;">자재명</th><th style="padding:4px 8px;text-align:right;">필요</th><th style="padding:4px 8px;text-align:right;">보유</th><th style="padding:4px 8px;text-align:right;">부족</th></tr></thead><tbody id="stockTableBody"></tbody></table></div><div id="stockWarning" style="font-size:12px;color:#e74c3c;margin:4px 0 8px;display:none;"> 재고가 부족한 자재가 있습니다. 등록 시 재고가 음수가 될 수 있습니다.</div><div class="form-group"><label>시작일</label><input type="datetime-local" id="startDate"></div><div class="form-group"><label>상태</label><select id="status"><option value="대기">대기</option><option value="진행중">진행중</option></select></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveWorkOrder()">저장</button></div></div>
</div><script>
let _pendingCompleteWorkOrderId = null;
function openCompleteModal(id) {
    _pendingCompleteWorkOrderId = id;
    document.getElementById('shipWarehouseId').value = '';
    document.getElementById('shipModal').classList.add('open');
}
function closeShipModal() {
    document.getElementById('shipModal').classList.remove('open');
}
function confirmShip() {
    const warehouseId = document.getElementById('shipWarehouseId').value;
    if (!warehouseId) { alert('창고를 선택해주세요.'); return; }
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/work-orders/' + _pendingCompleteWorkOrderId + '/status', {
        method: 'PATCH',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify({status: '완료', warehouseId: parseInt(warehouseId)})
    }).then(r => r.json()).then(data => {
        document.getElementById('shipModal').classList.remove('open');
        if (data.success) {
            alert('생산완료! 완성품이 선택한 창고에 입고되었습니다.\n주문처리현황에서 출고 처리를 진행하세요.');
        } else {
            alert('오류: ' + (data.error || '처리 실패'));
        }
        location.reload();
    }).catch(() => alert('처리 중 오류가 발생했습니다.'));
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
                return `<tr style="\${short ? 'background:rgba(231,76,60,0.08);' : ''}"><td style="padding:4px 8px;">\${item.componentName}</td><td style="padding:4px 8px;text-align:right;">\${item.needed}</td><td style="padding:4px 8px;text-align:right;">\${item.available}</td><td style="padding:4px 8px;text-align:right;color:\${short ? '#e74c3c' : '#27ae60'};font-weight:600;">\${short ? '-' + item.shortage : '충족'}</td></tr>`;
            }).join('');
            warning.style.display = hasShortage ? 'block' : 'none';
        });
}
function openCreate() {
    const src = document.getElementById('productSourceSelect');
    const dst = document.getElementById('productId');
    dst.innerHTML = src.innerHTML;
    document.getElementById('quantity').value = '';
    const now = new Date();
    const pad = n => String(n).padStart(2, '0');
    document.getElementById('startDate').value =
        now.getFullYear() + '-' + pad(now.getMonth()+1) + '-' + pad(now.getDate()) +
        'T' + pad(now.getHours()) + ':' + pad(now.getMinutes());
    document.getElementById('status').value = '대기';
    document.getElementById('stockCheckArea').style.display = 'none';
    document.getElementById('stockWarning').style.display = 'none';
    document.getElementById('modalOverlay').classList.add('open');
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveWorkOrder() {
    const productId = document.getElementById('productId').value;
    const quantity  = document.getElementById('quantity').value;
    if (!productId) { alert('제품을 선택하세요.'); return; }
    if (!quantity || parseInt(quantity) < 1) { alert('수량을 입력하세요.'); return; }
    const data = {
        productId: parseInt(productId),
        quantity:  parseInt(quantity),
        startDate: document.getElementById('startDate').value || null,
        status:    document.getElementById('status').value
    };
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/work-orders', {
        method: 'POST',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify(data)
    }).then(r => {
        if (r.ok) { location.href = '/work-orders?page=1'; }
        else { r.text().then(t => alert('등록 실패: ' + r.status + '\n' + t)); }
    }).catch(e => alert('네트워크 오류: ' + e));
}
let _pendingAutoOrderId = null;
function autoOrderParts(id) {
    _pendingAutoOrderId = id;
    document.getElementById('autoOrderManagerName').value = '';
    document.getElementById('autoOrderModal').classList.add('open');
}
function closeAutoOrderModal() {
    document.getElementById('autoOrderModal').classList.remove('open');
}
function confirmAutoOrder() {
    const managerName = document.getElementById('autoOrderManagerName').value.trim();
    if (!managerName) { alert('담당자명을 입력해주세요.'); return; }
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/work-orders/' + _pendingAutoOrderId + '/auto-order-parts', {
        method: 'POST',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify({managerName: managerName})
    }).then(r => r.json()).then(data => {
        closeAutoOrderModal();
        if (data.created > 0) {
            alert(data.created + '건의 발주가 생성되었습니다.\n발주 관리에서 거래처를 지정해주세요.');
        } else {
            alert('부족한 부품이 없습니다.');
        }
        location.reload();
    });
}
function openDetail(id) {
    fetch('/api/work-orders/' + id + '/detail')
        .then(r => r.json())
        .then(data => {
            const wo = data.wo || {};
            const statusLabel = {'대기':'대기', '진행중':'진행중', '완료':'완료', '취소':'취소'};
            document.getElementById('woDetailTitle').textContent = (wo.productName || wo.productId) + ' — 작업지시 상세';
            document.getElementById('woDetailMeta').innerHTML =
                '<div><span style="color:#6b7280;font-size:11px;">제품</span><br><b>' + (wo.productName || '-') + '</b></div>' +
                '<div><span style="color:#6b7280;font-size:11px;">생산수량</span><br><b>' + (wo.quantity || '-') + '개</b></div>' +
                '<div><span style="color:#6b7280;font-size:11px;">상태</span><br><b>' + (statusLabel[wo.status] || wo.status || '-') + '</b></div>';

            const materials = data.materials || [];
            const matBody = document.getElementById('woDetailMaterials');
            if (materials.length === 0) {
                matBody.innerHTML = '<tr><td colspan="4" style="padding:10px;text-align:center;color:#9ca3af;">BOM 정보 없음</td></tr>';
            } else {
                matBody.innerHTML = materials.map(m => {
                    const short = m.shortage > 0;
                    return '<tr style="' + (short ? 'background:rgba(239,68,68,0.06);' : '') + '">' +
                        '<td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;">' + (m.componentName || '-') + '</td>' +
                        '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + m.needed + '</td>' +
                        '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + m.available + '</td>' +
                        '<td style="padding:6px 8px;text-align:center;border-bottom:1px solid #f3f4f6;">' +
                          (short ? '<span style="color:#ef4444;font-weight:600;">부족 ' + m.shortage + '</span>' : '<span style="color:#16a34a;">충족</span>') +
                        '</td></tr>';
                }).join('');
            }

            const orders = data.orders || [];
            const ordBody = document.getElementById('woDetailOrders');
            const poStatus = {PENDING:'대기', APPROVED:'승인', COMPLETED:'완료', CANCELLED:'취소', RECEIVED:'입고완료'};
            if (orders.length === 0) {
                ordBody.innerHTML = '<tr><td colspan="4" style="padding:10px;text-align:center;color:#9ca3af;">연관 발주 없음</td></tr>';
            } else {
                ordBody.innerHTML = orders.map(o =>
                    '<tr><td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;font-size:11px;color:#6b7280;">' + (o.poCode || '-') + '</td>' +
                    '<td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;">' + (o.supplierName || '미지정') + '</td>' +
                    '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (o.totalQuantity || '-') + '</td>' +
                    '<td style="padding:6px 8px;text-align:center;border-bottom:1px solid #f3f4f6;">' + (poStatus[o.status] || o.status) + '</td></tr>'
                ).join('');
            }

            document.getElementById('woDetailModal').classList.add('open');
        });
}
function closeDetailModal() { document.getElementById('woDetailModal').classList.remove('open'); }

function changeStatus(id, status) {
    const msgMap = {
        '진행중': '생산을 시작하시겠습니까?',
        '완료':   '생산완료 처리하시겠습니까?\n완성품이 재고현황에 자동 등록됩니다.',
        '취소':   '작업지시를 취소하시겠습니까?'
    };
    if (!confirm(msgMap[status] || '상태를 변경하시겠습니까?')) return;
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/work-orders/' + id + '/status', {
        method: 'PATCH',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify({status: status})
    }).then(r => r.json()).then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert('오류: ' + (data.error || '알 수 없는 오류가 발생했습니다.'));
        }
    }).catch(() => alert('네트워크 오류가 발생했습니다.'));
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
