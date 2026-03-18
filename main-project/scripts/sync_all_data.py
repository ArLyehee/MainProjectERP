"""
ERP 전체 데이터 동기화 스크립트
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
실행 순서 (의존관계 순):

 STEP 1. attendance 자동생성
         → 실행일 기준 전날까지 누락 근무일 생성
           (트리거: attendance → monthly_salary 자동 갱신)

 STEP 2. monthly_salary → operating_expenses(급여) 동기화
         → monthly_salary 합계와 다른 달 UPDATE
         → 급여 항목이 없는 달 INSERT

 STEP 3. operating_expenses 급여 외 9개 항목 자동생성
         → 비어있는 달만 INSERT (기존 데이터 스킵)
         → 항목별 성격에 맞게 현실적 금액 생성

 STEP 4. financial_statements 재계산
         → 모든 월의 재무제표를 최신 데이터로 갱신
           (revenue / cogs / expenses / profit 전부 재계산)

기존 데이터: 스킵 또는 최신값으로 UPDATE
누락 데이터: 자동 생성
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
"""

import mariadb
import sys
import random
from datetime import date, timedelta
from dateutil.relativedelta import relativedelta

# ── DB 연결 설정 ───────────────────────────────────────────
DB_CONFIG = dict(
    host="192.168.0.224",
    port=3306,
    user="root",
    password="9927",
    database="gaebalfan_erp",   # ← 실제 DB명으로 변경
    autocommit=False,
)

# ── 한국 공휴일 ────────────────────────────────────────────
HOLIDAYS = {
    date(2025,1,1), date(2025,1,28), date(2025,1,29), date(2025,1,30),
    date(2025,3,1), date(2025,3,3), date(2025,5,5), date(2025,5,6),
    date(2025,6,6), date(2025,8,15), date(2025,10,3), date(2025,10,5),
    date(2025,10,6), date(2025,10,7), date(2025,10,9), date(2025,12,25),
    date(2026,1,1), date(2026,2,17), date(2026,2,18), date(2026,2,19),
    date(2026,3,1), date(2026,3,2), date(2026,5,5), date(2026,6,6),
    date(2026,8,15), date(2026,9,24), date(2026,9,25), date(2026,9,26),
    date(2026,10,3), date(2026,10,9), date(2026,12,25),
}

# ── 판관비 항목 설정 ───────────────────────────────────────
EXPENSE_CONFIG = {
    "임대료":   {"base": 20_265_618, "cv": 0.05,  "type": "fixed"},
    "서버비":   {"base":  4_398_889, "cv": 0.15,  "type": "fixed"},
    "보험료":   {"base":  5_208_064, "cv": 0.15,  "type": "fixed"},
    "전기수도": {"base":  7_577_577, "cv": 0.10,  "type": "seasonal"},
    "마케팅비": {"base": 30_332_448, "cv": 0.30,  "type": "random"},
    "물류비":   {"base": 18_368_701, "cv": 0.20,  "type": "sales_linked"},
    "복리후생": {"base":  6_464_031, "cv": 0.25,  "type": "random"},
    "사무용품": {"base":    930_515, "cv": 0.30,  "type": "random"},
    "출장비":   {"base":  3_020_579, "cv": 0.40,  "type": "random"},
}

SEASONAL_WEIGHT = {
    1:1.20, 2:1.15, 3:0.95, 4:0.90, 5:0.90, 6:1.05,
    7:1.25, 8:1.25, 9:1.00, 10:0.90, 11:1.05, 12:1.20,
}

# ══════════════════════════════════════════════════════════
# 유틸
# ══════════════════════════════════════════════════════════
def is_workday(d):
    return d.weekday() < 5 and d not in HOLIDAYS

def fmt_td(h, m):
    return f"{h:02d}:{m:02d}:00"

def section(title):
    print(f"\n{'━'*55}")
    print(f"  {title}")
    print(f"{'━'*55}")

def result(label, n, unit="건"):
    if n > 0:
        print(f"  ✅ {label}: {n}{unit}")
    else:
        print(f"  ⏭  {label}: 없음 (모두 최신)")

