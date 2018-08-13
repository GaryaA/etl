package ru.cubesolutions.etl.dbreader;

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

    public DataSource(SrcConfig appAppConfig) {
        config.setDriverClassName(appAppConfig.getJdbcDriver());
        config.setJdbcUrl(appAppConfig.getJdbcUrl());
        config.setUsername(appAppConfig.getJdbcUser());
        config.setPassword(appAppConfig.getJdbcPassword());
        config.setMaximumPoolSize(1);
        config.setReadOnly(true);
//        config.addDataSourceProperty("cachePrepStmts", "false");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
