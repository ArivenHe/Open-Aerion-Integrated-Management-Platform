package cn.ariven.openaimpbackend.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DatabaseManager {
    private static final String DB_PATH = "classpath:little_navmap_navigraph.sqlite";

    public static Connection getConnection() throws SQLException {
        try {
            File file = ResourceUtils.getFile(DB_PATH);
            String dbUrl = "jdbc:sqlite:" + file.getAbsolutePath();
            return DriverManager.getConnection(dbUrl);
        } catch (Exception e) {
            log.error("Failed to connect to navigation database", e);
            throw new SQLException("Failed to connect to navigation database", e);
        }
    }
}
