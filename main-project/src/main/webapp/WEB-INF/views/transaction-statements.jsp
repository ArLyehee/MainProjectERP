<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">
<title>거래명세서 | 개발팬 ERP</title>
<link rel="stylesheet" href="/css/erp.css?v=15">
<style>.items-table{width:100%;border-collapse:collapse;margin:12px 0;}.items-table th,.items-table td{border:1px solid #d1d5db;padding:6px 8px;font-size:13px;}.items-table th{background:#f3f4f6;text-align:center;}.items-table td input{width:100%;border:none;background:transparent;font-size:13px;padding:2px;}.items-table td input:focus{outline:1px solid var(--accent);border-radius:3px;}.btn-add-row{font-size:12px;padding:4px 10px;background:#e5e7eb;border:none;border-radius:4px;cursor:pointer;margin-bottom:8px;}.btn-add-row:hover{background:#d1d5db;}.btn-del-row{background:#fee2e2;border:none;border-radius:4px;padding:3px 7px;cursor:pointer;color:#dc2626;font-size:12px;}.total-row{font-weight:600;background:#f9fafb;}.action-btns{display:flex;gap:6px;}.btn-print{background:#2563eb;color:#fff;border:none;border-radius:6px;padding:5px 12px;cursor:pointer;font-size:13px;}.btn-print:hover{background:#1d4ed8;}.badge-no{font-family:monospace;font-size:12px;background:#eff6ff;color:#1e40af;padding:2px 8px;border-radius:10px;}.btn-edit{background:#059669;color:#fff;border:none;border-radius:6px;padding:5px 12px;cursor:pointer;font-size:13px;}.btn-edit:hover{background:#047857;}</style>
</head>
<body>
<div class="layout">
<jsp:include page="/WEB-INF/views/fragments/sidebar.jsp"><jsp:param name="current" value="transaction-statements"/></jsp:include>
<main class="main">
<div class="page-header"><div class="page-title"><h2>거래명세서</h2><p>거래명세서를 등록하고 인쇄합니다.</p></div>
<button onclick="openModal()" class="btn btn-primary">+ 거래명세서 등록</button>
<button onclick="openOcrModal()" class="btn btn-secondary" style="margin-left:8px;">📷 명세서 자동등록 (OCR)</button>
</div>
<div class="search-bar"><input type="text" id="searchInput" placeholder="명세서번호, 공급자명, 사업자번호 검색..." oninput="filterTable()" class="search-input"></div>
<div class="card"><table><thead><tr>
<th class="sort-th" onclick="sortTable(this,0,'str')">명세서번호<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th" onclick="sortTable(this,1,'str')">발행일<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th" onclick="sortTable(this,2,'str')">공급자명<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th" onclick="sortTable(this,3,'str')">품목<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th" onclick="sortTable(this,4,'str')">사업자번호<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th" onclick="sortTable(this,5,'str')">연락처<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th text-right" onclick="sortTable(this,6,'num')">공급가액<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th text-right" onclick="sortTable(this,7,'num')">세액(10%)<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th class="sort-th text-right" onclick="sortTable(this,8,'num')">합계금액<span class="sort-btn"><span class="arr-up">▲</span><span class="arr-down">▼</span></span></th>
<th>액션</th>
</tr></thead><tbody>
<c:if test="${empty statementList}"><tr><td colspan="10" class="empty-state">등록된 거래명세서가 없습니다.</td></tr></c:if>
<c:forEach var="s" items="${statementList}">
<tr>
<td><span class="badge-no">${s.statementNo}</span></td>
<td>${s.issueDate}</td>
<td>${s.supplyName}</td>
<td>${s.itemSummary != null ? s.itemSummary : '-'}</td>
<td>${s.customerBizNo != null ? s.customerBizNo : '-'}</td>
<td>${s.customerTel != null ? s.customerTel : '-'}</td>
<td class="text-right"><fmt:formatNumber value="${s.totalAmount}" type="number" groupingUsed="true"/>원</td>
<td class="text-right"><fmt:formatNumber value="${s.taxAmount}" type="number" groupingUsed="true"/>원</td>
<td class="text-right" style="font-weight:600;"><fmt:formatNumber value="${s.grandTotal}" type="number" groupingUsed="true"/>원</td>
<td><div class="action-btns">
<button class="btn-edit" onclick="openEditModal(${s.statementId})">수정</button>
<button class="btn-print" onclick="printStatement(${s.statementId})"> 인쇄</button>
<button class="btn-del-sm" onclick="deleteStatement(${s.statementId})">삭제</button>
</div></td>
</tr>
</c:forEach>
</tbody></table></div>
<c:if test="${totalPages != null and totalPages > 1}">
<div class="pagination">
<a href="/transaction-statements?page=${currentPage - 1}" class="${currentPage == 1 ? 'disabled' : ''}">&laquo;</a>
<c:forEach begin="${pageStart}" end="${pageEnd}" var="i">
<a href="/transaction-statements?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
</c:forEach>
<a href="/transaction-statements?page=${currentPage + 1}" class="${currentPage == totalPages ? 'disabled' : ''}">&raquo;</a>
</div>
</c:if>
</main>
</div>
<!-- OCR 모달 -->
<div class="modal-overlay" id="ocrModal"><div class="modal" style="max-width:500px;width:95%;"><h3>📷 명세서 자동등록 (OCR)</h3><p style="font-size:13px;color:var(--text-muted);margin-bottom:16px;">거래명세서 이미지(JPG, PNG, PDF)를 업로드하면 자동으로 내용을 인식합니다.</p><div class="form-group"><label>담당자 (우리 회사)</label><input type="text" id="ocrManagerName" value="${currentUserName}" placeholder="홍길동" style="width:100%;" autocomplete="off"></div><div class="form-group"><label>명세서 파일 *</label><input type="file" id="ocrFile" accept="image/*,.pdf" style="width:100%;padding:8px;border:1px dashed #d1d5db;border-radius:6px;"></div><div id="ocrStatus" style="display:none;padding:12px;border-radius:6px;font-size:13px;margin-top:8px;"></div><div class="modal-footer"><button class="btn-cancel-modal" onclick="closeOcrModal()">취소</button><button class="btn-save" onclick="submitOcr()">자동 인식 시작</button></div></div></div>
<!-- 등록/수정 모달 -->
<div class="modal-overlay" id="stmtModal"><div class="modal" style="max-width:700px;width:95%;"><h3 id="modalTitle">거래명세서 등록</h3><div style="display:grid;grid-template-columns:1fr 1fr;gap:0 16px;"><div class="form-group"><label>발행일 *</label><input type="date" id="issueDate"></div><div class="form-group"><label>공급자명 *</label><input type="text" id="supplyName" placeholder="(주)홍길동물산"></div><div class="form-group"><label>사업자번호</label><input type="text" id="customerBizNo" placeholder="000-00-00000"></div><div class="form-group"><label>연락처</label><input type="text" id="customerTel" placeholder="02-0000-0000"></div><div class="form-group" style="grid-column:1/-1;"><label>주소</label><input type="text" id="customerAddr" placeholder="서울시 강남구 ..."></div><div class="form-group"><label>담당자</label><input type="text" id="managerName" placeholder="홍길동"></div><div class="form-group"><label>비고</label><input type="text" id="notes" placeholder="특이사항 입력"></div></div><div style="font-weight:600;margin:8px 0 4px;">품목</div><button class="btn-add-row" onclick="addRow()">+ 품목 추가</button><table class="items-table"><thead><tr><th style="width:40%">품목명</th><th style="width:15%">수량</th><th style="width:20%">단가</th><th style="width:20%">금액</th><th style="width:5%"></th></tr></thead><tbody id="itemsBody"></tbody></table><div style="background:#f9fafb;border-radius:6px;padding:10px 14px;margin-bottom:12px;display:flex;align-items:center;justify-content:flex-end;gap:16px;font-size:13px;flex-wrap:wrap;"><span>공급가액: <b id="sumTotal">0</b>원</span><span style="display:flex;align-items:center;gap:4px;">세율: <input type="number" id="taxRate" value="10" min="0" max="100" style="width:48px;text-align:center;border:1px solid #d1d5db;border-radius:4px;padding:2px 4px;font-size:13px;" oninput="onTaxRateChange()">%</span><span style="display:flex;align-items:center;gap:4px;">세액: <input type="number" id="taxAmountInput" value="0" min="0" style="width:110px;text-align:right;border:1px solid #d1d5db;border-radius:4px;padding:2px 4px;font-size:13px;" oninput="onTaxAmountChange()">원</span><span>합계: <b id="sumGrand">0</b>원</span></div><div class="modal-footer"><button class="btn-cancel-modal" onclick="closeModal()">취소</button><button class="btn-save" onclick="saveStatement()">저장</button></div></div></div>
<script>
const csrf = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
let editingId = null;
const _sort = {col:-1, asc:true};
function sortTable(th, col, type) { document.querySelectorAll('.sort-th').forEach(t => t.classList.remove('sort-asc','sort-desc')); _sort.asc = _sort.col === col ? !_sort.asc : true; _sort.col = col; th.classList.add(_sort.asc ? 'sort-asc' : 'sort-desc'); const tbody = document.querySelector('tbody'); const rows = Array.from(tbody.querySelectorAll('tr')).filter(tr => tr.querySelectorAll('td').length > 1); rows.sort((a, b) => { const va = a.querySelectorAll('td')[col].textContent.trim(); const vb = b.querySelectorAll('td')[col].textContent.trim(); const cmp = type === 'num' ? (parseFloat(va.replace(/,/g,''))||0) - (parseFloat(vb.replace(/,/g,''))||0) : va.localeCompare(vb, 'ko'); return _sort.asc ? cmp : -cmp; }); rows.forEach(r => tbody.appendChild(r)); }
function filterTable() { const q = document.getElementById('searchInput').value.toLowerCase(); document.querySelectorAll('tbody tr').forEach(tr => { tr.style.display = tr.textContent.toLowerCase().includes(q) ? '' : 'none'; }); }
function openOcrModal() { document.getElementById('ocrModal').classList.add('open'); }
function closeOcrModal() { document.getElementById('ocrModal').classList.remove('open'); document.getElementById('ocrStatus').style.display='none'; }
function submitOcr() { const file = document.getElementById('ocrFile').files[0]; if (!file) { alert('파일을 선택해주세요.'); return; } const status = document.getElementById('ocrStatus'); status.style.display='block'; status.style.background='#eff6ff'; status.style.color='#1e40af'; status.textContent='인식 중...'; const formData = new FormData(); formData.append('file', file); const managerName = document.getElementById('ocrManagerName').value.trim(); if (managerName) formData.append('managerName', managerName); fetch('/api/ocr/transaction-statement', { method:'POST', headers:{[csrfHeader]:csrf}, body:formData }).then(r => r.json()).then(data => { if (data.success) { closeOcrModal(); location.reload(); } else { status.style.background='#fef2f2'; status.style.color='#dc2626'; status.textContent=data.message||'OCR 처리 실패.'; } }).catch(() => { status.style.background='#fef2f2'; status.style.color='#dc2626'; status.textContent='OCR API가 연동되지 않았습니다.'; }); }
function openModal() { editingId=null; document.getElementById('modalTitle').textContent='거래명세서 등록'; document.getElementById('issueDate').value=new Date().toISOString().slice(0,10); document.getElementById('supplyName').value=''; document.getElementById('customerBizNo').value=''; document.getElementById('customerTel').value=''; document.getElementById('customerAddr').value=''; document.getElementById('managerName').value=''; document.getElementById('notes').value=''; document.getElementById('taxRate').value='10'; document.getElementById('taxAmountInput').value='0'; document.getElementById('itemsBody').innerHTML=''; addRow(); calcTotal(); document.getElementById('stmtModal').classList.add('open'); }
function openEditModal(id) { fetch('/api/transaction-statements/'+id).then(r=>{ if(!r.ok) throw new Error('서버 오류 '+r.status); return r.json(); }).then(s=>{ editingId=id; document.getElementById('modalTitle').textContent='거래명세서 수정 ('+s.statementNo+')'; document.getElementById('issueDate').value=s.issueDate; document.getElementById('supplyName').value=s.supplyName||''; document.getElementById('customerBizNo').value=s.customerBizNo||''; document.getElementById('customerTel').value=s.customerTel||''; document.getElementById('customerAddr').value=s.customerAddr||''; document.getElementById('managerName').value=s.managerName||''; document.getElementById('notes').value=s.notes||''; document.getElementById('itemsBody').innerHTML=''; (s.items||[]).forEach(item=>{ const tr=document.createElement('tr'); tr.innerHTML=`<td><input type="text" placeholder="품목명" oninput="calcTotal()" value="${escHtml(item.itemName)}"></td><td><input type="number" value="${item.quantity}" min="1" oninput="calcTotal()"></td><td><input type="number" value="${item.unitPrice}" min="0" oninput="calcTotal()"></td><td><input type="text" readonly style="color:#6b7280;text-align:right;"></td><td><button class="btn-del-row" onclick="this.closest('tr').remove();calcTotal()">✕</button></td>`; document.getElementById('itemsBody').appendChild(tr); }); if(!s.items||s.items.length===0) addRow(); const loadedTotal=s.totalAmount||0; const loadedTax=s.taxAmount||0; document.getElementById('taxAmountInput').value=loadedTax; document.getElementById('taxRate').value=loadedTotal>0?Math.round(loadedTax/loadedTotal*100):10; calcTotal(); document.getElementById('taxAmountInput').value=loadedTax; document.getElementById('sumGrand').textContent=(loadedTotal+loadedTax).toLocaleString(); document.getElementById('stmtModal').classList.add('open'); }).catch(err=>alert('수정 로드 실패: '+err.message)); }
function escHtml(str) { return (str||'').replace(/&/g,'&amp;').replace(/"/g,'&quot;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
function closeModal() { document.getElementById('stmtModal').classList.remove('open'); }
function addRow() { const tr=document.createElement('tr'); tr.innerHTML=`<td><input type="text" placeholder="품목명" oninput="calcTotal()"></td><td><input type="number" value="1" min="1" oninput="calcTotal()"></td><td><input type="number" value="0" min="0" oninput="calcTotal()"></td><td><input type="text" readonly style="color:#6b7280;text-align:right;"></td><td><button class="btn-del-row" onclick="this.closest('tr').remove();calcTotal()">✕</button></td>`; document.getElementById('itemsBody').appendChild(tr); }
function parseItemsTotal() { let total=0; document.querySelectorAll('#itemsBody tr').forEach(tr=>{ const inputs=tr.querySelectorAll('input'); total+=(parseInt(inputs[1].value)||0)*(parseInt(inputs[2].value)||0); }); return total; }
function calcTotal() { let total=0; document.querySelectorAll('#itemsBody tr').forEach(tr=>{ const inputs=tr.querySelectorAll('input'); const qty=parseInt(inputs[1].value)||0; const price=parseInt(inputs[2].value)||0; const amt=qty*price; inputs[3].value=amt.toLocaleString(); total+=amt; }); const rate=parseFloat(document.getElementById('taxRate').value)||0; const tax=Math.round(total*rate/100); document.getElementById('sumTotal').textContent=total.toLocaleString(); document.getElementById('taxAmountInput').value=tax; document.getElementById('sumGrand').textContent=(total+tax).toLocaleString(); }
function onTaxRateChange() { const total=parseItemsTotal(); const rate=parseFloat(document.getElementById('taxRate').value)||0; const tax=Math.round(total*rate/100); document.getElementById('taxAmountInput').value=tax; document.getElementById('sumGrand').textContent=(total+tax).toLocaleString(); }
function onTaxAmountChange() { const total=parseItemsTotal(); const tax=parseInt(document.getElementById('taxAmountInput').value)||0; document.getElementById('sumGrand').textContent=(total+tax).toLocaleString(); }
function saveStatement() { const supplyName=document.getElementById('supplyName').value.trim(); if(!supplyName){alert('공급자명을 입력하세요.');return;} const rows=document.querySelectorAll('#itemsBody tr'); if(rows.length===0){alert('품목을 1개 이상 추가하세요.');return;} const items=[]; for(const tr of rows){const inputs=tr.querySelectorAll('input');const itemName=inputs[0].value.trim();if(!itemName){alert('품목명을 입력하세요.');return;}items.push({itemName:itemName,quantity:parseInt(inputs[1].value)||1,unitPrice:parseInt(inputs[2].value)||0});} const data={issueDate:document.getElementById('issueDate').value,customerName:'개발환기좀해 ERP',supplyName:supplyName,customerBizNo:document.getElementById('customerBizNo').value,customerTel:document.getElementById('customerTel').value,customerAddr:document.getElementById('customerAddr').value,notes:document.getElementById('notes').value,managerName:document.getElementById('managerName').value,taxAmount:parseInt(document.getElementById('taxAmountInput').value)||0,items:items}; const url=editingId?'/api/transaction-statements/'+editingId:'/api/transaction-statements'; const method=editingId?'PUT':'POST'; fetch(url,{method:method,headers:{'Content-Type':'application/json',[csrfHeader]:csrf},body:JSON.stringify(data)}).then(res=>{if(!res.ok){const msg=res.headers.get('X-Error-Message')||'저장에 실패했습니다.';alert(msg);}else{closeModal();location.reload();}}); }
function printStatement(id) { window.open('/transaction-statements/'+id+'/print','_blank'); }
function deleteStatement(id) { if(!confirm('삭제하시겠습니까?'))return; fetch('/api/transaction-statements/'+id,{method:'DELETE',headers:{[csrfHeader]:csrf}}).then(()=>location.reload()); }
</script>
</body>
</html>
