package com.arist.eform.mis_equipment_approval.model;

public class Equipment {

    private Integer id;
    private String equipmentNo;
    private String name;
    private Integer custodianId;
    private Integer departmentId;

    public Equipment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEquipmentNo() {
        return equipmentNo;
    }

    public void setEquipmentNo(String equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCustodianId() {
        return custodianId;
    }

    public void setCustodianId(Integer custodianId) {
        this.custodianId = custodianId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}