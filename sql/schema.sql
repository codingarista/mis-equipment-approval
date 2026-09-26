-- ============================================
-- 設備維修申請系統 資料庫結構
-- 資料庫名稱: mis_equipment_approval
-- ============================================

-- 部門主檔
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 使用者(申請人、主管、維修人員、品管、經理、管理員)
-- 業務規則:品管(QC)與經理(MANAGER)的處理範圍限定在自己所屬部門,由程式邏輯控制,非資料庫欄位限制
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(50) NOT NULL,
    role ENUM('APPLICANT','SUPERVISOR','TECHNICIAN','QC','MANAGER','ADMIN') NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 設備主檔
CREATE TABLE equipment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    equipment_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    custodian_id INT NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (custodian_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- 異常/維修申請單(主體)
-- status 對應完整流程圖的 9 個狀態
CREATE TABLE tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    equipment_id INT NOT NULL,
    applicant_id INT NOT NULL,
    current_technician_id INT NULL,
    description TEXT NOT NULL,
    severity ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    status ENUM(
        'PENDING_REPAIR',
        'IN_PROGRESS',
        'RETURNED_TO_APPLICANT',
        'WITHDRAWN',
        'PENDING_QC',
        'NOTIFIED',
        'COMPLETED',
        'ESCALATED',
        'CLOSED_BY_MANAGER'
    ) NOT NULL DEFAULT 'PENDING_REPAIR',
    repair_result ENUM('REPAIRED','SCRAP_RECOMMENDED') NULL,
    rejection_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at DATETIME NULL,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (applicant_id) REFERENCES users(id),
    FOREIGN KEY (current_technician_id) REFERENCES users(id)
);

-- 簽核/處理歷程紀錄(每次狀態變更都留下一筆紀錄,是查詢單據完整歷程的依據)
CREATE TABLE approval_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    actor_id INT NOT NULL,
    action ENUM('SUBMIT','SUPERVISOR_APPROVE','SUPERVISOR_REJECT',
                'TECH_CLAIM','TECH_RETURN','TECH_COMPLETE','APPLICANT_WITHDRAW',
                'APPLICANT_RESUBMIT','QC_APPROVE','APPLICANT_CONFIRM',
                'APPLICANT_DISPUTE','MANAGER_CLOSE') NOT NULL,
    comment VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    FOREIGN KEY (actor_id) REFERENCES users(id)
);

-- 通知紀錄(設備保管者/申請人的已讀狀態)
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    recipient_id INT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);