# ══════════════════════════════════════════════════════════
# STEP 1. attendance 자동생성
# ══════════════════════════════════════════════════════════
def gen_attendance_record(emp_id, salary, work_date):
    rng = random.Random(int(emp_id) * 10000 + work_date.toordinal())

    rand = rng.random()
    if rand < 0.70:
        ci_h, ci_m = 9, rng.randint(0, 5)
        late = ci_m if ci_m > 0 else 0
    elif rand < 0.90:
        ci_h, ci_m = 9, rng.randint(6, 30)
        late = ci_m
    else:
        ci_h = 9 if rng.random() < 0.5 else 10
        ci_m = rng.randint(31, 59) if ci_h == 9 else 0
        late = (ci_h - 9) * 60 + ci_m

    rand2 = rng.random()
    if rand2 < 0.10:
        co_h, co_m = 17, rng.randint(0, 50)
        early = 18 * 60 - (co_h * 60 + co_m)
    elif rand2 < 0.60:
        co_h, co_m = 18, rng.randint(0, 10)
        early = 0
    else:
        extra = rng.randint(11, 90)
        co_h, co_m = 18 + extra // 60, extra % 60
        early = 0

    work_min = (co_h * 60 + co_m) - (ci_h * 60 + ci_m)
    work_h   = round(work_min / 60, 2)
    ot_h     = round(max(0, (co_h * 60 + co_m) - 18 * 60) / 60, 2)
    base     = salary / 22
    daily    = round(base * (work_h / 8) - (late + early) * (base / 8 / 60))

    return (emp_id, work_date.strftime("%Y-%m-%d"),
            fmt_td(ci_h, ci_m), fmt_td(co_h, co_m),
            work_h, ot_h, float(late), float(early), daily)


def step1_attendance(cursor):
    section("STEP 1 / 4  attendance 자동생성")

    cursor.execute("SELECT employee_id, salary FROM employees WHERE status='ACTIVE'")
    employees = cursor.fetchall()

    cursor.execute("SELECT MAX(work_date) FROM attendance")
    (max_date,) = cursor.fetchone()
    if max_date is None:
        max_date = date(2025, 1, 1)

    start = max_date + timedelta(days=1)
    end   = date.today() - timedelta(days=1)

    print(f"  범위: {start} ~ {end} / ACTIVE 직원: {len(employees)}명")

    if start > end:
        result("생성할 근무일", 0)
        return 0

    target_dates = [
        start + timedelta(days=i)
        for i in range((end - start).days + 1)
        if is_workday(start + timedelta(days=i))
    ]

    # 이미 존재하는 키
    cursor.execute("""
        SELECT employee_id, work_date FROM attendance
         WHERE work_date BETWEEN %s AND %s
    """, (start.strftime("%Y-%m-%d"), end.strftime("%Y-%m-%d")))
    existing = set((str(r[0]), str(r[1])) for r in cursor.fetchall())

    sql = """INSERT INTO attendance
             (employee_id, work_date, check_in, check_out,
              work_hours, overtime_hours, late_minutes, early_leave_minutes, daily_pay)
             VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)"""

    total = 0
    for d in target_dates:
        batch = [
            gen_attendance_record(eid, float(sal), d)
            for (eid, sal) in employees
            if (str(eid), d.strftime("%Y-%m-%d")) not in existing
        ]
        if batch:
            cursor.executemany(sql, batch)
            total += len(batch)
            print(f"    📝 {d}  {len(batch)}명")

    result("삽입", total)
    return total


