package com.inno.servlettask.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public enum ConnectionPool {
    INSTANCE;
    public static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    private BlockingQueue<Connection> pool;

    public void init(String url, String user, String pass, int poolSize) {
        pool = new ArrayBlockingQueue<>(poolSize);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            for (int i = 0; i < poolSize; i++) {
                pool.add(DriverManager.getConnection(url, user, pass));
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new ExceptionInInitializerError(e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            logger.error("Interrupted while getting connection", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
        }
    }

    public void destroy() {
        for (Connection connection : pool) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing connection during pool destruction", e);
            }
        }
    }
}