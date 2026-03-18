-- ============================================================
-- 근태 출퇴근 시간 보정 스크립트
-- Python 스크립트가 "0 days HH:MM:SS" 형식으로 잘못 저장한 경우
-- check_in, check_out 이 모두 00:00:00 인 레코드를 삭제하고
-- sync_all_data.py 를 다시 실행하면 됩니다.
-- ============================================================

-- 1. 잘못된 데이터 확인 (먼저 실행해서 개수 확인)
SELECT COUNT(*) AS bad_records
FROM attendance
WHERE check_in = '00:00:00' OR check_in IS NULL;

-- 2. 잘못 저장된 attendance 레코드 삭제
--    (Python 스크립트로 생성한 레코드 중 check_in이 00:00:00인 것)
DELETE FROM attendance
WHERE check_in = '00:00:00';

-- 3. 삭제 후 sync_all_data.py 를 다시 실행하면 올바른 시간으로 재생성됩니다.
