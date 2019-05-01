package com.ak4.dao;

import com.ak4.common.dao.helper.JdbcTemplate;

public abstract class BaseDAO {

    protected JdbcTemplate jdbcClient;

    public BaseDAO(JdbcTemplate jdbcClient) {
        this.jdbcClient = jdbcClient;
    }
}
