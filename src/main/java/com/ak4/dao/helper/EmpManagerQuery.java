package com.ak4.dao.helper;

import com.ak4.constants.CommonConstants;

public class EmpManagerQuery {

    public static final String GET_EMP_BY_EMP_ID = "SELECT "+"*"+" FROM EMP WHERE "+ CommonConstants.EMP_ID+" = ?";
}
