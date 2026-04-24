<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>출고 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="shipments"/>
</jsp:include><main class="main">
<div class="page-header">
  <div class="page-title"><h2>출고 관리</h2><p>창고별 출고 현황을 관리합니다.</p></div>
  <button onclick="openCreate()" class="btn btn-primary">+ 출고 등록</button>
</div>
<div class="search-bar"><input type="text" id="searchInput" placeholder="제품명, 창고, 배송지 검색..." oninput="filterTable()" class="search-input"></div>
<div class="card"><table><thead>
  <tr>
    <th class="sort-th" onclick="sortTable(this,0,'str')">출고 번호<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th class="sort-th" onclick="sortTable(this,1,'str')">제품명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th class="sort-th" onclick="sortTable(this,2,'str')">출고 창고<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th class="sort-th" onclick="sortTable(this,3,'num')">수량<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th class="sort-th" onclick="sortTable(this,4,'str')">배송지 주소<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th class="sort-th" onclick="sortTable(this,5,'str')">출고일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
  </tr>
</thead><tbody>
<c:if test="${empty shipmentList}"><tr><td colspan="6" class="empty-state">등록된 출고 내역이 없습니다.</td></tr></c:if>
<c:forEach var="s" items="${shipmentList}">
<tr>
  <td>SHP-${s.shipmentId}</td>
  <td>${s.productName != null ? s.productName : s.productId}</td>
  <td>${s.warehouseName != null ? s.warehouseName : s.warehouseId}</td>
  <td class="qty">${s.quantity}</td>
  <td>${s.destination}</td>
  <td>${s.shipmentDateStr}</td>
</tr>
</c:forEach>
</tbody></table></div>
<c:if test="${totalPages != null and totalPages > 1}">
<div class="pagination">
  <a href="/shipments?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a>
  <c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
    <a href="/shipments?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
  </c:forEach>
  <a href="/shipments?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a>
</div>
</c:if>
</main>
</div>

<!-- 출고 등록 모달 -->
<div class="modal-overlay" id="shipmentModal"><div class="modal">
  <h3>출고 등록</h3>

  <!-- 1단계: 창고 선택 -->
  <div class="form-group">
    <label>출고 창고 * <span style="font-size:11px;color:#9ca3af;">어느 창고에서 출고할지 먼저 선택하세요</span></label>
    <select id="sWarehouseId" onchange="onWarehouseChange()">
      <option value="">창고 선택</option>
      <c:forEach var="w" items="${warehouseList}">
        <option value="${w.warehouseId}">${w.warehouseName}</option>
      </c:forEach>
    </select>
  </div>

  <!-- 2단계: 제품 선택 (창고 선택 후 활성화) -->
  <div class="form-group">
    <label>판매 제품 * <span style="font-size:11px;color:#9ca3af;">선택한 창고의 보유 제품만 표시</span></label>
    <select id="sProductId" onchange="onProductChange()" disabled>
      <option value="">창고를 먼저 선택하세요</option>
    </select>
  </div>

  <!-- 재고 정보 -->
  <div id="stockInfo" style="display:none;margin-bottom:12px;padding:8px 12px;border-radius:6px;font-size:13px;font-weight:600;"></div>

  <!-- 수량 -->
  <div class="form-group">
    <label>판매 수량 *</label>
    <input type="number" id="sQuantity" placeholder="0" min="1" disabled>
  </div>

  <!-- 배송지 주소 -->
  <div class="form-group">
    <label>배송지 주소 * <span style="font-size:11px;color:#9ca3af;">판매처(수신자) 주소</span></label>
    <input type="text" id="sDestination" placeholder="예: 서울특별시 강남구 테헤란로 123">
  </div>

  <!-- 출고일 -->
  <div class="form-group">
    <label>출고일</label>
    <input type="date" id="sShipmentDate">
  </div>

  <div class="modal-footer">
    <button class="btn-cancel-modal" onclick="closeModal()">취소</button>
    <button class="btn-save" onclick="saveShipment()">출고 등록</button>
  </div>
</div></div>

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

// 창고별 재고 캐시
let _warehouseInventory = [];

