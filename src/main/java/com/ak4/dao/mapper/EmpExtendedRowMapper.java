package com.ak4.dao.mapper;

import com.ak4.dao.helper.RowMapper;
import com.ak4.data.Emp;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmpExtendedRowMapper extends EmpRowMapper {

    private static final Logger log = LoggerFactory
            .getLogger(EmpExtendedRowMapper.class);

    public Emp mapRow(JsonObject rs){
        Emp emp = null;
        try{
            emp = super.mapRow(rs);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return emp;
    }
}
