package com.ak4.init;

import com.ak4.common.dao.helper.JdbcClientFactory;
import com.ak4.constants.CommonConstants;
import com.ak4.util.JsonConfigFileLoader;
import com.ak4.verticle.EmpManagerRestWorkerVerticle;
import com.ak4.verticle.HttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLauncher {

    private static Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        JsonObject config = JsonConfigFileLoader.loadFile();

        logger.info("Starting the http server verticle");

        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);

        vertx.deployVerticle(HttpServerVerticle.class.getName(), deploymentOptions, res -> {
            if(res.succeeded()) {
                try {
                    logger.warn("[HttpServerVerticle] is deployed");

                    vertx.sharedData().getLocalMap(CommonConstants.VERTX_LOCAL_MAP).put(CommonConstants.VERTX_APP_CONFIG, config);

                    JdbcClientFactory.getInstance(vertx);

                    DeploymentOptions deploymentOptions1 = new DeploymentOptions().setWorker(true).setMultiThreaded(true).setConfig(config);

                    vertx.deployVerticle(EmpManagerRestWorkerVerticle.class.getName(), deploymentOptions1, res1 -> {
                        logger.info("" + res1.toString());
                        logger.info("" + res1.result());
                        logger.info("" + res1.cause());
                        logger.info(res1.toString());

                        if(res1.succeeded()) {
                            logger.warn("[EmpManagerRestWorkerVerticle] is deployed");
                        } else {
                            logger.error("Error", res1.cause());
                            logger.error("Deployment of [CaseManagerRestWorkerVerticle] is unsuccessful.", res1.cause());
                        }
                    });
                } catch (Exception e) {
                    logger.error("Exception while starting the application", e);
                }
            } else {
                logger.error("Deployment of [HttpServerVerticle] is unsuccessful.", res.cause());
            }
        });

    }
}
