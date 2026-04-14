<!-- 프로젝트 로고/배너 영역 -->
<h1>개발환기좀해 ERP</h1>
<p><b>중소 제조기업을 위한 통합 기업자원관리 시스템</b></p>

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white"/>
  <img src="https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"/>
</p>

<p>
  <img src="https://img.shields.io/badge/프로젝트%20기간-2026.03.11 ~ 04.02-blue?style=flat-square"/>
  <img src="https://img.shields.io/badge/팀원-3인-green?style=flat-square"/>
  <img src="https://img.shields.io/badge/DB 테이블-23개-orange?style=flat-square"/>
  <img src="https://img.shields.io/badge/자동화 로직-20개-red?style=flat-square"/>
</p>

</div>

---

## 프로젝트 소개

**개발환기좀해 ERP**는 중소 제조기업의 전 업무 영역을 하나의 플랫폼으로 통합한 **ERP(Enterprise Resource Planning) 시스템**입니다.

인사·급여, 생산·BOM, 재고·창고, 구매·발주, 영업·매출, 재무·회계까지 **6개 핵심 모듈**을 구축하였으며,  
MariaDB의 **트리거(Trigger)**와 **저장 프로시저(Stored Procedure)**를 활용하여 데이터 간 연계를 완전 자동화했습니다.

> 출근 기록 하나가 입력되면 → 일급 계산 → 월급 집계 → 판관비 반영 → 재무제표까지 **자동으로 갱신**됩니다.

---

## 팀원 소개 및 역할 분담

| 팀원 | 담당 영역 | 담당 태스크 | 주요 기여 |
|:---:|:---:|---|---|
| **한결 김** | DB 설계 & 백엔드 | 데이터베이스 관리 (재무표 생성 및 계산)<br>데이터베이스 로직 설계<br>데이터베이스 구축 및 연결<br>ERP Spring 프로젝트 설계<br>가상 데이터 생성<br>오류 수정<br>데이터베이스 구조 개선<br>개발 환경 구축 (공동) | MariaDB 트리거 15개 + 저장 프로시저 5개 구현, Google Colab/Python 더미 데이터 생성, 근태→급여→재무제표 자동 갱신 체계 구축, 오류 테스트 및 품질 관리 |
| **윤종빈** | 풀스택 개발 & UI/UX | ERP Spring 프로젝트 코드 기획<br>Spring 웹 개발 (풀스택)<br>개발 환경 구축 (공동) | Spring Boot + MyBatis 백엔드 전체, 대시보드·매출·재무·거래명세서·근태·인사 화면 구현, 사이드바 UI·정렬·검색·페이지네이션 전체 업그레이드, 심각한 레이아웃 버그 및 Spring Security 문제 해결 |
| **_Hee** | OCR & RPA & 재무분석 | ERP Spring 프로젝트 깃 연동 (팀)<br>재무 분석 및 예측<br>개발 환경 구축 (공동) | Tesseract OCR 파이프라인 구축 (정확도 7.41% → **99.56%** 개선), RPA로 ERP 재무제표 자동 다운로드 및 피벗 테이블·그래프 생성, OCR 데이터 DB 연동, 재무 분석 및 예측 기능 |

---

## 기술 스택

| 구분 | 기술 | 상세 |
|---|---|---|
| **Backend** | Java, Spring Boot | Spring Boot 3.x, MVC 패턴, 비즈니스 로직 |
| **ORM / SQL** | MyBatis | SQL 매퍼, 도메인 클래스 |
| **Frontend** | JSP, HTML/CSS/JS | 서버사이드 렌더링, 동적 화면 |
| **Database** | MariaDB 12.1.2 | InnoDB 엔진, utf8mb4, 트리거/프로시저 |
| **데이터 생성** | Python, Google Colab | 더미 데이터 자동 생성 스크립트 |
| **자동화** | Python, Tesseract OCR, RPA | OCR 파이프라인, 영수증 인식, 업무 자동화 |
| **보안** | Spring Security 6 | 로그인, 권한 관리 (ADMIN / EMPLOYEE) |
| **형상관리** | Git, GitHub | master 브랜치 기반 협업 |

