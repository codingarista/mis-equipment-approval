package com.arist.eform.mis_equipment_approval.repository;

import com.arist.eform.mis_equipment_approval.model.Equipment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EquipmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public EquipmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 依照 id 查詢一台設備,找不到就回傳 null
    public Equipment findById(Integer id) {
        String sql = "SELECT id, equipment_no, name, custodian_id, department_id FROM equipment WHERE id = ?";

        List<Equipment> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Equipment equipment = new Equipment();
            equipment.setId(rs.getInt("id"));
            equipment.setEquipmentNo(rs.getString("equipment_no"));
            equipment.setName(rs.getString("name"));
            equipment.setCustodianId(rs.getInt("custodian_id"));
            equipment.setDepartmentId(rs.getInt("department_id"));
            return equipment;
        }, id);

        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}