# ══════════════════════════════════════════════════════════
# STEP 2. monthly_salary → operating_expenses(급여) 동기화
# ══════════════════════════════════════════════════════════
def step2_salary_sync(cursor):
    section("STEP 2 / 4  급여 항목 동기화 (monthly_salary → operating_expenses)")

    cursor.execute("""
        SELECT DATE_FORMAT(month,'%Y-%m-01'), SUM(total_salary)
          FROM monthly_salary GROUP BY 1 ORDER BY 1
    """)
    ms = {str(r[0]): float(r[1]) for r in cursor.fetchall()}

    cursor.execute("""
        SELECT DATE_FORMAT(expense_date,'%Y-%m-01'), amount
          FROM operating_expenses WHERE expense_type='급여'
    """)
    oe = {str(r[0]): float(r[1]) for r in cursor.fetchall()}

    inserted = updated = skipped = 0
    for m, total in sorted(ms.items()):
        exp_date = m[:7] + "-25"
        if m not in oe:
            cursor.execute("""
                INSERT INTO operating_expenses (expense_type, amount, expense_date)
                VALUES ('급여', %s, %s)
            """, (total, exp_date))
            print(f"    ➕ {m[:7]}  급여 INSERT  {total:>15,.0f}")
            inserted += 1
        elif abs(total - oe[m]) > 1:
            cursor.execute("""
                UPDATE operating_expenses SET amount=%s
                 WHERE expense_type='급여'
                   AND DATE_FORMAT(expense_date,'%Y-%m-01')=%s
            """, (total, m))
            print(f"    🔄 {m[:7]}  급여 UPDATE  {oe[m]:>15,.0f} → {total:>15,.0f}")
            updated += 1
        else:
            skipped += 1

    result("INSERT", inserted)
    result("UPDATE", updated)
    result("스킵 (이미 일치)", skipped)
    return inserted + updated


# ══════════════════════════════════════════════════════════
# STEP 3. operating_expenses 급여 외 9개 항목 생성
# ══════════════════════════════════════════════════════════
def gen_opex_amount(expense_type, target_month, revenue, rng):
    cfg   = EXPENSE_CONFIG[expense_type]
    base  = cfg["base"]
    cv    = cfg["cv"]
    kind  = cfg["type"]
    noise = rng.gauss(1.0, cv)
    noise = max(1.0 - cv * 2, min(1.0 + cv * 2, noise))

    if kind == "fixed":
        return max(0, round(base * noise))
    elif kind == "seasonal":
        return max(0, round(base * SEASONAL_WEIGHT.get(target_month.month, 1.0) * noise))
    elif kind == "sales_linked":
        ratio = max(0.5, min(2.0, revenue / 1_000_000_000 if revenue > 0 else 1.0))
        return max(0, round(base * ratio * noise))
    else:
        return max(0, round(base * noise))


def step3_opex_fill(cursor):
    section("STEP 3 / 4  판관비 9개 항목 자동생성")

    # 대상 월 범위: yearly_sales 첫 달 ~ 현재 월
    cursor.execute("SELECT MIN(sale_date) FROM yearly_sales")
    (min_sale,) = cursor.fetchone()
    start_month = date(min_sale.year, min_sale.month, 1) if min_sale else date(2025, 1, 1)
    end_month   = date.today().replace(day=1)

    # 월별 매출
    cursor.execute("""
        SELECT DATE_FORMAT(sale_date,'%Y-%m-01'), COALESCE(SUM(revenue),0)
          FROM yearly_sales GROUP BY 1
    """)
    monthly_rev = {str(r[0]): float(r[1]) for r in cursor.fetchall()}

    # 기존 항목
    cursor.execute("""
        SELECT expense_type, DATE_FORMAT(expense_date,'%Y-%m-01')
          FROM operating_expenses WHERE expense_type <> '급여'
    """)
    existing = set((str(r[0]), str(r[1])) for r in cursor.fetchall())

    sql = """INSERT INTO operating_expenses (expense_type, amount, expense_date)
             VALUES (%s, %s, %s)"""

    total = 0
    cur   = start_month
    while cur <= end_month:
        m_str   = cur.strftime("%Y-%m-01")
        rev     = monthly_rev.get(m_str, 800_000_000)
        rng     = random.Random(cur.year * 100 + cur.month + 42)
        exp_dt  = cur.replace(day=25).strftime("%Y-%m-%d")
        batch   = []

        for t in EXPENSE_CONFIG:
            if (t, m_str) not in existing:
                amt = gen_opex_amount(t, cur, rev, rng)
                batch.append((t, amt, exp_dt))

        if batch:
            cursor.executemany(sql, batch)
            total += len(batch)
            print(f"    📝 {cur.strftime('%Y-%m')}  {len(batch)}개 항목 생성")

        cur += relativedelta(months=1)

    result("삽입", total)
    return total