### 언어 비중

```
HTML       ████████████████████░░░░░░░░░░░░░░░░  43.7%
Java       ██████████████░░░░░░░░░░░░░░░░░░░░░░  35.4%
Python     █████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░  12.8%
CSS        ███░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░   8.1%
```

---

## 시스템 아키텍처

### 6대 핵심 기능 모듈

```
┌──────────────────────────────────────────────────────────────────┐
│                     개발환기좀해 ERP System                        │
├──────────┬──────────┬──────────┬──────────┬──────────┬──────────┤
│  01 HR   │  02 MFG  │  03 INV  │  04 PUR  │  05 SLS  │  06 FIN  │
│  인사관리 │  생산관리 │  재고관리 │  구매관리 │  영업관리 │  재무관리 │
└──────────┴──────────┴──────────┴──────────┴──────────┴──────────┘
```

### 데이터 자동화 흐름

#### ① 근태 → 급여 → 재무제표
```
근태 입력          일급 계산         월급 집계
(attendance)  →  (daily_pay 산정) → (monthly_salary)
                                           ↓ 트리거 자동 연쇄
재무제표 갱신  ←  판관비 반영      ← 급여 비용화
(financial_   ←  (operating_     ← (sp_refresh_
 statements)       expenses)        salary_expense)
```

#### ② 발주 → 재고 → 재무제표
```
발주 생성          상태: COMPLETED          재고 자동 입고
(purchase_    →  (trg_purchase_order_  →  (inventory 갱신)
 orders)          after_update)               ↓
                                       거래명세서 생성    →  재무제표 갱신
                                       (transaction_     →  (sp_refresh_
                                        statements)           financial)
```

#### ③ 판매 → 재무제표
```
판매 등록          revenue 자동 계산        sales 테이블 동기화
(yearly_sales) → (BEFORE INSERT 트리거) → (AFTER INSERT 트리거) → 재무제표 즉시 갱신
```

---

## 데이터베이스 설계

### 전체 테이블 구성 (23개)

| 모듈 | 테이블 | 설명 |
|---|---|---|
| **인사/급여** | `employees`, `departments`, `positions`, `attendance`, `monthly_salary` | 직원 정보, 부서, 직급, 근태, 월급 집계 |
| **생산** | `products`, `bom`, `bom_item`, `work_orders`, `production_receipts` | 제품/부품, BOM(자재명세서), 작업지시, 생산 실적 |
| **재고/창고** | `inventory`, `warehouses`, `receipts` | 창고별 재고 현황, 입고 기록 |
| **구매/발주** | `suppliers`, `supplier_products`, `purchase_orders`, `purchase_order_items` | 거래처, 발주서, 발주 품목 |
| **영업** | `customer_orders`, `yearly_sales`, `sales`, `shipments` | 고객 주문, 연간 판매, 출하 |
| **재무** | `financial_statements`, `operating_expenses` | 월별 재무제표, 판관비 |
| **거래명세서** | `transaction_statements`, `transaction_statement_items` | 거래명세서 헤더 및 상세 |
| **시스템** | `users` | 사용자 계정, 권한 관리 |

### 데이터 규모

| 테이블 | 데이터 규모 |
|---|---|
| `attendance` | ~33,000건+ |
| `work_orders` / `production_receipts` | ~49,352건 |
| `monthly_salary` | ~13,438건 |
| `operating_expenses` | ~13,669건 |
| `transaction_statements` | ~565건 |
| `purchase_orders` | ~458건 |

---

## 핵심 자동화 로직

### 저장 프로시저 (5개)

| 프로시저 | 기능 |
|---|---|
| `proc_create_order_and_statement` | 발주 생성 + 거래명세서 자동 생성 (세액 10% 자동 산출) |
| `proc_create_order_with_date` | 날짜 지정 발주 생성 (더미 데이터 활용) |
| `proc_generate_2025_dummy_data` | 2025년 전체 발주 더미 데이터 자동 생성 |
| `sp_refresh_financial` | 특정 월 재무제표 재계산 (매출·원가·판관비·영업이익 UPSERT) |
| `sp_refresh_salary_expense` | 월별 급여를 판관비로 자동 반영 |

