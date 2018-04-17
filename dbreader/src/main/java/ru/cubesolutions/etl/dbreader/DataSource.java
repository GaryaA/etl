package ru.cubesolutions.etl.dbreader;

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
        config.setDriverClassName(Config.JDBC_DRIVER);
        config.setJdbcUrl(Config.JDBC_URL);
        config.setUsername(Config.JDBC_USER);
        config.setPassword(Config.JDBC_PASSWORD);
        config.setMaximumPoolSize(1);
        config.setReadOnly(true);
//        config.setLeakDetectionThreshold(3000);
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
