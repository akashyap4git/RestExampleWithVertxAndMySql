package com.ak4.common.dao.helper;

import com.ak4.constants.CommonConstants;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcClientFactory {

    private static Logger logger = LoggerFactory.getLogger(JdbcClientFactory.class);

    private static JdbcTemplate jdbcTemplate = null;

    private static ComboPooledDataSource pooledDataSource;

    private static DataSource ds;


    public static JdbcTemplate getInstance(Vertx vertx) {
        if(jdbcTemplate == null) {
            synchronized (vertx) {
                if(jdbcTemplate == null) {
                    logger.warn("Creating the data source");

                    JsonObject config = ((JsonObject) vertx.sharedData().getLocalMap(CommonConstants.VERTX_LOCAL_MAP)
                                                            .get(CommonConstants.VERTX_APP_CONFIG)).getJsonObject("dbConfig");

                    JsonObject mysqlConfig = config.getJsonObject("mysql");

                    JDBCClient jdbcClient = initDataSource(vertx, mysqlConfig);

                    jdbcTemplate = new JdbcTemplate(jdbcClient);
                }
            }
        }
        return jdbcTemplate;
    }

    private static JDBCClient initDataSource(Vertx vertx, JsonObject config) {

        JDBCClient jdbcClient = null;
        Logger logger = LoggerFactory.getLogger("Init Data Source");

        logger.warn("Creating the database connection...");

        Properties props = getPropsFromJson(config.getJsonObject("properties"));

        try {
            io.vertx.ext.jdbc.JDBCClient jdbcDelegate = null;
            C3P0DataSourceProvider cp = new C3P0DataSourceProvider();
            pooledDataSource = (ComboPooledDataSource) cp.getDataSource(config);
            if (props != null) {
                pooledDataSource.setProperties(props);
            }
            jdbcDelegate = io.vertx.ext.jdbc.JDBCClient
                    .create((io.vertx.core.Vertx) vertx.getDelegate(), pooledDataSource);

            jdbcClient = new JDBCClient(jdbcDelegate);

            String name = config.getString("name");

            jdbcClient.getConnectionObservable().subscribe(conn -> {
                logger.warn(name + " connection is successful");
            } , ex -> {
                logger.warn(name + " connection is failed");
            });
        }
        catch (SQLException sqlException) {
            logger.error("Error while creating the data source", sqlException);
        }
        return jdbcClient;
    }

    private static Properties getPropsFromJson(JsonObject json) {

        Properties props = null;

        if (json != null) {

            props = new Properties();

            for (String key : json.getMap().keySet()) {
                props.put(key, json.getMap().get(key));
            }
        }
        return props;
    }

}
