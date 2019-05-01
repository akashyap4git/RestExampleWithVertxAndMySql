package com.ak4.verticle;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);

    @Override
    public void start() throws Exception {
        super.start();

        RestAPIConfigurations restAPIConfig = new RestAPIConfigurations(vertx, config().getJsonObject("appConfig"));

        Router router = restAPIConfig.configRouter();

        HttpServerOptions options = new HttpServerOptions().setCompressionSupported(true);

        HttpServer server = vertx.createHttpServer(options);
        server.requestHandler(router::accept).listen(config().getJsonObject("appConfig").getInteger("port"));

        logger.info("Started the http server listening to port[" + config().getJsonObject("appConfig").getInteger("port") + "]");

    }


}