### 트리거 (15개)

<details>
<summary>트리거 목록 펼쳐보기</summary>

| 트리거 | 대상 | 시점 | 동작 |
|---|---|---|---|
| `trg_attendance_after_insert` | attendance | AFTER INSERT | 근태 입력 시 월급 자동 집계 |
| `trg_attendance_after_update` | attendance | AFTER UPDATE | 근태 수정 시 이전·신규 월 급여 재계산 |
| `trg_attendance_after_delete` | attendance | AFTER DELETE | 근태 삭제 시 급여 재산정 |
| `trg_monthly_salary_after_insert` | monthly_salary | AFTER INSERT | 급여 입력 시 판관비·재무제표 갱신 |
| `trg_monthly_salary_after_update` | monthly_salary | AFTER UPDATE | 급여 변경 시 재무제표 즉시 갱신 |
| `trg_monthly_salary_after_delete` | monthly_salary | AFTER DELETE | 급여 삭제 시 재무제표 재계산 |
| `trg_operating_expenses_after_insert` | operating_expenses | AFTER INSERT | 판관비 입력 시 재무제표 갱신 |
| `trg_operating_expenses_after_update` | operating_expenses | AFTER UPDATE | 판관비 수정 시 재무제표 재계산 |
| `trg_operating_expenses_after_delete` | operating_expenses | AFTER DELETE | 판관비 삭제 시 재무제표 갱신 |
| `trg_purchase_order_after_update` | purchase_orders | AFTER UPDATE | 발주 COMPLETED 전환 시 재고·영수증·재무 일괄 처리 |
| `trg_yearly_sales_before_insert` | yearly_sales | BEFORE INSERT | 판매가 기준 revenue 자동 계산 |
| `trg_yearly_sales_after_insert` | yearly_sales | AFTER INSERT | sales 테이블 연동 + 재무제표 갱신 |
| `trg_yearly_sales_after_update` | yearly_sales | AFTER UPDATE | sales 재삽입 + 재무제표 재계산 |
| `trg_yearly_sales_after_delete` | yearly_sales | AFTER DELETE | sales 정리 + 재무제표 갱신 |
| `trg_po_assign_item_count` | purchase_orders | BEFORE INSERT | 발주 수량 미입력 시 1~50 랜덤 배정 |

</details>

---


## 사이트 이미지
<img width="1920" height="1041" alt="대시보드" src="https://github.com/user-attachments/assets/02650894-af64-4d37-9e92-c2c937d706db" />
<img width="1917" height="1036" alt="주문 처리 현황" src="https://github.com/user-attachments/assets/0abbdba3-d3cb-4338-b7f5-1647fba91a00" />
<img width="1920" height="1035" alt="재고 현황" src="https://github.com/user-attachments/assets/c8ab41ab-34be-4b0b-9d86-5c1d6b88c98a" />
<img width="1920" height="1036" alt="발주 관리" src="https://github.com/user-attachments/assets/edd759f4-ebe5-40b9-8134-6f34fc645759" />
<img width="1916" height="1028" alt="입고 관리" src="https://github.com/user-attachments/assets/07e9a5cb-7f53-4fd9-bab5-31df3a108dcc" />
<img width="1920" height="1037" alt="출고 관리" src="https://github.com/user-attachments/assets/0c8eaea2-2256-4fa9-91b1-d8b1e8bef92e" />
<img width="1919" height="1043" alt="매출 관리" src="https://github.com/user-attachments/assets/0f9cd30d-f854-4cd1-9fc0-568d9fd47729" />
<img width="1920" height="1037" alt="비용 관리" src="https://github.com/user-attachments/assets/8097af81-acad-4c6d-b2d7-3f06e2d6ada0" />
<img width="1920" height="1034" alt="거래 명세서" src="https://github.com/user-attachments/assets/7f39924b-0b35-45dd-8426-025a07015a32" />
<img width="1919" height="1036" alt="거래명세서" src="https://github.com/user-attachments/assets/87015866-7087-4e1e-95e9-33db09035e60" />

