<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>발주 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="purchase-orders"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>발주 관리</h2><p>공급업체 발주 현황을 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 발주 등록</button></div><div class="search-bar"><input type="text" id="searchInput" value="${q}" placeholder="공급업체, 상태 검색..." class="search-input" onkeydown="if(event.key==='Enter'){location.href='/purchase-orders?q='+encodeURIComponent(this.value)+'&page=1';}"><button onclick="location.href='/purchase-orders?q='+encodeURIComponent(document.getElementById('searchInput').value)+'&page=1'" class="btn btn-secondary" style="margin-left:6px;">검색</button></div><div class="card"><table id="poTable"><thead><tr><th class="sort-th" onclick="sortTable(this,0,'str')">공급업체<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">발주일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'num')">품목 수<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty purchaseOrderList}"><tr><td colspan="5" class="empty-state">등록된 발주가 없습니다.</td></tr></c:if><c:forEach var="po" items="${purchaseOrderList}">
<tr style="cursor:pointer;" onclick="openDetail(${po.poId}, '${po.poCode}')"><td><div>${po.supplierName != null ? po.supplierName : (po.customerName != null ? po.customerName : '거래처 미지정')}</div><div class="sub-text">${po.poCode}</div></td><td>${po.orderDateStr}</td><td>${po.totalQuantity != null ? po.totalQuantity : (po.item != null ? po.item : '-')}</td><td><span class="badge badge-${fn:toLowerCase(po.status)}">${po.status == 'PENDING' ? '대기' : po.status == 'APPROVED' ? '승인' : po.status == 'RECEIVED' ? '입고완료' : po.status == 'COMPLETED' ? '완료' : '취소'}</span></td><td>
  <c:if test="${po.status == 'PENDING'}"><button class="btn-action btn-approve" onclick="event.stopPropagation();changePoStatus(${po.poId},'COMPLETED')">승인</button></c:if>
  <c:if test="${po.status == 'APPROVED'}"><span style="font-size:12px;color:#6b7280;">입고 대기중</span></c:if>
  <c:if test="${po.status == 'COMPLETED'}"><span style="font-size:12px;color:#9ca3af;">완료됨</span></c:if>
</td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/purchase-orders?page=${currentPage - 1}&q=${q}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/purchase-orders?page=${i}&q=${q}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/purchase-orders?page=${currentPage + 1}&q=${q}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- 발주 상세 모달 -->
<div class="modal-overlay" id="poDetailModal">
<div class="modal" style="max-width:620px;">
  <h3 id="detailTitle">발주 상세</h3>
  <div style="display:grid;grid-template-columns:1fr 1fr;gap:8px 24px;margin-bottom:16px;font-size:13px;" id="detailMeta"></div>
  <div style="margin-bottom:8px;font-weight:600;font-size:13px;">📦 발주 품목</div>
  <table style="width:100%;border-collapse:collapse;font-size:12px;margin-bottom:8px;">
    <thead><tr style="background:#f3f4f6;"><th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">품목명</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">수량</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">단가 (수정 가능)</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">금액</th></tr></thead>
    <tbody id="detailItems"></tbody>
  </table>
  <div style="text-align:right;margin-bottom:16px;"><button onclick="saveItemPrices()" style="background:#6366f1;color:#fff;border:none;border-radius:6px;padding:6px 16px;font-size:12px;cursor:pointer;">단가 저장</button></div>
  <div style="margin-bottom:8px;font-weight:600;font-size:13px;">✅ 입고 내역</div>
  <table style="width:100%;border-collapse:collapse;font-size:12px;">
    <thead><tr style="background:#f3f4f6;"><th style="padding:6px 8px;text-align:left;border-bottom:1px solid #e5e7eb;">품목명</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">입고 수량</th><th style="padding:6px 8px;text-align:right;border-bottom:1px solid #e5e7eb;">입고일</th></tr></thead>
    <tbody id="detailReceipts"></tbody>
  </table>
  <div class="modal-footer"><button class="btn-cancel-modal" onclick="closeDetailModal()">닫기</button></div>
</div>
</div>

<!-- 발주 등록 모달 -->
<div class="modal-overlay" id="poModal">
<div class="modal" style="max-width:560px;">
<h3>발주 등록</h3>
<div style="display:grid;grid-template-columns:1fr 1fr;gap:0 16px;">
  <div class="form-group">
    <label>공급업체 *</label>
    <select id="poSupplierId" onchange="loadSupplierProducts()">
      <option value="">선택</option>
      <c:forEach var="s" items="${supplierList}"><option value="${s.supplierId}">${s.supplierName}</option></c:forEach>
    </select>
  </div>
  <div class="form-group">
    <label>발주일 *</label>
    <input type="date" id="poOrderDate">
  </div>
