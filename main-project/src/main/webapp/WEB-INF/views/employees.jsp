<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="_csrf" content="${_csrf.token}"><meta name="_csrf_header" content="${_csrf.headerName}"><title>직원 관리 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=18">
</head>
<body>
<div class="layout"><jsp:include page="/WEB-INF/views/fragments/sidebar.jsp">
  <jsp:param name="current" value="employees"/>
</jsp:include><main class="main"><div class="page-header"><div class="page-title"><h2>직원 관리</h2><p>임직원 정보를 조회하고 관리합니다.</p></div><button onclick="openCreate()" class="btn btn-primary">+ 직원 등록</button></div><div class="search-bar"><input type="text" id="searchInput" value="${q}" placeholder="이름, 부서, 직급, 이메일 검색..." class="search-input" onkeydown="if(event.key==='Enter'){location.href='/employees?page=1&q='+encodeURIComponent(this.value)+'&sort=${sort}&dir=${dir}';}"><button onclick="location.href='/employees?page=1&q='+encodeURIComponent(document.getElementById('searchInput').value)+'&sort=${sort}&dir=${dir}'" class="btn btn-secondary" style="margin-left:6px;">검색</button></div><div class="card"><table><thead><tr><th class="sort-th" onclick="sortTable(this,0,'num')">#<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,1,'str')">이름<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,2,'str')">부서 / 직급<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,3,'str')">이메일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,4,'str')">연락처<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,5,'str')">입사일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th class="sort-th" onclick="sortTable(this,6,'str')">상태<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th><th>연차 잔여</th><th>액션</th></tr></thead><tbody><c:if test="${empty employeeList}"><tr><td colspan="8" class="empty-state">등록된 직원이 없습니다.</td></tr></c:if><c:forEach var="e" items="${employeeList}">
<tr style="cursor:pointer;" onclick="openDetail(${e.employeeId})"><td>${e.employeeId}</td><td>${e.name}</td><td><div>${e.departmentName}</div><div class="sub-text">${e.positionName}</div></td><td class="sub-text">${e.email}</td><td class="sub-text">${e.phone}</td><td class="sub-text">${e.hireDate}</td><td><c:if test="${e.status == 'ACTIVE'}"><span class="badge badge-active">재직</span></c:if><c:if test="${e.status == 'RESIGNED'}"><span class="badge badge-resigned">퇴직</span></c:if><c:if test="${e.status == 'INACTIVE'}"><span class="badge badge-resigned">비활성</span></c:if></td><td class="sub-text">${e.remaining != null ? '잔여 '.concat(e.remaining).concat('일 (사용 ').concat(e.annualLeave).concat('일)') : '-'}</td><td onclick="event.stopPropagation()"><button class="btn-action btn-edit" onclick="openEdit(${e.employeeId})">수정</button><c:if test="${e.status == 'ACTIVE'}"><button class="btn-action btn-status" onclick="updateStatus(${e.employeeId}, 'INACTIVE')">비활성화</button></c:if><c:if test="${e.status != 'ACTIVE'}"><button class="btn-action btn-edit" onclick="updateStatus(${e.employeeId}, 'ACTIVE')">활성화</button></c:if></td></tr>
</c:forEach></tbody></table></div><!-- 페이지네이션 --><c:if test="${totalPages != null and totalPages > 1}"><div class="pagination"><a href="/employees?page=${currentPage - 1}&q=${q}&sort=${sort}&dir=${dir}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a><c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/employees?page=${i}&q=${q}&sort=${sort}&dir=${dir}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach><a href="/employees?page=${currentPage + 1}&q=${q}&sort=${sort}&dir=${dir}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a></div></c:if></main>
</div><!-- Hidden Thymeleaf-rendered selects for JS to read -->
<select id="deptSourceSelect" style="display:none"><option value="">부서 선택</option><c:forEach var="d" items="${departmentList}"><option value="${d.departmentId}">${d.departmentName}</option></c:forEach>
</select>
<select id="posSourceSelect" style="display:none"><option value="">직급 선택</option><c:forEach var="p" items="${positionList}"><option value="${p.positionId}">${p.positionName}</option></c:forEach>
</select><!-- 상세 모달 -->
<div class="modal-overlay" id="detailOverlay">
  <div class="modal" style="max-width:520px;">
    <h3>직원 상세</h3>
    <div style="display:flex;gap:24px;align-items:flex-start;margin-bottom:16px;">
      <div style="flex-shrink:0;text-align:center;">
        <img id="detailPhoto" src="" alt="사진" style="width:100px;height:100px;border-radius:50%;object-fit:cover;background:#2a2a3e;display:block;margin-bottom:8px;" onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><circle cx=%2250%22 cy=%2250%22 r=%2250%22 fill=%22%232a2a3e%22/><circle cx=%2250%22 cy=%2238%22 r=%2218%22 fill=%22%236b7280%22/><ellipse cx=%2250%22 cy=%2285%22 rx=%2230%22 ry=%2220%22 fill=%22%236b7280%22/></svg>'">
        <label style="cursor:pointer;font-size:12px;color:#7c8cf8;">
          📷 사진 변경
          <input type="file" id="photoInput" accept="image/*" style="display:none;" onchange="uploadPhoto()">
        </label>
      </div>
      <div style="flex:1;display:grid;grid-template-columns:1fr 1fr;gap:8px 16px;font-size:13px;">
        <div><span style="color:#888;">이름</span><br><b id="detailName"></b></div>
        <div><span style="color:#888;">상태</span><br><span id="detailStatus"></span></div>
        <div><span style="color:#888;">부서</span><br><span id="detailDept"></span></div>
        <div><span style="color:#888;">직급</span><br><span id="detailPos"></span></div>
        <div><span style="color:#888;">이메일</span><br><span id="detailEmail"></span></div>
        <div><span style="color:#888;">연락처</span><br><span id="detailPhone"></span></div>
        <div><span style="color:#888;">입사일</span><br><span id="detailHire"></span></div>
        <div><span style="color:#888;">급여</span><br><span id="detailSalary"></span></div>
        <div><span style="color:#888;">연차 잔여</span><br><span id="detailLeave"></span></div>
      </div>
    </div>
    <div class="modal-footer">
      <button class="btn-cancel" onclick="closeDetail()">닫기</button>
      <button class="btn-save" id="detailEditBtn" onclick="">수정</button>
    </div>
  </div>
