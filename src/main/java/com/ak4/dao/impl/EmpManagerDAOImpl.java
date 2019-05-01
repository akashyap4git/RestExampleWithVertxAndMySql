package com.ak4.dao.impl;

import com.ak4.common.dao.helper.JdbcTemplate;
import com.ak4.dao.BaseDAO;
import com.ak4.dao.EmpManagerDAO;
import com.ak4.dao.helper.EmpManagerQuery;
import com.ak4.dao.mapper.EmpExtendedRowMapper;
import com.ak4.data.Emp;
import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

public class EmpManagerDAOImpl extends BaseDAO implements EmpManagerDAO {

    private static final Logger logger = LoggerFactory.getLogger(EmpManagerDAOImpl.class);


    public EmpManagerDAOImpl(JdbcTemplate jdbcClient, Vertx vertx) {
        super(jdbcClient);
    }

    @Override
    public Observable<Emp> getEmpByEmpId(String empId) {
        return Observable.create(sub -> {
            JsonArray params = new JsonArray();
            params.add(empId);
            long start_time = System.currentTimeMillis();

            logger.info("querying case details for caseID::" + empId);

            jdbcClient.queryObservable(EmpManagerQuery.GET_EMP_BY_EMP_ID, params, new EmpExtendedRowMapper())
                    .subscribe(resList -> {
                        Emp e = null;
                        if(resList != null && !resList.isEmpty()) {
                            e = resList.get(0);
                        }
                        logger.warn("Time taken to fetch emp by emp Id(super set) : " + (System.currentTimeMillis() - start_time));
                        sub.onNext(e);
                        sub.onCompleted();
                    }, ex -> sub.onError(ex));
        });
    }
}