</div>
<div class="form-group">
  <label>부품 선택 * <span style="font-size:11px;color:#9ca3af;">(공급업체 먼저 선택)</span></label>
  <div id="poItemRows">
    <div class="po-item-row" style="display:flex;gap:8px;align-items:center;margin-bottom:6px;">
      <select class="item-product-sel" style="flex:2;" onchange="fillUnitPrice(this)"><option value="">부품 선택</option></select>
      <input type="number" class="item-qty" placeholder="수량" min="1" style="width:80px;">
      <span class="item-price-label" style="font-size:11px;color:#9ca3af;white-space:nowrap;">단가: -</span>
    </div>
  </div>
  <button type="button" onclick="addItemRow()" style="font-size:12px;color:#6366f1;border:none;background:none;cursor:pointer;padding:4px 0;">+ 행 추가</button>
</div>
<div class="modal-footer"><button class="btn-cancel-modal" onclick="closePoModal()">취소</button><button class="btn-save" onclick="savePo()">저장</button></div>
</div>
</div>
<script>
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
// 거래처 변경 시 부품 목록 로드 (작업2)
let supplierProducts = [];
function loadSupplierProducts() {
    const supplierId = document.getElementById('poSupplierId').value;
    supplierProducts = [];
    // 기존 행 초기화
    document.querySelectorAll('.item-product-sel').forEach(sel => {
        sel.innerHTML = '<option value="">부품 선택</option>';
        sel.value = '';
    });
    document.querySelectorAll('.item-price-label').forEach(lbl => lbl.textContent = '단가: -');
    if (!supplierId) return;
    fetch('/api/purchase-orders/supplier/' + supplierId + '/products')
        .then(r => r.json())
        .then(products => {
            supplierProducts = products;
            document.querySelectorAll('.item-product-sel').forEach(sel => {
                fillProductOptions(sel);
            });
        });
}
function fillProductOptions(sel) {
    const prev = sel.value;
    sel.innerHTML = '<option value="">부품 선택</option>';
    supplierProducts.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.productId;
        opt.textContent = p.productName;
        opt.dataset.cost = p.costPrice || 0;
        sel.appendChild(opt);
    });
    // 이전 선택이 목록에 없으면 초기화
    if (prev && [...sel.options].some(o => o.value == prev)) sel.value = prev;
}
function fillUnitPrice(sel) {
    const opt = sel.options[sel.selectedIndex];
    const lbl = sel.parentElement.querySelector('.item-price-label');
    if (opt && opt.dataset.cost) {
        lbl.textContent = '단가: ' + Number(opt.dataset.cost).toLocaleString('ko-KR') + '원';
    } else {
        lbl.textContent = '단가: -';
    }
}
function addItemRow() {
    const row = document.createElement('div');
    row.className = 'po-item-row';
    row.style.cssText = 'display:flex;gap:8px;align-items:center;margin-bottom:6px;';
    row.innerHTML = '<select class="item-product-sel" style="flex:2;" onchange="fillUnitPrice(this)"><option value="">부품 선택</option></select><input type="number" class="item-qty" placeholder="수량" min="1" style="width:80px;"><span class="item-price-label" style="font-size:11px;color:#9ca3af;white-space:nowrap;">단가: -</span><button type="button" onclick="this.parentElement.remove()" style="border:none;background:none;color:#ef4444;cursor:pointer;font-size:14px;">✕</button>';
    document.getElementById('poItemRows').appendChild(row);
    const newSel = row.querySelector('.item-product-sel');
    fillProductOptions(newSel);
}
function openCreate() {
    document.getElementById('poSupplierId').value = '';
    document.getElementById('poOrderDate').value = '';
    supplierProducts = [];
    document.getElementById('poItemRows').innerHTML = '<div class="po-item-row" style="display:flex;gap:8px;align-items:center;margin-bottom:6px;"><select class="item-product-sel" style="flex:2;" onchange="fillUnitPrice(this)"><option value="">부품 선택</option></select><input type="number" class="item-qty" placeholder="수량" min="1" style="width:80px;"><span class="item-price-label" style="font-size:11px;color:#9ca3af;white-space:nowrap;">단가: -</span></div>';
    document.getElementById('poModal').classList.add('open');
}
function closePoModal() { document.getElementById('poModal').classList.remove('open'); }
function savePo() {
    const supplierId = document.getElementById('poSupplierId').value;
    const orderDate  = document.getElementById('poOrderDate').value;
    if (!supplierId) { alert('공급업체를 선택하세요.'); return; }
    if (!orderDate)  { alert('발주일을 입력하세요.'); return; }
    // 부품 항목 수집 (작업1)
    const items = [];
    document.querySelectorAll('.po-item-row').forEach(row => {
        const sel = row.querySelector('.item-product-sel');
        const qty = row.querySelector('.item-qty');
        if (sel && sel.value && qty && qty.value) {
            const opt = sel.options[sel.selectedIndex];
            items.push({ productId: parseInt(sel.value), quantity: parseInt(qty.value), unitPrice: parseFloat(opt.dataset.cost || 0) });
        }
    });
    if (items.length === 0) { alert('부품을 1개 이상 선택하세요.'); return; }
    const data = { supplierId: supplierId, orderDate: orderDate + 'T00:00:00', status: 'PENDING', item: items.length, items: items };
    fetch('/api/purchase-orders', { method:'POST', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify(data) })
        .then(r => {
            if (r.ok) { location.href = '/purchase-orders?page=1'; }
            else { r.json().then(j => alert('발주 등록 실패: ' + (j.error || r.status))).catch(() => alert('발주 등록 실패: ' + r.status)); }
        })
        .catch(e => alert('네트워크 오류: ' + e));
}
function openDetail(id, poCode) {
    fetch('/api/purchase-orders/' + id + '/detail')
        .then(r => r.json())
        .then(data => {
            const po = data.po || {};
            document.getElementById('detailTitle').textContent = '발주 상세 — ' + poCode;
            document.getElementById('detailMeta').innerHTML =
                '<div><span style="color:#6b7280;">발주일</span><br><b>' + (po.orderDateStr || '-') + '</b></div>' +
                '<div><span style="color:#6b7280;">상태</span><br><b>' + (po.status || '-') + '</b></div>';

            const items = data.items || [];
            const itBody = document.getElementById('detailItems');
            if (items.length === 0) {
                itBody.innerHTML = '<tr><td colspan="4" style="padding:10px;color:#9ca3af;text-align:center;">품목 없음</td></tr>';
            } else {
                itBody.innerHTML = items.map(i => {
                    const amt = (i.unitPrice || 0) * (i.quantity || 0);
                    return '<tr>' +
                        '<td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;">' + (i.productName || i.productId) + '</td>' +
                        '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (i.quantity || 0).toLocaleString() + '</td>' +
                        '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' +
                          '<input type="number" class="item-price-input" data-item-id="' + i.poItemId + '" data-qty="' + i.quantity + '" value="' + (i.unitPrice || 0) + '" min="0" style="width:90px;text-align:right;border:1px solid #d1d5db;border-radius:4px;padding:2px 6px;font-size:12px;" oninput="updateAmtCell(this)">' +
                        '</td>' +
                        '<td class="amt-cell" style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + amt.toLocaleString() + '원</td>' +
                        '</tr>';
                }).join('');
            }

            const receipts = data.receipts || [];
            const rcBody = document.getElementById('detailReceipts');
            if (receipts.length === 0) {
                rcBody.innerHTML = '<tr><td colspan="3" style="padding:10px;color:#9ca3af;text-align:center;">입고 내역 없음</td></tr>';
            } else {
                rcBody.innerHTML = receipts.map(r =>
                    '<tr><td style="padding:6px 8px;border-bottom:1px solid #f3f4f6;">' + (r.productName || r.productId) + '</td>' +
                    '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (r.quantity || 0).toLocaleString() + '</td>' +
                    '<td style="padding:6px 8px;text-align:right;border-bottom:1px solid #f3f4f6;">' + (r.receiptDate || '-') + '</td></tr>'
                ).join('');
            }
            document.getElementById('poDetailModal').classList.add('open');
        });
}
function closeDetailModal() { document.getElementById('poDetailModal').classList.remove('open'); }

