<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>근태 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="attendance"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>근태 관리</h2><p>직원 출퇴근 현황을 조회하고 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 출근 등록</button><button onclick="openLeave()" class="btn btn-secondary" style="margin-left:8px;">+ 연차 등록</button></div><div class="search-bar"><input type="text" class="search-input" id="searchInput" placeholder="직원명, 날짜, 출퇴근 상태 검색..." oninput="filterTable()"></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'str')">직원명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">날짜<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">출근<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">퇴근<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,4,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>액션</th></tr></thead><tbody><c:if test="${empty attendanceList}"><tr><td colspan="6" class="empty-state">등록된 근태 기록이 없습니다.</td></tr></c:if><c:forEach var="a" items="${attendanceList}">
<tr><td>${a.employeeName}</td><td>${a.workDate}</td><td>${a.checkIn != null ? a.checkIn : '-'}</td><td>${a.checkOut != null ? a.checkOut : '-'}</td><td><c:if test="${a.leaveType == 'ANNUAL'}"><span class="badge" style="background:#8e44ad;color:#fff;">연차</span></c:if><c:if test="${a.leaveType == 'SICK'}"><span class="badge" style="background:#e67e22;color:#fff;">병가</span></c:if><c:if test="${a.leaveType == 'HALF'}"><span class="badge" style="background:#2980b9;color:#fff;">반차</span></c:if><c:if test="${a.leaveType == null and a.checkOut != null}"><span class="badge badge-present">퇴근완료</span></c:if><c:if test="${a.leaveType == null and a.checkOut == null}"><span class="badge badge-absent">근무중</span></c:if></td><td><c:if test="${a.leaveType == null and a.checkOut == null}"><button class="btn-action btn-checkout" onclick="checkOut(${a.attendanceId})">퇴근</button></c:if></td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/attendance?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/attendance?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/attendance?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- 연차 등록 모달 -->
<div class="modal-overlay" id="leaveModal"><div class="modal"><h3>연차 등록</h3><div class="form-group"><label>직원 *</label><select id="lEmployeeId"><option value="">직원 선택</option><c:forEach var="e" items="${employeeList}"><option value="${e.employeeId}">${e.name}</option></c:forEach></select></div><div class="form-group"><label>날짜 *</label><input type="date" id="leaveDate"></div><div class="form-group"><label>유형</label><select id="leaveType"><option value="ANNUAL">연차</option><option value="HALF">반차</option><option value="SICK">병가</option></select></div><div class="modal-footer"><button class="btn-cancel-modal" onclick="closeLeaveModal()">취소</button><button class="btn-save" onclick="saveLeave()">등록</button></div></div>
</div>
<!-- 출근 등록 모달 -->
<div class="modal-overlay" id="attModal"><div class="modal"><h3>출근 등록</h3><div class="form-group"><label>직원 *</label><select id="aEmployeeId"><option value="">직원 선택</option><c:forEach var="e" items="${employeeList}"><option value="${e.employeeId}">${e.name}</option></c:forEach></select></div><div class="modal-footer"><button class="btn-cancel-modal" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveCheckIn()">출근 등록</button></div></div>
</div><script>
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

function openCreate() { document.getElementById('attModal').classList.add('open'); }
function closeModal() { document.getElementById('attModal').classList.remove('open'); }
function openLeave() {
    document.getElementById('lEmployeeId').value = '';
    document.getElementById('leaveDate').value = new Date().toISOString().slice(0,10);
    document.getElementById('leaveType').value = 'ANNUAL';
    document.getElementById('leaveModal').classList.add('open');
}
function closeLeaveModal() { document.getElementById('leaveModal').classList.remove('open'); }
function saveLeave() {
    const data = {
        employeeId: document.getElementById('lEmployeeId').value,
        leaveDate: document.getElementById('leaveDate').value,
        leaveType: document.getElementById('leaveType').value
    };
    if (!data.employeeId) { alert('직원을 선택하세요.'); return; }
    if (!data.leaveDate) { alert('날짜를 입력하세요.'); return; }
    fetch('/api/attendance/leave', { method:'POST', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify(data) })
        .then(res => {
            if (!res.ok) {
                const msg = res.headers.get('X-Error-Message') || '연차 등록에 실패했습니다.';
                alert(msg);
            } else {
                location.reload();
            }
        });
}

function saveCheckIn() {
    const data = { employeeId: document.getElementById('aEmployeeId').value };
    if (!data.employeeId) { alert('직원을 선택하세요.'); return; }
    fetch('/api/attendance/check-in', { method:'POST', headers:{'Content-Type':'application/json',[csrfHeader]:csrf}, body:JSON.stringify(data) })
        .then(res => {
            if (!res.ok) {
                const msg = res.headers.get('X-Error-Message') || '출근 등록에 실패했습니다.';
                alert(msg);
            } else {
                location.reload();
            }
        });
}

function checkOut(id) {
    fetch('/api/attendance/' + id + '/check-out', { method:'PATCH', headers:{[csrfHeader]:csrf} })
        .then(() => location.reload());
}
</script>
</body>
</html>
