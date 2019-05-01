package com.ak4.util;

import com.ak4.common.beans.Errors;
import com.ak4.constants.CommonConstants;
import com.ak4.exceptions.ResourceNotFoundException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseUtil {

    private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    private static final String UNABLE_TO_PROCESS_THE_REQUEST_DUE_TO_THE_INTERNAL_SERVER_ERROR = "Unable to process the request, due to the internal server error";

    public static JsonObject createResponseByView(Object obj, String viewName){

        JsonObject response = new JsonObject();
        response.put(CommonConstants.HTTP_RESPONSE_CODE, 200);
        response.put(CommonConstants.HTTP_DATA_KEY, ViewsUtility.getViewAsString(obj, viewName));

        return response;
    }

    public static JsonObject createResourceNotFoundResponse(String message) {
        ResourceNotFoundException ex = new ResourceNotFoundException(message);
        return createErrorResponse(ex);
    }

    public static JsonObject createErrorResponse(Throwable ex) {

        JsonObject response = new JsonObject();

        Errors errors = new Errors();

        if (ex instanceof ResourceNotFoundException) {

            response.put(CommonConstants.HTTP_RESPONSE_CODE, 404);

            Errors.Error error = new Errors.Error();
            error.setCode(404);
            error.setMessage(ex.getMessage());

            errors.getErrors().add(error);

        }

        else {
            logger.error("", ex);
            response.put(CommonConstants.HTTP_RESPONSE_CODE, 500);
            Errors.Error error = new Errors.Error();
            error.setCode(500);
            // 500 error, log error and send generic 500 error message.
            error.setMessage(UNABLE_TO_PROCESS_THE_REQUEST_DUE_TO_THE_INTERNAL_SERVER_ERROR);
            errors.getErrors().add(error);
        }
        response.put(CommonConstants.HTTP_DATA_KEY, Json.encode(errors));

        return response;

    }
}