</div>
<div class="modal-overlay" id="modalOverlay"><div class="modal"><h3 id="modalTitle">직원 등록</h3><input type="hidden" id="employeeId"><div class="form-row"><div class="form-group"><label>이름 *</label><input type="text" id="name" placeholder="이름 입력"></div><div class="form-group"><label>이메일</label><input type="email" id="email" placeholder="이메일 입력"></div></div><div class="form-row"><div class="form-group"><label>전화번호</label><input type="text" id="phone" placeholder="전화번호 입력"></div><div class="form-group"><label>입사일</label><input type="date" id="hireDate"></div></div><div class="form-row"><div class="form-group"><label>부서</label><select id="departmentId"></select></div><div class="form-group"><label>직급</label><select id="positionId"></select></div></div><div class="form-row"><div class="form-group"><label>급여 (원)</label><input type="number" id="salary" placeholder="0"></div><div class="form-group"><label>상태</label><select id="status"><option value="ACTIVE">재직 (ACTIVE)</option><option value="INACTIVE">비활성 (INACTIVE)</option><option value="RESIGNED">퇴직 (RESIGNED)</option></select></div></div><div class="modal-footer"><button class="btn-cancel" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveEmployee()">저장</button></div></div>
</div><script>
let _currentDetailId = null;
function openDetail(id) {
    _currentDetailId = id;
    fetch('/api/employees/' + id)
        .then(r => r.json())
        .then(e => {
            const defaultAvatar = "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><circle cx='50' cy='50' r='50' fill='%232a2a3e'/><circle cx='50' cy='38' r='18' fill='%236b7280'/><ellipse cx='50' cy='85' rx='30' ry='20' fill='%236b7280'/></svg>";
            document.getElementById('detailPhoto').src = e.photoPath || defaultAvatar;
            document.getElementById('detailName').textContent = e.name || '';
            document.getElementById('detailDept').textContent = e.departmentName || '-';
            document.getElementById('detailPos').textContent = e.positionName || '-';
            document.getElementById('detailEmail').textContent = e.email || '-';
            document.getElementById('detailPhone').textContent = e.phone || '-';
            document.getElementById('detailHire').textContent = e.hireDate || '-';
            document.getElementById('detailSalary').textContent = e.salary ? Number(e.salary).toLocaleString() + '원' : '-';
            const statusMap = {ACTIVE:'재직', RESIGNED:'퇴직', INACTIVE:'비활성'};
            document.getElementById('detailStatus').textContent = statusMap[e.status] || e.status;
            const leave = e.remaining != null ? '잔여 ' + e.remaining + '일 (사용 ' + (e.annualLeave||0) + '일)' : '-';
            document.getElementById('detailLeave').textContent = leave;
            document.getElementById('detailEditBtn').onclick = () => { closeDetail(); openEdit(id); };
            document.getElementById('detailOverlay').classList.add('open');
        });
}
function closeDetail() { document.getElementById('detailOverlay').classList.remove('open'); }
function uploadPhoto() {
    const file = document.getElementById('photoInput').files[0];
    if (!file || !_currentDetailId) return;
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    const form = new FormData();
    form.append('file', file);
    fetch('/api/employees/' + _currentDetailId + '/photo', {
        method: 'POST',
        headers: { [csrfHeader]: csrf },
        body: form
    }).then(r => r.json()).then(data => {
        document.getElementById('detailPhoto').src = data.photoPath + '?t=' + Date.now();
    });
}
const _sortCols = ['id','name','dept','email','phone','hireDate','status'];
const _urlParams = new URLSearchParams(window.location.search);
const _curSort = _urlParams.get('sort') || '';
const _curDir  = _urlParams.get('dir')  || 'asc';
document.querySelectorAll('.sort-th').forEach((th, idx) => {
    const col = _sortCols[idx];
    if (col === _curSort) th.classList.add(_curDir === 'asc' ? 'sort-asc' : 'sort-desc');
});
function sortTable(th, col, type) {
    const colKey = _sortCols[col];
    const newDir = (_curSort === colKey && _curDir === 'asc') ? 'desc' : 'asc';
    const q = _urlParams.get('q') || '';
    location.href = '/employees?page=1&sort=' + colKey + '&dir=' + newDir + (q ? '&q=' + encodeURIComponent(q) : '');
}
function populateSelects() {
    const deptSrc = document.getElementById('deptSourceSelect');
    const posSrc = document.getElementById('posSourceSelect');
    const deptDst = document.getElementById('departmentId');
    const posDst = document.getElementById('positionId');
    deptDst.innerHTML = deptSrc.innerHTML;
    posDst.innerHTML = posSrc.innerHTML;
}
function openCreate() {
    populateSelects();
    document.getElementById('modalTitle').textContent = '직원 등록';
    document.getElementById('employeeId').value = '';
    document.getElementById('name').value = '';
    document.getElementById('email').value = '';
    document.getElementById('phone').value = '';
    document.getElementById('hireDate').value = '';
    document.getElementById('departmentId').value = '';
    document.getElementById('positionId').value = '';
    document.getElementById('salary').value = '';
    document.getElementById('status').value = 'ACTIVE';
    document.getElementById('modalOverlay').classList.add('open');
}
function openEdit(id) {
    fetch('/api/employees/' + id)
        .then(r => r.json())
        .then(e => {
            populateSelects();
            document.getElementById('modalTitle').textContent = '직원 수정';
            document.getElementById('employeeId').value = e.employeeId;
            document.getElementById('name').value = e.name || '';
            document.getElementById('email').value = e.email || '';
            document.getElementById('phone').value = e.phone || '';
            document.getElementById('hireDate').value = e.hireDate || '';
            document.getElementById('departmentId').value = e.departmentId || '';
            document.getElementById('positionId').value = e.positionId || '';
            document.getElementById('salary').value = e.salary || '';
            document.getElementById('status').value = e.status || 'ACTIVE';
            document.getElementById('modalOverlay').classList.add('open');
        });
}
function closeModal() { document.getElementById('modalOverlay').classList.remove('open'); }
function saveEmployee() {
    const id = document.getElementById('employeeId').value;
    const data = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        hireDate: document.getElementById('hireDate').value || null,
        departmentId: document.getElementById('departmentId').value || null,
        positionId: document.getElementById('positionId').value || null,
        salary: document.getElementById('salary').value || null,
        status: document.getElementById('status').value
    };
    const url = id ? '/api/employees/' + id : '/api/employees';
    const method = id ? 'PUT' : 'POST';
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch(url, { method, headers: {'Content-Type':'application/json', [csrfHeader]: csrf}, body: JSON.stringify(data) })
        .then(() => location.reload());
}
function updateStatus(id, status) {
    const csrf = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    fetch('/api/employees/' + id + '/status', {
        method: 'PATCH',
        headers: {'Content-Type':'application/json', [csrfHeader]: csrf},
        body: JSON.stringify({status: status})
    }).then(() => location.reload());
}
</script>
</body>
</html>
