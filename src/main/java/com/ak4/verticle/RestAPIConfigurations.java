package com.ak4.verticle;

import com.ak4.constants.CommonConstants;
import com.ak4.util.RestAPIURIConfig;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import io.vertx.rxjava.ext.web.handler.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Action1;

public class RestAPIConfigurations {

    private Logger logger = LoggerFactory.getLogger(RestAPIConfigurations.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String PRAGMA = "Pragma";

    private Vertx vertx;

    private final JsonObject config;

    private long updateReplyTimeOut;
    private long eventBusReplyMessageTimeoutMillis;
    private long socketTimeoutForCreateCase = 60000;
    private int eventBusReplyTimedoutRetry;

    public RestAPIConfigurations(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;

        try {
            updateReplyTimeOut = config.getInteger("updateReplyTimeOut");
            eventBusReplyMessageTimeoutMillis = config.getInteger("eventBusReplyMessageTimeoutMillis");
            eventBusReplyTimedoutRetry = config.getInteger("eventBusReplyTimedoutRetry");
            socketTimeoutForCreateCase = config.getInteger("fusionWsTimeoutMillis");
        } catch (Exception ex) {
            logger.error("Error while setting the number of retry param", ex);
        }
    }

    public Router configRouter() {
        Router router = Router.router(vertx);

        router.route()
                .handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
                        .allowedMethod(HttpMethod.PUT).allowedMethod(HttpMethod.OPTIONS)
                        .allowedHeader("Content-Type").allowedHeader(AUTHORIZATION).allowedHeader(PRAGMA));

        router.route().handler(BodyHandler.create());

        io.vertx.ext.web.Router routerNonRx = router.getDelegate();
        router.route(HttpMethod.GET, RestAPIURIConfig.GET_EMP_BY_EMPID_URI).handler(this::reqHandler);
        return router;
    }

    private void reqHandler(RoutingContext ctx) {

        try {
            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.setSendTimeout(eventBusReplyMessageTimeoutMillis);
            RetryHandler errorHandler = new RetryHandler(ctx, eventBusReplyTimedoutRetry, deliveryOptions);
            handleRequest(ctx, deliveryOptions, errorHandler);
        } catch (Exception ex) {
            logger.error("Error", ex);
            logger.error("Error for path : ", ctx.currentRoute().getPath());
            ctx.request().response().headers().add("Content-Type", "application/json");
            addNoCacheHeaders(ctx);
            ctx.request().response().setStatusCode(500);
            ctx.request().response().end("{\"message\": \"Unable to process the request\"}");

        }
    }

    private void handleRequest(RoutingContext ctx, DeliveryOptions deliveryOptions, RetryHandler errorHandler) throws Exception {
        String uriVal = RestAPIURIConfig.getURIValue(ctx.currentRoute().getPath());

        if (uriVal == null) {
            uriVal = ctx.currentRoute().getPath();
        }

        logger.info("uriPath " + uriVal);

        switch (uriVal) {
            case RestAPIURIConfig.GET_EMP_BY_EMPID_URI_VAL:
                if(HttpMethod.GET.equals(ctx.request().method())) {
                    JsonObject obj = new JsonObject();
                    obj.put("empId", ctx.request().getParam("empId"));
                    vertx.eventBus().sendObservable(CommonConstants.MSG_GET_EMP_BY_EMP_ID, obj, deliveryOptions)
                            .subscribe(new ResponseHandler(ctx), errorHandler);
                } else if(HttpMethod.PUT.equals(ctx.request().method())) {
                    JsonObject obj = new JsonObject();
                    obj.put("empId", ctx.request().getParam("empId"));
                    vertx.eventBus().sendObservable(CommonConstants.MSG_UPDATE_EMP_BY_EMP_ID, obj, deliveryOptions.setSendTimeout(updateReplyTimeOut))
                            .subscribe(new ResponseHandler(ctx), new ErrorHandler(ctx));
                }
                break;
        }

        }


    private class ResponseHandler implements Action1<Message<Object>> {

        private RoutingContext ctx;

        public ResponseHandler(RoutingContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void call(Message<Object> replyMessage) {
            JsonObject message = (JsonObject) replyMessage.body();
            ctx.request().response().headers().add("Content-Type", "application/json");
            addNoCacheHeaders(ctx);
            if(message.containsKey(CommonConstants.HTTP_RESPONSE_CODE)) {
                ctx.request().response().setStatusCode(message.getInteger(CommonConstants.HTTP_RESPONSE_CODE));
            }

            if(message.containsKey(CommonConstants.HTTP_DATA_KEY)) {
                ctx.request().response().end(message.getString(CommonConstants.HTTP_DATA_KEY));
            }
        }
    }

    private class ErrorHandler implements Action1<Throwable> {
        private RoutingContext ctx;

        public ErrorHandler(RoutingContext ctx) {
            this.ctx = ctx;
        }

        public void call(Throwable th) {

            logger.error("Error", th);

            ctx.request().response().headers().add("Content-Type", "application/json");
            addNoCacheHeaders(ctx);
            ctx.request().response().setStatusCode(500);

            ctx.request().response()
                    .end("{\"error\": \"Unable to process the request\", \"message\":\"" + th.getMessage() + "\"}");

        };
    }

    private void addNoCacheHeaders(RoutingContext ctx) {
        ctx.request().response().headers().add("Cache-Control", "no-cache, no-store, must-revalidate")
                .add("Pragma", "no-cache").add("Expires", "0");
    }

    private class RetryHandler implements Action1<Throwable> {

        private int retry;
        private RoutingContext ctx;
        private DeliveryOptions deliveryOptions;

        public RetryHandler(RoutingContext ctx, int retry, DeliveryOptions deliveryOptions) {
            this.ctx = ctx;
            this.retry = retry;
            this.deliveryOptions = deliveryOptions;
        }


        @Override
        public void call(Throwable ex) {
            if(ex instanceof ReplyException) {
                ReplyException replyException = (ReplyException) ex;

                if(ReplyFailure.TIMEOUT.equals(replyException.failureType())) {
                    if(retry > 0) {
                        --retry;

                        try {
                            logger.error(replyException.getMessage());
                            logger.error("Retry -> " + (eventBusReplyTimedoutRetry - retry));
                            handleRequest(ctx, deliveryOptions, this);
                        } catch (Exception ex2) {
                            sendResponseFromGenericException(ctx, ex2);
                        }
                    } else {
                        sendResponseFromGenericException(ctx, ex);
                    }
                } else {
                    sendResponseFromGenericException(ctx, ex);
                }
            } else {
                sendResponseFromGenericException(ctx, ex);
            }
        };
    }

    private void sendResponseFromGenericException(RoutingContext ctx, Throwable ex) {

        setNoCacheHeaders(ctx);
        ctx.request().response().headers().add("Content-Type", "application/json");
        ctx.request().response().setStatusCode(500);

        ctx.request().response()
                .end("{\"error\": \"Unable to process the request\", \"message\":\"" + ex.getMessage() + "\"}");
    }

    private void setNoCacheHeaders(RoutingContext ctx) {
        ctx.request().response().headers().add("Cache-Control", "no-cache, no-store, must-revalidate");
        ctx.request().response().headers().add("Pragma", "no-cache");
        ctx.request().response().headers().add("Expires", "0");

    }

}