async function onWarehouseChange() {
    const warehouseId = document.getElementById('sWarehouseId').value;
    const productSel  = document.getElementById('sProductId');
    const qtyInput    = document.getElementById('sQuantity');
    const stockBox    = document.getElementById('stockInfo');

    productSel.innerHTML = '<option value="">불러오는 중...</option>';
    productSel.disabled = true;
    qtyInput.disabled   = true;
    stockBox.style.display = 'none';

    if (!warehouseId) {
        productSel.innerHTML = '<option value="">창고를 먼저 선택하세요</option>';
        return;
    }

    const res  = await fetch('/api/inventory/by-warehouse/' + warehouseId);
    _warehouseInventory = await res.json();

    productSel.innerHTML = '<option value="">제품 선택</option>';
    if (_warehouseInventory.length === 0) {
        productSel.innerHTML = '<option value="">이 창고에 재고가 없습니다</option>';
        return;
    }
    _warehouseInventory.forEach(item => {
        const opt = document.createElement('option');
        opt.value       = item.productId;
        opt.textContent = item.productName + ' (' + (item.quantity || 0).toLocaleString() + '개 보유)';
        opt.dataset.qty = item.quantity || 0;
        productSel.appendChild(opt);
    });
    productSel.disabled = false;
    qtyInput.disabled   = false;
}

function onProductChange() {
    const productId = document.getElementById('sProductId').value;
    const stockBox  = document.getElementById('stockInfo');
    if (!productId) { stockBox.style.display = 'none'; return; }

    const item = _warehouseInventory.find(i => String(i.productId) === productId);
    const qty  = item ? (item.quantity || 0) : 0;

    stockBox.style.display = 'block';
    if (qty <= 0) {
        stockBox.style.background = '#fee2e2'; stockBox.style.color = '#991b1b';
        stockBox.textContent = '⚠ 재고 없음 (0개)';
    } else if (qty <= 10) {
        stockBox.style.background = '#fef3c7'; stockBox.style.color = '#92400e';
        stockBox.textContent = '⚠ 재고 부족: ' + qty.toLocaleString() + '개';
    } else {
        stockBox.style.background = '#d1fae5'; stockBox.style.color = '#065f46';
        stockBox.textContent = '✓ 보유 재고: ' + qty.toLocaleString() + '개';
    }
}

function openCreate() {
    document.getElementById('sWarehouseId').value = '';
    document.getElementById('sProductId').innerHTML = '<option value="">창고를 먼저 선택하세요</option>';
    document.getElementById('sProductId').disabled  = true;
    document.getElementById('sQuantity').value      = '';
    document.getElementById('sQuantity').disabled   = true;
    document.getElementById('sDestination').value   = '';
    const today = new Date().toISOString().substring(0, 10);
    document.getElementById('sShipmentDate').value  = today;
    document.getElementById('stockInfo').style.display = 'none';
    _warehouseInventory = [];
    document.getElementById('shipmentModal').classList.add('open');
}
function closeModal() { document.getElementById('shipmentModal').classList.remove('open'); }

function saveShipment() {
    const warehouseId = document.getElementById('sWarehouseId').value;
    const productId   = document.getElementById('sProductId').value;
    const quantity    = parseInt(document.getElementById('sQuantity').value);
    const destination = document.getElementById('sDestination').value.trim();
    const shipmentDate= document.getElementById('sShipmentDate').value;

    if (!warehouseId)      { alert('출고 창고를 선택하세요.'); return; }
    if (!productId)        { alert('판매할 제품을 선택하세요.'); return; }
    if (!quantity || quantity <= 0) { alert('수량을 입력하세요.'); return; }
    if (!destination)      { alert('배송지 주소를 입력하세요.'); return; }

    const data = { productId, warehouseId, quantity, destination, shipmentDate };
    fetch('/api/shipments', {
        method: 'POST',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify(data)
    }).then(res => {
        if (!res.ok) {
            const msg = res.headers.get('X-Error-Message') || '출고 등록에 실패했습니다.';
            alert(msg);
        } else {
            location.reload();
        }
    });
}
</script>
</body>
</html>
