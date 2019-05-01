package com.ak4.dao.mapper;

import com.ak4.dao.helper.RowMapper;
import com.ak4.data.Emp;
import io.vertx.core.json.JsonObject;

public class EmpRowMapper implements RowMapper<Emp> {

    public Emp mapRow(JsonObject rs){

        Emp e = new Emp();
        e.setEmpId(rs.getString("empId"));
        e.setEmpName(rs.getString("empName"));
        e.setEmpSalary(rs.getString("empSalary"));
        e.setEmpCity(rs.getString("empCity"));
        return e;
    }
}