<img width="1920" height="1030" alt="재무재표" src="https://github.com/user-attachments/assets/25b4a838-260f-49b0-8531-5ec1bdc1caf7" />
<img width="1920" height="1031" alt="제품 관리" src="https://github.com/user-attachments/assets/d33936d6-cfdb-4ce0-92d6-0a55037b0cde" />
<img width="1919" height="1034" alt="공급업체" src="https://github.com/user-attachments/assets/47c72ea2-adc3-4edc-88d0-ce8fc369d091" />
<img width="1920" height="1037" alt="물류센터" src="https://github.com/user-attachments/assets/6f7c83c3-a5b0-4bfb-ac77-0ac9482bb41c" />
<img width="1920" height="1031" alt="직원 관리" src="https://github.com/user-attachments/assets/aa31f677-21fb-400c-8cb8-37bdd1c103a8" />
<img width="1920" height="1036" alt="근태 관리" src="https://github.com/user-attachments/assets/ef7ae11d-7e21-4096-b49e-0ca63f559282" />

<img width="1920" height="1032" alt="작업 지시" src="https://github.com/user-attachments/assets/0d5eaf0c-32eb-4a8c-b71c-184cf812b90e" />
<img width="1919" height="1033" alt="BOM" src="https://github.com/user-attachments/assets/87e1a2c4-fb59-4c6e-9da3-42c2e2017c84" />

---

## 테스트 도구

<img width="1157" height="747" alt="테스트 도구(근태 생성)" src="https://github.com/user-attachments/assets/5a7976c8-b1f8-4f5b-8c4d-96d851f7d560" />
<img width="1154" height="749" alt="테스트 도구(주문생성)" src="https://github.com/user-attachments/assets/9d91be56-882e-4990-a6e2-3544cd7b6f60" />
<img width="1157" height="743" alt="테스트 도구(데이터 삭제)" src="https://github.com/user-attachments/assets/5c0bb4dc-a396-4b15-a161-900ada3fb8b8" />
<img width="1161" height="748" alt="테스트 도구(직원 데이터 생성)" src="https://github.com/user-attachments/assets/67e74551-65be-4715-84ac-828dee8934eb" />
<img width="1324" height="749" alt="테스트 도구(생산 자제 재고보충)" src="https://github.com/user-attachments/assets/246bb83f-1f86-40a5-bdf5-e2bdddd5ed9a" />
<img width="1315" height="745" alt="테스트 도구(판관비 생성)" src="https://github.com/user-attachments/assets/351fc6e5-153a-446c-afb0-e94c89d8a607" />

---

## 주요 기능

### 01. 인사 / 급여 관리
- 직원 등록·수정·삭제 (부서, 직급, 급여, 재직상태)
- 연차 관리 — 사용 연차 / 남은 연차 자동 추적
- 근태 기록 — 출근·퇴근·지각·조퇴 시간 기반 일급 자동 계산
- 월급 자동 집계 → 트리거로 `monthly_salary` 실시간 갱신
- 프로필 사진 업로드 지원

### 02. 생산 관리 (BOM)
- BOM(자재명세서) 등록 — 제품별 구성 부품 및 소요량 정의, 버전 관리
- 작업지시서 생성 — 부품 부족 시 자동 발주, 재고 충분 시 자동 IN_PROGRESS
- 생산 실적 등록 — 작업 완료 시 완성품 재고 자동 추가

### 03. 재고 / 창고 관리
- 다중 창고별 재고 수량 실시간 추적
- 발주 완료 시 트리거로 재고 자동 입고 (UPSERT 방식)
- 재고부족 기준값 설정 → 대시보드 알림 자동 표시

### 04. 구매 / 발주 관리
- 거래처 등록 및 취급 품목 매핑
- 발주 워크플로우 — 대기 → 승인 → 완료 3단계
- 발주 COMPLETED 전환 시 **자동 처리**: 재고 입고 → 입고 기록 → 거래명세서 생성 → 재무제표 갱신
- 거래명세서 자동 생성 — 공급가액, 세액(10%), 합계금액 자동 산출

