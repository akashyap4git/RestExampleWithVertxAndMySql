package com.ak4.common.dao.helper;

import com.ak4.dao.helper.RowMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;


public class JdbcTemplate {

    private Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

    private JDBCClient jdbcClient;

    public JdbcTemplate(JDBCClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }

    public <T> Observable<List<T>> queryObservable(String sql, JsonArray sqlParams, RowMapper<T> mapper) {
        return Observable.create(sub -> {

            long start_time = System.currentTimeMillis();

            jdbcClient.getConnectionObservable().subscribe(conn -> {

                long start_time2 = System.currentTimeMillis();

                conn.queryWithParamsObservable(sql, sqlParams).subscribe(rs -> {

                    long dbRespTime	= System.currentTimeMillis() - start_time;

                    List<T> list = new ArrayList<>();
                    if (rs != null && rs.getNumRows() > 0) {
                        for (JsonObject obj : rs.getRows()) {
                            list.add(mapper.mapRow(obj));
                        }
                    }

//					if(logger.isInfoEnabled()){
                    long end_time = System.currentTimeMillis() - start_time;
                    logger.info(sql + " "+ sqlParams + " Time taken for DB operation : (" + dbRespTime + ", "+ end_time+")" + "(" + (System.currentTimeMillis() - start_time2) +")");
//					}

                    conn.close();
                    sub.onNext(list);
                    sub.onCompleted();
                } , ex -> {
                    logger.error("DB Error : " + sql);
                    conn.close();
                    sub.onError(ex);
                } , () -> conn.close());

            } , ex -> sub.onError(ex));
        });
    }
}
