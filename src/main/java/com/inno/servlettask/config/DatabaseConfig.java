package com.inno.servlettask.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    private static final String DB_DRIVER = "db.driver";
    private static final String DB_PROPERTIES = "db.properties";
    private static final String DB_URL = "db.url";
    private static final String DB_USERNAME = "db.username";
    private static final String DB_PASSWORD = "db.password";
    private static final String DB_POOL_MAX_SIZE = "db.pool.maxSize";
    private static final String DB_POOL_MIN_IDLE = "db.pool.minIdle";
    private static final String DB_POOL_CONNECTION_TIMEOUT = "db.pool.connectionTimeout";
    private static final String DB_POOL_IDLE_TIMEOUT = "db.pool.idleTimeout";
    private static final String DB_POOL_MAX_LIFETIME = "db.pool.maxLifetime";

    private static final String DEFAULT_MAX_POOL_SIZE = "10";
    private static final String DEFAULT_MIN_IDLE = "5";
    private static final String DEFAULT_CONNECTION_TIMEOUT = "30000";
    private static final Long DEFAULT_IDLE_TIMEOUT = 600000L;
    private static final Long DEFAULT_MAX_LIFETIME = 1800000L;
    private static final Long DEFAULT_LEAK_DETECTION = 60000L;


    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(DB_PROPERTIES)) {
            Properties props = new Properties();
            props.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty(DB_URL));
            config.setUsername(props.getProperty(DB_USERNAME));
            config.setPassword(props.getProperty(DB_USERNAME));
            config.setDriverClassName(props.getProperty(DB_DRIVER));

            config.setMaximumPoolSize(Integer.parseInt(props.getProperty(DB_POOL_MAX_SIZE, DEFAULT_MAX_POOL_SIZE)));
            config.setMinimumIdle(Integer.parseInt(props.getProperty(DB_POOL_MIN_IDLE, DEFAULT_MIN_IDLE)));
            config.setConnectionTimeout(Long.parseLong(props.getProperty(DB_POOL_CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT)));
            config.setIdleTimeout(DEFAULT_IDLE_TIMEOUT);
            config.setMaxLifetime(DEFAULT_MAX_LIFETIME);

            config.setPoolName("HikariPool");
            config.setLeakDetectionThreshold(DEFAULT_LEAK_DETECTION);

            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database pool", e);
            throw new RuntimeException("Failed to initialize database pool", e);
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
