<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<aside class="sidebar">
    <div class="sidebar-logo">
        <img src="/images/logo.png" alt="로고" class="sidebar-logo-img">
        <div class="brand" style="font-size:14px; font-weight:700; letter-spacing:0;">개발환기좀해 ERP<small style="font-size:10px; color:var(--sb-muted); letter-spacing:0; font-weight:400;">풍기산업</small></div>
    </div>

    <div class="nav-scroll">
    <!-- 메인 -->
    <div class="nav-category open">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>메인</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/dashboard" class="${param.current == 'dashboard' ? 'nav-item active' : 'nav-item'}">대시보드</a>
        </div>
    </div>

    <!-- 주문 관리 -->
    <div class="${param.current == 'orders' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>주문 관리</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/orders" class="${param.current == 'orders' ? 'nav-item active' : 'nav-item'}">주문 처리 현황</a>
        </div>
    </div>

    <!-- 재고/물류 -->
    <div class="${param.current == 'inventory' or param.current == 'purchase-orders' or param.current == 'receipts' or param.current == 'shipments' or param.current == 'sales' or param.current == 'expenses' or param.current == 'transaction-statements' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>재고/물류</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/inventory"       class="${param.current == 'inventory'       ? 'nav-item active' : 'nav-item'}">재고 현황</a>
            <a href="/purchase-orders" class="${param.current == 'purchase-orders' ? 'nav-item active' : 'nav-item'}">발주 관리</a>
            <a href="/receipts"        class="${param.current == 'receipts'        ? 'nav-item active' : 'nav-item'}">입고 관리</a>
            <a href="/shipments"       class="${param.current == 'shipments'       ? 'nav-item active' : 'nav-item'}">출고 관리</a>
            <a href="/sales"           class="${param.current == 'sales'           ? 'nav-item active' : 'nav-item'}">매출 관리</a>
            <a href="/expenses"        class="${param.current == 'expenses'        ? 'nav-item active' : 'nav-item'}">비용 관리</a>
            <a href="/transaction-statements" class="${param.current == 'transaction-statements' ? 'nav-item active' : 'nav-item'}">거래명세서</a>
        </div>
    </div>

    <!-- 경영 -->
    <div class="${param.current == 'finance' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>경영</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/finance" class="${param.current == 'finance' ? 'nav-item active' : 'nav-item'}">재무제표</a>
        </div>
    </div>

    <!-- 기준정보 -->
    <div class="${param.current == 'products' or param.current == 'suppliers' or param.current == 'warehouses' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>기준정보</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/products"   class="${param.current == 'products'   ? 'nav-item active' : 'nav-item'}">제품 관리</a>
            <a href="/suppliers"  class="${param.current == 'suppliers'  ? 'nav-item active' : 'nav-item'}">공급업체</a>
            <a href="/warehouses" class="${param.current == 'warehouses' ? 'nav-item active' : 'nav-item'}">창고 관리</a>
        </div>
    </div>

    <!-- 인사 -->
    <div class="${param.current == 'employees' or param.current == 'attendance' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>인사</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/employees"  class="${param.current == 'employees'  ? 'nav-item active' : 'nav-item'}">직원 관리</a>
            <a href="/attendance" class="${param.current == 'attendance' ? 'nav-item active' : 'nav-item'}">근태 관리</a>
        </div>
    </div>

    <!-- 생산관리 -->
    <div class="${param.current == 'work-orders' or param.current == 'bom' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>생산관리</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/work-orders" class="${param.current == 'work-orders' ? 'nav-item active' : 'nav-item'}">작업지시</a>
            <a href="/bom"         class="${param.current == 'bom'         ? 'nav-item active' : 'nav-item'}">BOM 관리</a>
        </div>
    </div>

    <!-- 관리자 -->
    <sec:authorize access="hasRole('ADMIN')">
    <div class="${param.current == 'admin-settings' ? 'nav-category open' : 'nav-category'}">
        <div class="nav-category-header" onclick="toggleNav(this)">
            <span>관리자</span><span class="nav-arrow">▾</span>
        </div>
        <div class="nav-category-body">
            <a href="/admin/users" class="nav-item">사용자 관리</a>
            <a href="/admin/settings" class="${param.current == 'admin-settings' ? 'nav-item active' : 'nav-item'}">시스템 설정</a>
        </div>
    </div>
    </sec:authorize>
    </div><!-- /nav-scroll -->

    <div class="sidebar-bottom">
        <div class="user-info">
            <div class="avatar"><sec:authentication property="name"/></div>
            <div class="user-meta">
                <div class="uname"><sec:authentication property="name"/></div>
                <div class="urole"><sec:authentication property="authorities[0].authority"/></div>
            </div>
            <form action="/logout" method="post" style="display:inline;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn-logout" title="로그아웃">⏻</button>
            </form>
        </div>
    </div>

    <script>
    function toggleNav(header) {
        const cat = header.closest('.nav-category');
        cat.classList.toggle('open');
        saveNavState();
    }

    function saveNavState() {
        const state = {};
        document.querySelectorAll('.nav-category').forEach(cat => {
            const key = cat.querySelector('.nav-category-header span').textContent.trim();
            state[key] = cat.classList.contains('open');
        });
        localStorage.setItem('sidebarNavState', JSON.stringify(state));
    }

    (function restoreNavState() {
        const saved = localStorage.getItem('sidebarNavState');
        if (!saved) return;
        try {
            const state = JSON.parse(saved);
            document.querySelectorAll('.nav-category').forEach(cat => {
                const key = cat.querySelector('.nav-category-header span').textContent.trim();
                // 현재 페이지가 포함된 카테고리는 항상 열려있어야 함
                const hasActive = cat.querySelector('.nav-item.active') !== null;
                if (hasActive) {
                    cat.classList.add('open');
                } else if (key in state) {
                    cat.classList.toggle('open', state[key]);
                }
            });
        } catch(e) {}
    })();
    </script>
</aside>