# ══════════════════════════════════════════════════════════
# STEP 4. financial_statements 전월 재계산
# ══════════════════════════════════════════════════════════
def step4_financial_refresh(cursor):
    section("STEP 4 / 4  financial_statements 재계산")

    # 대상 월 수집 (yearly_sales + operating_expenses 기준)
    cursor.execute("""
        SELECT DISTINCT DATE_FORMAT(sale_date,'%Y-%m-01') FROM yearly_sales
        UNION
        SELECT DISTINCT DATE_FORMAT(expense_date,'%Y-%m-01') FROM operating_expenses
        ORDER BY 1
    """)
    months = [str(r[0]) for r in cursor.fetchall()]
    print(f"  대상: {len(months)}개월")

    upsert_sql = """
        INSERT INTO financial_statements
            (month, revenue, cogs, gross_profit, expenses, operating_profit)
        VALUES (%s, %s, %s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            revenue          = VALUES(revenue),
            cogs             = VALUES(cogs),
            gross_profit     = VALUES(gross_profit),
            expenses         = VALUES(expenses),
            operating_profit = VALUES(operating_profit)
    """

    refreshed = 0
    for m in months:
        m_end = (date.fromisoformat(m) + relativedelta(months=1) - timedelta(days=1)).strftime("%Y-%m-%d")

        cursor.execute("""
            SELECT COALESCE(SUM(revenue),0) FROM yearly_sales
             WHERE sale_date BETWEEN %s AND %s
        """, (m, m_end))
        revenue = float(cursor.fetchone()[0])

        cursor.execute("""
            SELECT COALESCE(SUM(ys.quantity_sold * p.cost_price),0)
              FROM yearly_sales ys
              JOIN products p ON ys.product_id = p.product_id
             WHERE ys.sale_date BETWEEN %s AND %s
        """, (m, m_end))
        cogs = float(cursor.fetchone()[0])

        cursor.execute("""
            SELECT COALESCE(SUM(amount),0) FROM operating_expenses
             WHERE expense_date BETWEEN %s AND %s
        """, (m, m_end))
        expenses = float(cursor.fetchone()[0])

        gross_profit     = revenue - cogs
        operating_profit = gross_profit - expenses

        cursor.execute(upsert_sql, (
            m, revenue, cogs, gross_profit, expenses, operating_profit
        ))
        refreshed += 1
        print(f"    🔄 {m[:7]}  매출={revenue:>15,.0f}  영업이익={operating_profit:>15,.0f}")

    result("재계산", refreshed, "개월")
    return refreshed


# ══════════════════════════════════════════════════════════
# MAIN
# ══════════════════════════════════════════════════════════
def get_connection():
    try:
        conn = mariadb.connect(**DB_CONFIG)
        print(f"✅ DB 연결: {DB_CONFIG['host']} / {DB_CONFIG['database']}")
        return conn
    except mariadb.Error as e:
        print(f"❌ DB 연결 실패: {e}")
        sys.exit(1)


def main():
    print("=" * 55)
    print("   ERP 전체 데이터 동기화 스크립트")
    print("=" * 55)

    conn   = get_connection()
    cursor = conn.cursor()

    try:
        r1 = step1_attendance(cursor)
        conn.commit()   # attendance 먼저 커밋 → 트리거로 monthly_salary 갱신

        r2 = step2_salary_sync(cursor)
        conn.commit()   # 급여 동기화 커밋 → 트리거로 financial_statements 1차 갱신

        r3 = step3_opex_fill(cursor)
        conn.commit()   # 판관비 커밋 → 트리거로 financial_statements 2차 갱신

        r4 = step4_financial_refresh(cursor)
        conn.commit()   # 최종 재계산 커밋

        print(f"""
{'='*55}
  ✅ 전체 동기화 완료
{'='*55}
  STEP 1  attendance 생성   : {r1:>6}건
  STEP 2  급여 동기화       : {r2:>6}건
  STEP 3  판관비 항목 생성  : {r3:>6}건
  STEP 4  재무제표 재계산   : {r4:>6}개월
{'='*55}
""")

    except Exception as e:
        conn.rollback()
        print(f"\n💥 오류 발생 — 롤백 처리됨: {e}")
        import traceback; traceback.print_exc()
        sys.exit(1)

    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()
