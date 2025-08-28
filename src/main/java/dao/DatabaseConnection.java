package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static HikariDataSource dataSource;

    static {
        try {
            // Load configuration from properties file
            Properties props = loadProperties();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("jdbc.url"));
            config.setUsername(props.getProperty("jdbc.username"));
            config.setPassword(props.getProperty("jdbc.password"));
            config.setDriverClassName(props.getProperty("jdbc.driver"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikaricp.maxPoolSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("hikaricp.minIdle", "5")));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // Initialize connection pool
            dataSource = new HikariDataSource(config);

            // Initialize database schema
            initializeSchema();
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Unable to find database.properties");
            }
            props.load(input);
        }
        return props;
    }

    private static void initializeSchema() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String schemaSql = new String(DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("schema.sql").readAllBytes());
            String[] statements = schemaSql.split(";");
            for (String sql : statements) {
                if (!sql.trim().isEmpty()) {
                    logger.debug("Executing SQL: {}", sql.trim());
                    stmt.execute(sql);
                }
            }
            logger.info("Database schema initialized successfully");
        } catch (IOException e) {
            logger.error("Failed to read schema.sql", e);
            throw new SQLException("Failed to initialize schema: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            logger.debug("Database connection acquired");
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to acquire database connection", e);
            throw e;
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}