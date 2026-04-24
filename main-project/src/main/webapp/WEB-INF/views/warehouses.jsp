<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>창고 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
<style>
.wh-stats{display:flex;gap:8px;margin-top:6px;flex-wrap:wrap;}
.wh-stat{font-size:11px;padding:3px 9px;border-radius:12px;background:rgba(37,99,235,.1);color:#93c5fd;border:1px solid rgba(37,99,235,.2);}
.type-badge{display:inline-block;padding:2px 10px;border-radius:12px;font-size:12px;font-weight:600;}
.type-material{background:#fef3c7;color:#92400e;border:1px solid #f59e0b;}
.type-finished{background:#dbeafe;color:#1e3a8a;border:1px solid #60a5fa;}
.inv-table{width:100%;border-collapse:collapse;margin-top:0;}
.inv-table th{padding:9px 12px;font-size:12px;font-weight:600;text-align:left;border-bottom:2px solid #e5e7eb;color:#6b7280;background:#f9fafb;}
.inv-table td{padding:10px 12px;font-size:13px;border-bottom:1px solid #f3f4f6;}
.inv-table tr:last-child td{border-bottom:none;}
.inv-table tr:hover td{background:#f9fafb;}
.qty-badge{display:inline-block;padding:2px 10px;border-radius:12px;font-size:12px;font-weight:600;}
.qty-ok{background:#d1fae5;color:#065f46;}
.qty-low{background:#fef3c7;color:#92400e;}
.qty-zero{background:#fee2e2;color:#991b1b;}
.detail-modal{max-width:680px!important;}
.detail-header{display:flex;align-items:center;gap:12px;margin-bottom:20px;padding-bottom:16px;border-bottom:1px solid #f3f4f6;}
.detail-wh-name{font-size:20px;font-weight:700;color:#111;}
.detail-meta{font-size:12px;color:#9ca3af;}
.empty-inv{text-align:center;padding:32px;color:#9ca3af;font-size:13px;}
</style>
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="warehouses"/>
</jsp:include><main class="main">
<div class="page-header">
  <div class="page-title"><h2>창고 관리</h2><p>창고별 재고 현황을 관리합니다.</p></div>
  <button onclick="openCreate()" class="btn btn-primary">+ 창고 등록</button>
</div>
<div class="search-bar"><input type="text" id="searchInput" placeholder="창고명 검색..." oninput="filterTable()" class="search-input"></div>
<div class="card"><table><thead>
  <tr>
    <th class="sort-th" onclick="sortTable(this,0,'num')">#<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th class="sort-th" onclick="sortTable(this,1,'str')">창고명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
    <th>창고 구분</th>
    <th>등록일</th>
    <th>액션</th>
  </tr>
</thead><tbody>
<c:if test="${empty warehouseList}"><tr><td colspan="6" class="empty-state">등록된 창고가 없습니다.</td></tr></c:if>
<c:forEach var="w" items="${warehouseList}">
<tr>
  <td>${w.warehouseId}</td>
  <td><strong>${w.warehouseName}</strong></td>
  <td>
    <c:choose>
      <c:when test="${w.warehouseType == '자재창고'}">
        <span class="type-badge type-material">자재창고</span>
      </c:when>
      <c:otherwise>
        <span class="type-badge type-finished">완제품창고</span>
      </c:otherwise>
    </c:choose>
  </td>
  <td style="font-size:12px;color:#9ca3af;">${w.createdAtStr}</td>
  <td>
    <button class="btn-action" style="background:#eff6ff;color:#2563eb;border-color:#bfdbfe;" onclick="openDetail(${w.warehouseId}, '${w.warehouseName}')">재고보기</button>
    <button class="btn-action btn-edit" onclick="openEdit(${w.warehouseId})">수정</button>
    <button class="btn-action btn-del"  onclick="confirmDelete(${w.warehouseId})">삭제</button>
  </td>
</tr>
</c:forEach>
</tbody></table></div>
</main>
</div>

<!-- 창고 등록/수정 모달 -->
<div class="modal-overlay" id="modalOverlay"><div class="modal">
  <h3 id="modalTitle">창고 등록</h3>
  <input type="hidden" id="warehouseId">
  <div class="form-group"><label>창고명 *</label><input type="text" id="warehouseName" placeholder="예: D창고"></div>
  <div class="form-group">
    <label>창고 구분 *</label>
    <select id="warehouseType">
      <option value="완제품창고">완제품창고</option>
      <option value="자재창고">자재창고</option>
    </select>
  </div>
  <div class="modal-footer">
    <button class="btn-cancel" onclick="closeModal()">취소</button>
    <button class="btn-save" onclick="saveWarehouse()">저장</button>
  </div>
</div></div>

<!-- 창고 재고 상세 모달 -->
<div class="modal-overlay" id="detailOverlay"><div class="modal detail-modal">
  <div class="detail-header">
    <div>
      <div class="detail-wh-name" id="detailWhName">A창고</div>
      <div class="detail-meta" id="detailWhMeta"></div>
    </div>
    <button class="btn-cancel" style="margin-left:auto;" onclick="closeDetail()">닫기</button>
  </div>
  <div id="detailBody">
    <div class="empty-inv">불러오는 중...</div>
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
function openCreate() {
    document.getElementById('modalTitle').textContent = '창고 등록';
    document.getElementById('warehouseId').value = '';
    document.getElementById('warehouseName').value = '';
    document.getElementById('warehouseType').value = '완제품창고';
    document.getElementById('modalOverlay').classList.add('open');
}
function openEdit(id) {
    fetch('/api/warehouses/' + id)
        .then(r => r.json())
        .then(w => {
            document.getElementById('modalTitle').textContent = '창고 수정';
            document.getElementById('warehouseId').value = w.warehouseId;
            document.getElementById('warehouseName').value = w.warehouseName || '';
            document.getElementById('warehouseType').value = w.warehouseType || '완제품창고';
            document.getElementById('modalOverlay').classList.add('open');
        });
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveWarehouse() {
    const id = document.getElementById('warehouseId').value;
    const data = {
        warehouseName: document.getElementById('warehouseName').value,
        warehouseType: document.getElementById('warehouseType').value
    };
    if (!data.warehouseName.trim()) { alert('창고명을 입력하세요.'); return; }
    const url = id ? '/api/warehouses/' + id : '/api/warehouses';
    const method = id ? 'PUT' : 'POST';
    fetch(url, { method, headers: {'Content-Type':'application/json', [csrfHeader]: csrf}, body: JSON.stringify(data) })
        .then(() => location.reload());
}
function confirmDelete(id) {
    if (confirm('정말 삭제하시겠습니까?')) {
        fetch('/api/warehouses/' + id, { method: 'DELETE', headers: {[csrfHeader]: csrf} })
            .then(() => location.reload());
    }
}

// 창고 재고 상세
function openDetail(warehouseId, warehouseName) {
    document.getElementById('detailWhName').textContent = warehouseName;
    document.getElementById('detailWhMeta').textContent = '';
    document.getElementById('detailBody').innerHTML = '<div class="empty-inv">불러오는 중...</div>';
    document.getElementById('detailOverlay').classList.add('open');

    fetch('/api/inventory/by-warehouse/' + warehouseId)
        .then(r => r.json())
        .then(items => {
            const totalKinds = items.length;
            const totalQty   = items.reduce((s, i) => s + (i.quantity || 0), 0);
            document.getElementById('detailWhMeta').textContent =
                '총 ' + totalKinds + '종 · ' + totalQty.toLocaleString() + '개 보관 중';

            if (items.length === 0) {
                document.getElementById('detailBody').innerHTML =
                    '<div class="empty-inv">이 창고에 보관 중인 재고가 없습니다.</div>';
                return;
            }
            let html = '<table class="inv-table"><thead><tr>' +
                '<th>제품명</th><th>보유 수량</th><th>최종 업데이트</th><th>최초 입고일</th>' +
                '</tr></thead><tbody>';
            items.forEach(i => {
                const qty = i.quantity || 0;
                const qtyClass = qty <= 0 ? 'qty-zero' : qty <= 10 ? 'qty-low' : 'qty-ok';
                const lastUpd  = i.lastUpdate ? i.lastUpdate.substring(0, 10) : '-';
                const created  = i.createdAt  ? i.createdAt.substring(0, 10)  : '-';
                html += '<tr>' +
                    '<td><strong>' + (i.productName || '-') + '</strong></td>' +
                    '<td><span class="qty-badge ' + qtyClass + '">' + qty.toLocaleString() + '개</span></td>' +
                    '<td style="font-size:12px;color:#9ca3af;">' + lastUpd + '</td>' +
                    '<td style="font-size:12px;color:#9ca3af;">' + created + '</td>' +
                    '</tr>';
            });
            html += '</tbody></table>';
            document.getElementById('detailBody').innerHTML = html;
        })
        .catch(() => {
            document.getElementById('detailBody').innerHTML =
                '<div class="empty-inv">데이터를 불러오지 못했습니다.</div>';
        });
}
function closeDetail() { document.getElementById('detailOverlay').classList.remove('open'); }
</script>
</body>
</html>
