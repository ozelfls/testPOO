package dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {

    private static final String DB_DIR  = "Tamagotchi/database";
    private static final String DB_PATH = DB_DIR + "/tamagotchi.db";
    private static final String URL     = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite nao encontrado. "
                    + "Adicione sqlite-jdbc.jar em lib/ ao classpath.", e);
        }
        ensureDatabase();
        return DriverManager.getConnection(URL);
    }

    private static void ensureDatabase() throws SQLException {
        try {
            Path dir = Paths.get(DB_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path dbFile = Paths.get(DB_PATH);
            if (!Files.exists(dbFile)) {
                try (Connection conn = DriverManager.getConnection(URL);
                     Statement st = conn.createStatement()) {
                    String schema = Files.readString(Paths.get(DB_DIR, "schema.sql"));
                    for (String rawStmt : schema.split(";")) {
                        String sql = rawStmt.trim();
                        if (!sql.isEmpty()) {
                            st.executeUpdate(sql);
                        }
                    }
                }
            } else {
                // Migrate existing database: image_path TEXT -> image_data BLOB
                try (Connection conn = DriverManager.getConnection(URL)) {
                    migrateImageColumn(conn);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao inicializar banco SQLite: " + e.getMessage(), e);
        }
    }

    /** Migrates the pet table from image_path (TEXT) to image_data (BLOB) if needed. */
    private static void migrateImageColumn(Connection conn) throws SQLException {
        boolean hasImageData = false;
        boolean hasImagePath = false;
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "pet", "image_data")) {
            hasImageData = rs.next();
        }
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, "pet", "image_path")) {
            hasImagePath = rs.next();
        }
        try (Statement st = conn.createStatement()) {
            if (!hasImageData) {
                st.executeUpdate("ALTER TABLE pet ADD COLUMN image_data BLOB");
            }
            if (hasImagePath) {
                try {
                    st.executeUpdate("ALTER TABLE pet DROP COLUMN image_path");
                } catch (SQLException ignored) {
                    // SQLite < 3.35 does not support DROP COLUMN — leave it in place
                }
            }
        }
    }
}

