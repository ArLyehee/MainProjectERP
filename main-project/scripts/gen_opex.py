"""
판관비(operating_expenses) 자동생성 스크립트

[가상 회사 판관비 설계 원칙]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
항목      성격        변동 방식
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
임대료    준고정      기준값 ±5%  (계약 기반, 거의 고정)
서버비    완만 변동   기준값 ±15% (사용량 소폭 변동)
보험료    완만 변동   기준값 ±15% (연납/분납, 소폭 변동)
전기수도  계절 변동   기준값 ±20% + 계절 가중치
마케팅비  큰 변동     기준값 ±35% (캠페인 집중 여부)
물류비    매출 연동   매출 비례 ±20%
복리후생  직원 연동   직원수 기반 ±25%
사무용품  소폭 변동   기준값 ±30% (소모품 특성)
출장비    큰 변동     기준값 ±45% (행사/이벤트 변동)
급여      자동        attendance → monthly_salary 트리거 처리
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

실행 시:
  - 현재 월까지 비어있는 달만 채움 (이미 있는 달은 건너뜀)
  - 급여는 트리거가 자동 처리하므로 제외
"""

import mariadb
import sys
import random
from datetime import date
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

# ── 항목별 기준값 및 변동 설정 ─────────────────────────────
# 기존 데이터(2025-01~12) 분석 결과 기반 설정
EXPENSE_CONFIG = {
    # 항목명: (기준값, 변동계수, 성격)
    # 성격: 'fixed' | 'seasonal' | 'sales_linked' | 'random'
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

# 계절 가중치 (전기수도: 여름·겨울 높음)
SEASONAL_WEIGHT = {
    1: 1.20,   # 겨울 난방
    2: 1.15,
    3: 0.95,
    4: 0.90,
    5: 0.90,
    6: 1.05,
    7: 1.25,   # 여름 냉방
    8: 1.25,
    9: 1.00,
    10: 0.90,
    11: 1.05,
    12: 1.20,  # 겨울 난방
}


def gen_amount(expense_type: str, target_month: date,
               monthly_revenue: float, rng: random.Random) -> int:
    """
    항목 성격에 맞게 금액 생성
    - fixed      : 기준값 ± cv (소폭 변동)
    - seasonal   : 기준값 × 계절가중치 ± cv
    - sales_linked: 매출 비례
    - random     : 기준값 ± cv (큰 변동)
    """
    cfg  = EXPENSE_CONFIG[expense_type]
    base = cfg["base"]
    cv   = cfg["cv"]
    kind = cfg["type"]

    # 정규분포 노이즈 (±cv 범위)
    noise = rng.gauss(1.0, cv)
    noise = max(1.0 - cv * 2, min(1.0 + cv * 2, noise))  # 극단값 클램핑

    if kind == "fixed":
        amount = base * noise

    elif kind == "seasonal":
        seasonal = SEASONAL_WEIGHT.get(target_month.month, 1.0)
        amount = base * seasonal * noise

    elif kind == "sales_linked":
        # 매출 10억 기준으로 정규화 후 비례
        revenue_ratio = (monthly_revenue / 1_000_000_000) if monthly_revenue > 0 else 1.0
        revenue_ratio = max(0.5, min(2.0, revenue_ratio))  # 0.5x ~ 2.0x
        amount = base * revenue_ratio * noise

    else:  # random
        amount = base * noise

    return max(0, round(amount))


def get_connection():
    try:
        conn = mariadb.connect(**DB_CONFIG)
        print(f"✅ DB 연결: {DB_CONFIG['host']} / {DB_CONFIG['database']}")
        return conn
    except mariadb.Error as e:
        print(f"❌ DB 연결 실패: {e}")
        sys.exit(1)


def main():
    conn   = get_connection()
    cursor = conn.cursor()

    # ── 1. 생성 대상 월 범위 결정 ─────────────────────────
    # yearly_sales 첫 달 ~ 현재 월
    cursor.execute("SELECT MIN(sale_date), MAX(sale_date) FROM yearly_sales")
    (min_sale, max_sale) = cursor.fetchone()

    if min_sale is None:
        print("⚠️  yearly_sales 데이터 없음. attendance 기준으로 대체합니다.")
        cursor.execute("SELECT MIN(work_date), MAX(work_date) FROM attendance")
        (min_sale, max_sale) = cursor.fetchone()

    start_month = date(min_sale.year, min_sale.month, 1)
    end_month   = date.today().replace(day=1)  # 현재 월 포함

    print(f"📅 생성 범위: {start_month} ~ {end_month}")

    # ── 2. 이미 존재하는 (expense_type, 월) 세트 조회 ─────
    cursor.execute("""
        SELECT expense_type, DATE_FORMAT(expense_date, '%Y-%m-01')
          FROM operating_expenses
         WHERE expense_type <> '급여'
    """)
    existing = set((row[0], str(row[1])) for row in cursor.fetchall())
    print(f"⏭  기존 레코드: {len(existing)}개 (건너뜀)")

    # ── 3. 월별 매출 조회 (물류비 연동용) ─────────────────
    cursor.execute("""
        SELECT DATE_FORMAT(sale_date, '%Y-%m-01') AS month,
               COALESCE(SUM(revenue), 0) AS revenue
          FROM yearly_sales
         GROUP BY 1
    """)
    monthly_revenue = {str(row[0]): float(row[1]) for row in cursor.fetchall()}

    # ── 4. 월 순회하며 누락 항목만 INSERT ─────────────────
    insert_sql = """
        INSERT INTO operating_expenses (expense_type, amount, expense_date)
        VALUES (%s, %s, %s)
    """

    total_inserted = 0
    total_skipped  = 0
    cur_month = start_month

    while cur_month <= end_month:
        month_str    = cur_month.strftime("%Y-%m-01")
        # 매출이 없는 달은 평균 매출로 대체
        revenue      = monthly_revenue.get(month_str, 800_000_000)
        # 월별 고정 시드 (같은 월 재실행 시 동일한 값 생성)
        rng = random.Random(cur_month.year * 100 + cur_month.month + 42)

        # 해당 월의 expense_date (25일 기준으로 통일)
        expense_date = cur_month.replace(day=25).strftime("%Y-%m-%d")

        month_inserted = 0
        for expense_type in EXPENSE_CONFIG:
            key = (expense_type, month_str)
            if key in existing:
                total_skipped += 1
                continue

            amount = gen_amount(expense_type, cur_month, revenue, rng)
            cursor.execute(insert_sql, (expense_type, amount, expense_date))
            total_inserted += 1
            month_inserted += 1

        if month_inserted > 0:
            print(f"  📝 {cur_month.strftime('%Y-%m')} → {month_inserted}개 항목 생성")

        cur_month += relativedelta(months=1)

    conn.commit()

    # ── 5. 결과 요약 ──────────────────────────────────────
    print(f"""
╔══════════════════════════════════════════╗
║        판관비 자동생성 완료              ║
╠══════════════════════════════════════════╣
║  생성 기간  : {str(start_month)} ~ {str(end_month)}    ║
║  삽입 레코드: {total_inserted:>5}개                   ║
║  건너뜀     : {total_skipped:>5}개 (이미 존재)        ║
╚══════════════════════════════════════════╝

💡 급여 항목은 attendance 트리거가 자동 처리합니다.
""")

    # ── 6. 생성 결과 샘플 출력 ────────────────────────────
    cursor.execute("""
        SELECT expense_type,
               COUNT(*) AS months,
               FORMAT(MIN(amount), 0) AS min_amt,
               FORMAT(AVG(amount), 0) AS avg_amt,
               FORMAT(MAX(amount), 0) AS max_amt
          FROM operating_expenses
         WHERE expense_type <> '급여'
         GROUP BY expense_type
         ORDER BY AVG(amount) DESC
    """)
    rows = cursor.fetchall()
    print(f"{'항목':<10} {'월수':>5} {'최소':>18} {'평균':>18} {'최대':>18}")
    print("─" * 75)
    for r in rows:
        print(f"{r[0]:<10} {r[1]:>5} {r[2]:>18} {r[3]:>18} {r[4]:>18}")

    cursor.close()
    conn.close()


if __name__ == "__main__":
    main()
