package com.ak4.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewsUtility {

    public static String getViewAsString(Object val, String viewName){
        String retVal = null;
        try {
            if("external".equalsIgnoreCase(viewName)){
                retVal =  new ObjectMapper()
                        .writerWithView(Views.ExternalView.class)
                        .writeValueAsString(val);
            } else if("internal".equalsIgnoreCase(viewName)){
                retVal = new ObjectMapper()
                        .writerWithView(Views.InternalView.class).writeValueAsString(val);
            } else if("Distributor".equalsIgnoreCase(viewName)){
                retVal = new ObjectMapper()
                        .writerWithView(Views.DistributorView.class).writeValueAsString(val);
            } else if("DVAR".equalsIgnoreCase(viewName)){
                retVal = new ObjectMapper()
                        .writerWithView(Views.DVARView.class).writeValueAsString(val);
            }  else if("IVAR".equalsIgnoreCase(viewName)){
                retVal = new ObjectMapper()
                        .writerWithView(Views.IVARView.class).writeValueAsString(val);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return retVal;
    }

}
