<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>로그인 | 개발팬 ERP</title><link rel="stylesheet" href="/css/erp.css?v=15">
</head>
<body class="login-page">
<div class="login-wrap"><div class="logo-area"><img src="/images/logo.png" alt="로고" class="login-logo"><div class="logo-title">GAEBALFAN</div><div class="logo-sub">개발환기좀시키자 ERP 시스템</div></div><div class="login-card"><div class="card-header"><h2>로그인</h2><p>관리자로부터 부여받은 계정으로 로그인하세요.</p></div><c:if test="${errorMsg}"><div class="alert alert-error"><span>${errorMsg}</span></div></c:if><c:if test="${logoutMsg}"><div class="alert alert-success"><span>${logoutMsg}</span></div></c:if><form action="/login" method="post"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><div class="form-group"><label class="login-label" for="username">아이디</label><input class="login-input" type="text" id="username" name="username" placeholder="로그인 ID를 입력하세요" autofocus required/></div><div class="form-group"><label class="login-label" for="password">비밀번호</label><input class="login-input" type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required/></div><button type="submit" class="btn-login">로그인</button></form></div><div class="footer-note"><span>GAEBALFAN ERP</span> &nbsp;·&nbsp; 계정 문의는 관리자에게 연락하세요
    </div>
</div>
</body>
</html>
