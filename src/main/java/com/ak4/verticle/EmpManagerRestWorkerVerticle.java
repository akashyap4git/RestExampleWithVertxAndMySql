package com.ak4.verticle;

import com.ak4.common.dao.helper.JdbcClientFactory;
import com.ak4.constants.CommonConstants;
import com.ak4.dao.EmpManagerDAO;
import com.ak4.dao.impl.EmpManagerDAOImpl;
import com.ak4.util.ResponseUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmpManagerRestWorkerVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(EmpManagerRestWorkerVerticle.class);

    private EmpManagerDAO empManagerDAO;

    @Override
    public void start() throws Exception {
        super.start();

        empManagerDAO = new EmpManagerDAOImpl(JdbcClientFactory.getInstance(vertx), vertx);
        vertx.eventBus().consumer(CommonConstants.MSG_GET_EMP_BY_EMP_ID, this::getEmpByEmpId);

    }

    private void getEmpByEmpId(Message<JsonObject> message) {



        JsonObject jsonData = message.body();

        long start_time = System.currentTimeMillis();

        String empId = jsonData.getString("empId");

        try {


            empManagerDAO.getEmpByEmpId(empId).subscribe(_emp -> {
                JsonObject responseMessage = null;
                if (_emp != null) {
                    responseMessage = ResponseUtil.createResponseByView(_emp, "external");
                } else {
                    String caseData = "Emp with the id '" + empId + "' is not found";
                    responseMessage = ResponseUtil.createResourceNotFoundResponse(caseData);
                }
                logger.warn("Time taken by getEmpByEmpId  :: " + (System.currentTimeMillis() - start_time));
                message.reply(responseMessage);
            }, ex -> {
                logger.error("Failed Fetching emp for emp id " + empId, ex);
                ex = new Throwable(CommonConstants.FIVE_HUNDRED_ERROR_MESSAGE);
                JsonObject responseMessage = ResponseUtil.createErrorResponse(ex);
                message.reply(responseMessage);
            });

        } catch (Exception e) {
            JsonObject responseMessage = ResponseUtil.createErrorResponse(e);
            message.reply(responseMessage);
        }
    }
}