function updateAmtCell(input) {
    const qty = parseFloat(input.dataset.qty) || 0;
    const price = parseFloat(input.value) || 0;
    const cell = input.closest('td').nextElementSibling;
    if (cell) cell.textContent = (qty * price).toLocaleString() + '원';
}

function saveItemPrices() {
    const inputs = document.querySelectorAll('.item-price-input');
    if (inputs.length === 0) return;
    const promises = Array.from(inputs).map(input => {
        const poItemId = input.dataset.itemId;
        const unitPrice = parseFloat(input.value) || 0;
        return fetch('/api/purchase-orders/items/' + poItemId + '/price', {
            method: 'PATCH',
            headers: {'Content-Type': 'application/json', [csrfHeader]: csrf},
            body: JSON.stringify({ unitPrice })
        });
    });
    Promise.all(promises).then(() => alert('단가가 저장되었습니다.'));
}

function assignSupplier(id) {
    const sel = document.getElementById('sup_' + id);
    const supplierId = sel ? sel.value : '';
    if (!supplierId) { alert('거래처를 선택하세요.'); return; }
    fetch('/api/purchase-orders/' + id + '/supplier', {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json', [csrfHeader]: csrf},
        body: JSON.stringify({ supplierId: parseInt(supplierId) })
    }).then(r => { if (r.ok) location.reload(); else alert('배정 실패'); });
}
function changePoStatus(id, status) {
    if (!confirm('상태를 변경하시겠습니까?')) return;
    fetch('/api/purchase-orders/' + id + '/status', { method:'PATCH', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify({status}) })
        .then(r => {
            if (r.ok) { location.reload(); }
            else { r.text().then(t => alert('승인 실패: ' + r.status + '\n' + t)); }
        })
        .catch(e => alert('네트워크 오류: ' + e));
}
</script>
</body>
</html>