### 05. 영업 / 매출 관리
- 고객 주문 등록 및 상태 추적
- 판매 등록 시 BEFORE INSERT 트리거로 매출액 자동 계산
- 주문-생산-출하 전 단계 연계 추적

### 06. 재무 / 회계 관리
- 월별 재무제표 자동 갱신 (`sp_refresh_financial`)
- **매출** = yearly_sales 기반 집계
- **매출원가(COGS)** = 판매 제품 원가 + 완료된 발주 매입 원가
- **매출총이익** = 매출 - 매출원가
- **판관비** = 급여 + 기타 운영비용 자동 집계
- **영업이익** = 매출총이익 - 판관비 실시간 계산

### 🤖 OCR & RPA 자동화 (부가 기능)
- **Tesseract OCR** 파이프라인 — 영수증 PDF → 이미지 변환 → 텍스트 추출 → DB 저장
- 이미지 전처리 최적화 — KR_adaptive (GaussianBlur + 적응형 이진화), 정확도 **7.41% → 99.56%**
- **RPA** — ERP 사이트 자동 로그인 → 재무제표 다운로드 → 피벗 테이블·그래프 자동 생성

---

## 프로젝트 구조

```
main-project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/erp/
│       │       ├── controller/         # 각 도메인 컨트롤러
│       │       │   ├── DashboardController.java
│       │       │   ├── EmployeeController.java
│       │       │   ├── AttendanceController.java
│       │       │   ├── WorkOrderController.java
│       │       │   ├── InventoryController.java
│       │       │   ├── PurchaseOrderController.java
│       │       │   ├── SaleController.java
│       │       │   ├── FinancialController.java
│       │       │   ├── TransactionStatementController.java
│       │       │   └── EmployeePhotoController.java
│       │       ├── service/            # 비즈니스 로직
│       │       │   ├── WorkOrderService.java    # BOM 재고 연동, 자동 발주
│       │       │   ├── OrderService.java        # 주문 수락 흐름
│       │       │   └── ...
│       │       ├── mapper/             # MyBatis SQL 매퍼 인터페이스
│       │       ├── domain/             # 도메인 클래스 (VO/DTO)
│       │       └── config/
│       │           ├── SecurityConfig.java      # Spring Security 설정
│       │           └── WebConfig.java           # 웹 설정
│       ├── resources/
│       │   ├── mapper/                 # MyBatis XML 매퍼
│       │   │   ├── AttendanceMapper.xml
│       │   │   ├── EmployeeMapper.xml
│       │   │   └── ...
│       │   └── application.yml         # DB 연결, 포트, 스키마 설정
│       └── webapp/
│           ├── WEB-INF/views/          # JSP
│           │   ├── dashboard.jsp
│           │   ├── employees.html
│           │   ├── work-orders.jsp
│           │   ├── purchase-orders.jsp
│           │   ├── sidebar.jsp
│           │   └── transaction-statement-print.html  # 인쇄 전용 템플릿
│           └── static/
│               └── css/erp.css
└── SQL.sql                             # DB 스키마 전체 (트리거/프로시저 포함)

## 사용자 권한

| 역할 | 접근 범위 | 주요 권한 |
|---|---|---|
| `ADMIN` | 전체 모듈 | 사용자 관리, 재무 열람, 시스템 설정, 데이터 수정/삭제 |
| `EMPLOYEE` | 할당된 모듈 | 본인 근태 조회, 담당 업무 데이터 입력 |

---

## 개발 일지 요약

| 날짜 | 주요 작업 |
|---|---|
| 2026-03-11 | Spring Boot + MyBatis + MariaDB 환경 구성, 로그인 풀스택, 발주관리 SQL 매퍼 |
| 2026-03-12 | Google Colab 1년치 데이터 생성, 재무제표 생성/연동, 대시보드·매출·재무 기능 구현 |
| 2026-03-13 | MariaDB 자동 갱신 쿼리 추가 (근태→일급→월급→재무제표) |
| 2026-03-16 | DB 연결 완료, 자동 업데이트 스크립트, 거래명세서·인쇄 기능 완성, OCR 파이프라인 첫 실행 |
| 2026-03-17 | 레이아웃 버그 수정, UI 개선, OCR 정확도 7.41% → 99.56% 달성, RPA 피벗 테이블 완성 |
| 2026-03-18 | DB 수정(출고/입고/재고/부품), UX 개선, 재고 연동 자동화, RPA 재무제표 연동 |
| 2026-03-24 | 담당자 자동배정 트리거, 정렬/검색 전체 업그레이드, OCR DB 저장 기능 |
| 2026-03-25~26 | 발주 워크플로우 단순화, 트랜잭션 처리, 사이드바 아코디언 UI, 연차 관리, 페이지네이션 |
| 2026-03-27 | OCR 파싱 방식 고도화 (header/item 분리), 이미지 전처리 세부 공정 추가 |
| 2026-03-30 | Spring Security 리다이렉트 루프 수정, JSP 경로 문제 해결, 트리거 수정 완료 |
| 2026-03-31 | 자동발주 버그 수정, 발주 취소 기능, 주문 흐름 개선, 데이터 초기화 코드 생성 |
| 2026-04-02 | 2025년 가상 데이터 대량 생성, 재무제표 관리, 오류 테스트 |

---

## 주요 기술적 도전 & 해결

### 레이아웃 버그 — 테이블 19,000px 팽창
> 페이지네이션 529개가 한 줄로 렌더링되어 첫 번째 컬럼만 보이는 현상  
> **해결:** `flex-wrap: wrap` 적용

### Spring Security 리다이렉트 루프
> Spring Security 6이 JSP forward 요청도 가로채서 `/login`으로 무한 리다이렉트  
> **해결:** `SecurityConfig.java`에 `dispatcherTypeMatchers(FORWARD, INCLUDE).permitAll()` 추가

### DB 타입 불일치
> `purchase_orders.po_id`가 `VARCHAR`인데 Java에서 `Long`으로 받아 파싱 오류  
> **해결:** 매퍼 및 도메인 클래스 타입 수정

### 자동 발주 로직 오류
> 재고가 있는 부품도 발주되는 버그 (BOM수량 × 작업수량 - 현재재고 계산 오류)  
> **해결:** 현재재고가 음수인 경우에만 그 절대값만큼 발주하도록 수정

### OCR 정확도 7.41% 문제
> 전처리만 적용 시 한국어 인식률 극히 낮음  
> **해결:** 후처리 + 특수기호 제거, KR_adaptive(GaussianBlur + 적응형 이진화) 방식으로 **99.56%** 달성

---

## 설계 특징

- **자동화 중심 설계** — 모든 재무 수치는 트리거·프로시저가 자동 갱신, 수작업 오류 방지
- **UPSERT 패턴** — 재무제표·재고·급여 테이블 모두 `INSERT ... ON DUPLICATE KEY UPDATE`로 정합성 보장
- **CASCADE 삭제** — `transaction_statement_items` → `transaction_statements` 삭제 시 하위 항목 자동 정리
- **다중 창고 지원** — `inventory` 테이블이 `warehouse_id`를 포함해 창고별 재고 분리 관리
- **트랜잭션 처리** — 입고/출고/생산입고 각각 단일 트랜잭션으로 데이터 정합성 보장
- **세금 자동 계산** — 거래명세서 생성 시 공급가의 10%를 세액으로 자동 산출

---

<div align="center">
  <p>
    <b>개발환기좀해 ERP</b> &nbsp;·&nbsp; gaebalfan_erp &nbsp;·&nbsp; 2026
  </p>
  <p>
    <img src="https://img.shields.io/badge/Made%20with-❤️-red?style=flat-square"/>
    <img src="https://img.shields.io/badge/DB-MariaDB-003545?style=flat-square&logo=mariadb"/>
    <img src="https://img.shields.io/badge/Framework-Spring Boot-6DB33F?style=flat-square&logo=springboot"/>
  </p>
</div>
