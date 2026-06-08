package com.inno.servlettask.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Прокси-класс для Connection
 * Перехватывает вызов метода close() для возврата соединения в пул вместо реального закрытия
 * Без использования synchronized и volatile
 */
public class ProxyConnection implements Connection {
    private static final Logger logger = LogManager.getLogger(ProxyConnection.class);

    private final Connection realConnection;
    private final ConnectionPool connectionPool;
    private long lastUsedTime;
    private long createdAt;
    private boolean isClosed = false;

    /**
     * Конструктор прокси-соединения
     * @param realConnection реальное соединение с БД
     * @param connectionPool пул соединений
     */
    public ProxyConnection(Connection realConnection, ConnectionPool connectionPool) {
        this.realConnection = realConnection;
        this.connectionPool = connectionPool;
        this.createdAt = System.currentTimeMillis();
        this.lastUsedTime = this.createdAt;
        logger.debug("ProxyConnection created for {} at {}", realConnection, this.createdAt);
    }

    /**
     * Обновление времени последнего использования
     */
    public void updateLastUsedTime() {
        this.lastUsedTime = System.currentTimeMillis();
    }

    /**
     * Получение времени последнего использования
     */
    public long getLastUsedTime() {
        return lastUsedTime;
    }

    /**
     * Получение времени создания
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Проверка, не закрыто ли соединение
     */
    public boolean isProxyClosed() {
        return isClosed;
    }

    /**
     * Реальное закрытие соединения (вызывается при завершении работы пула)
     */
    public void reallyClose() {
        if (!isClosed) {
            try {
                if (!realConnection.isClosed()) {
                    realConnection.close();
                    logger.debug("Real connection closed");
                }
                isClosed = true;
            } catch (SQLException e) {
                logger.error("Error closing real connection", e);
            }
        }
    }

    /**
     * Переопределенный метод close - вместо закрытия возвращает соединение в пул
     */
    @Override
    public void close() throws SQLException {
        if (!isClosed) {
            logger.debug("Returning connection to pool");
            /*connectionPool.returnConnection(this);*/
        }
    }

    // Делегирование всех остальных методов реальному соединению

    @Override
    public Statement createStatement() throws SQLException {
        updateLastUsedTime();
        return realConnection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        updateLastUsedTime();
        return realConnection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        updateLastUsedTime();
        realConnection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        updateLastUsedTime();
        return realConnection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        updateLastUsedTime();
        realConnection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        updateLastUsedTime();
        realConnection.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed || realConnection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        updateLastUsedTime();
        return realConnection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        updateLastUsedTime();
        realConnection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        updateLastUsedTime();
        return realConnection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        updateLastUsedTime();
        realConnection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        updateLastUsedTime();
        return realConnection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        updateLastUsedTime();
        realConnection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        updateLastUsedTime();
        return realConnection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        updateLastUsedTime();
        return realConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        updateLastUsedTime();
        realConnection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        updateLastUsedTime();
        return realConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        updateLastUsedTime();
        return realConnection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        updateLastUsedTime();
        realConnection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        updateLastUsedTime();
        realConnection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        updateLastUsedTime();
        return realConnection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        updateLastUsedTime();
        return realConnection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        updateLastUsedTime();
        return realConnection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        updateLastUsedTime();
        realConnection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        updateLastUsedTime();
        realConnection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        updateLastUsedTime();
        return realConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        updateLastUsedTime();
        return realConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        updateLastUsedTime();
        return realConnection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        updateLastUsedTime();
        return realConnection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        updateLastUsedTime();
        return realConnection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        updateLastUsedTime();
        return realConnection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        updateLastUsedTime();
        return realConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        try {
            updateLastUsedTime();
            realConnection.setClientInfo(name, value);
        } catch (SQLClientInfoException e) {
            throw e;
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            updateLastUsedTime();
            realConnection.setClientInfo(properties);
        } catch (SQLClientInfoException e) {
            throw e;
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        updateLastUsedTime();
        return realConnection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        updateLastUsedTime();
        return realConnection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        updateLastUsedTime();
        return realConnection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        updateLastUsedTime();
        return realConnection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        updateLastUsedTime();
        realConnection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        updateLastUsedTime();
        return realConnection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        updateLastUsedTime();
        realConnection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        updateLastUsedTime();
        realConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        updateLastUsedTime();
        return realConnection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        updateLastUsedTime();
        return realConnection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        updateLastUsedTime();
        return realConnection.isWrapperFor(iface);
    }
}