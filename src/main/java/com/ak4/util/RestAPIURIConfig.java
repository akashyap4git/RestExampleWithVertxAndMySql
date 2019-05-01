package com.ak4.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RestAPIURIConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestAPIURIConfig.class);

    public static final String BASE_CONTEXT_URI = "/api";
    public static final String EMP_MANAGER_CONTEXT_URI = BASE_CONTEXT_URI + "/emp-manager";
    public static final String GET_EMPS_URI = EMP_MANAGER_CONTEXT_URI + "/emps";
    public static final String GET_EMPS_URI_VAL = "GET_EMPS";

    public static final String GET_EMP_BY_EMPID_URI = GET_EMPS_URI + "/:empId";
    public static final String GET_EMP_BY_EMPID_URI_VAL = "GET_EMP_BY_EMPID";

    private static Map<String, String> uriMap = new HashMap<String, String>();

    static {
        uriMap.put(GET_EMPS_URI, GET_EMPS_URI_VAL);
        uriMap.put(GET_EMP_BY_EMPID_URI, GET_EMP_BY_EMPID_URI_VAL);
    }

    public static String getURIValue(String uri) throws Exception {
        return uriMap.get(uri);
    }

    
}
