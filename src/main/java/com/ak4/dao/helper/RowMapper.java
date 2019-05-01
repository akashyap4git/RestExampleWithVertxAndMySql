package com.ak4.dao.helper;

import io.vertx.core.json.JsonObject;

public interface RowMapper<T> {

    T mapRow(JsonObject rs);
}
