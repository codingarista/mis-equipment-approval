-- ============================================
-- 測試資料(開發階段使用)
-- 注意:密碼欄位目前為明碼,待登入功能開發完成後改為 BCrypt 加密儲存
-- ============================================

-- 部門
INSERT INTO departments (name) VALUES
('資訊部'),
('設備部');

-- 使用者
INSERT INTO users (username, password, full_name, role, department_id) VALUES
('harry_a', 'password123', '哈利波t', 'APPLICANT', 1),
('hermione_b', 'password123', '格蘭傑妙l', 'SUPERVISOR', 1),
('ron_c', 'password123', '榮恩w', 'TECHNICIAN', 1),
('dumbledore_d', 'password123', '鄧不利d', 'QC', 1),
('mcgonagall_e', 'password123', 'm教授', 'MANAGER', 1),
('snape_f', 'password123', '石內b', 'ADMIN', 1),
('draco_g', 'password123', '跩哥馬f', 'APPLICANT', 2),
('luna_h', 'password123', '露n', 'SUPERVISOR', 2),
('neville_i', 'password123', '奈威隆b', 'TECHNICIAN', 2),
('sirius_j', 'password123', '天狼x', 'QC', 2),
('lupin_k', 'password123', '路平j', 'MANAGER', 2),
('hagrid_l', 'password123', '海格l', 'ADMIN', 2);

-- 設備
INSERT INTO equipment (equipment_no, name, custodian_id, department_id) VALUES
('EQ-U-001', '電腦', 1, 1),
('EQ-U-002', '主機', 2, 1),
('EQ-U-003', '音響', 3, 1),
('EQ-D-001', 'wifi分享器', 7, 2),
('EQ-D-002', '電腦', 8, 2),
('EQ-D-003', '主機', 9, 2);