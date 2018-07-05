package ru.cubesolutions.etl.clickhousepusher;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Garya on 02.02.2018.
 */
public class DataSource {

    private HikariConfig config = new HikariConfig();
    private HikariDataSource ds;

    public DataSource(DestConfig appConfig) {
        config.setDriverClassName(appConfig.getJdbcDriver());
        config.setJdbcUrl(appConfig.getJdbcUrl());
        config.setUsername(appConfig.getJdbcUser());
        config.setPassword(appConfig.getJdbcPassword());
        config.setMaximumPoolSize(1);
        config.setReadOnly(false);
//        config.addDataSourceProperty("cachePrepStmts", "false");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
