package ru.cubesolutions.etl.clickhousepusher;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Garya on 02.02.2018.
 */
public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setDriverClassName(AppConfig.getInstance().getJdbcDriver());
        config.setJdbcUrl(AppConfig.getInstance().getJdbcUrl());
        config.setUsername(AppConfig.getInstance().getJdbcUser());
        config.setPassword(AppConfig.getInstance().getJdbcPassword());
        config.setMaximumPoolSize(1);
        config.setReadOnly(false);
//        config.addDataSourceProperty("cachePrepStmts", "false");
//        config.addDataSourceProperty("prepStmtCacheSize", "250");
//        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    public DataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
