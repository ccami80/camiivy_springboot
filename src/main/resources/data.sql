-- 테스트용 사용자 및 계정 (로그인 테스트용)
-- socialId '12345678'로 카카오 로그인 테스트 가능 (실제 OAuth code 필요)
INSERT INTO user_module (user_id, email, name, phone, address, status, created_id, created_at, updated_id, updated_at)
VALUES ('usr_test_1', NULL, NULL, NULL, NULL, 'ACTIVE', 'system', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP);

INSERT INTO user_module_account (user_id, social_provider, social_id, phone, email, name, status, created_id, created_at, updated_id, updated_at)
VALUES ('usr_test_1', 'KAKAO', '12345678', NULL, NULL, NULL, 'ACTIVE', 'system', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP);
