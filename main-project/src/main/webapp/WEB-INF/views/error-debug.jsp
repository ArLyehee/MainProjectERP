<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><title>Error</title>
<style>
body { font-family: monospace; padding: 30px; background: #1e1e1e; color: #d4d4d4; }
h2 { color: #f44747; }
.box { background: #2d2d2d; border: 1px solid #444; border-radius: 6px; padding: 16px; margin: 12px 0; white-space: pre-wrap; word-break: break-all; font-size: 13px; }
.label { color: #9cdcfe; font-size: 11px; text-transform: uppercase; margin-bottom: 4px; }
</style>
</head>
<body>
<h2>서버 오류 발생</h2>
<div class="label">에러 메시지</div>
<div class="box">${errorMessage}</div>
<div class="label">원인 (Cause)</div>
<div class="box">${causeMessage}</div>
<div class="label">스택 트레이스</div>
<div class="box">${stackTrace}</div>
</body>
</html>
