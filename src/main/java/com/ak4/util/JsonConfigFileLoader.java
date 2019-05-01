package com.ak4.util;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonConfigFileLoader {

    private static Logger logger = LoggerFactory.getLogger(JsonConfigFileLoader.class);
    static String filePath = "C:\\IntelliJ\\MyWorkspace\\vertx-rest-mysql\\src\\main\\resources\\config-dev.json";

    public static JsonObject loadFile() {
        JsonObject config = new JsonObject();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String str = null;
            while((str = reader.readLine()) != null) {
                sb.append(str);
            }
            config = new JsonObject(sb.toString());
        } catch (Exception ex) {
            logger.error("Error while reading the file contents", ex);
        }
        return config;
    }
}
