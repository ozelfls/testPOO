package dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionFactory {

    private static final String DB_FILE_NAME = "tamagotchi.db";
    private static final String SCHEMA_FILE_NAME = "schema.sql";
    private static final Path DB_DIR = resolveDatabaseDirectory();
    private static final Path DB_PATH = DB_DIR.resolve(DB_FILE_NAME).toAbsolutePath().normalize();
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private static boolean initialized;

    public static Connection getConnection() throws SQLException {
        loadDriver();
        ensureDatabase();

        Connection conn = DriverManager.getConnection(URL);
        configureConnection(conn);
        return conn;
    }

    private static void loadDriver() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite nao encontrado. "
                    + "Adicione sqlite-jdbc.jar em lib/ ao classpath.", e);
        }
    }

    private static synchronized void ensureDatabase() throws SQLException {
        if (initialized) {
            return;
        }

        try {
            Files.createDirectories(DB_DIR);
            boolean emptyDatabase = !Files.exists(DB_PATH) || Files.size(DB_PATH) == 0;

            try (Connection conn = DriverManager.getConnection(URL)) {
                configureConnection(conn);
                if (emptyDatabase || isSchemaIncomplete(conn)) {
                    runSchema(conn);
                }
                migratePetImageStorage(conn);
            }

            initialized = true;
        } catch (IOException e) {
            throw new SQLException("Erro ao preparar diretorio do banco SQLite: " + DB_DIR, e);
        } catch (Exception e) {
            throw new SQLException("Erro ao inicializar banco SQLite em " + DB_PATH
                    + ": " + e.getMessage(), e);
        }
    }

    private static void configureConnection(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA busy_timeout = 5000");
        }
    }

    private static boolean isSchemaIncomplete(Connection conn) throws SQLException {
        return !hasTable(conn, "pet")
                || !hasTable(conn, "creature_type")
                || !hasTable(conn, "evolution")
                || !hasTable(conn, "app_setting");
    }

    private static boolean hasTable(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean hasColumn(Connection conn, String tableName, String columnName)
            throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void runSchema(Connection conn) throws SQLException, IOException {
        String schema = readSchema();
        try (Statement st = conn.createStatement()) {
            for (String rawStmt : schema.split(";")) {
                String sql = rawStmt.trim();
                if (!sql.isEmpty()) {
                    st.executeUpdate(sql);
                }
            }
        }
    }

    private static String readSchema() throws IOException {
        Path schemaPath = resolveSchemaPath();
        if (schemaPath != null) {
            return Files.readString(schemaPath);
        }

        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/database/schema.sql")) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        try (InputStream in = ConnectionFactory.class.getResourceAsStream("/schema.sql")) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        throw new IOException("Arquivo schema.sql nao encontrado. Diretorio do banco: " + DB_DIR);
    }

    private static void migratePetImageStorage(Connection conn) throws SQLException {
        if (!hasTable(conn, "pet")) {
            return;
        }

        boolean hasImageData = hasColumn(conn, "pet", "image_data");
        boolean hasImagePath = hasColumn(conn, "pet", "image_path");

        if (!hasImageData) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("ALTER TABLE pet ADD COLUMN image_data BLOB");
            }
        }

        if (hasImagePath) {
            migrateImagePathToBlob(conn);
        }
    }

    private static void migrateImagePathToBlob(Connection conn) throws SQLException {
        String select = "SELECT id, image_path FROM pet "
                + "WHERE image_path IS NOT NULL AND TRIM(image_path) <> '' "
                + "AND image_data IS NULL";
        String update = "UPDATE pet SET image_data = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(select);
             ResultSet rs = ps.executeQuery();
             PreparedStatement updatePs = conn.prepareStatement(update)) {
            while (rs.next()) {
                Path imagePath = Paths.get(rs.getString("image_path"));
                if (!imagePath.isAbsolute()) {
                    imagePath = DB_DIR.getParent().resolve(imagePath).normalize();
                }
                if (Files.isRegularFile(imagePath)) {
                    try {
                        updatePs.setBytes(1, Files.readAllBytes(imagePath));
                        updatePs.setInt(2, rs.getInt("id"));
                        updatePs.executeUpdate();
                    } catch (IOException ignored) {
                        // Mantem image_path se o arquivo nao puder ser lido.
                    }
                }
            }
        }
    }

    private static Path resolveDatabaseDirectory() {
        String propertyOverride = System.getProperty("tamagotchi.database.dir");
        if (propertyOverride != null && !propertyOverride.isBlank()) {
            return Paths.get(propertyOverride).toAbsolutePath().normalize();
        }

        String envOverride = System.getenv("TAMAGOTCHI_DATABASE_DIR");
        if (envOverride != null && !envOverride.isBlank()) {
            return Paths.get(envOverride).toAbsolutePath().normalize();
        }

        for (Path candidate : databaseCandidates()) {
            if (Files.exists(candidate.resolve(SCHEMA_FILE_NAME))
                    || Files.exists(candidate.resolve(DB_FILE_NAME))) {
                return candidate.toAbsolutePath().normalize();
            }
        }

        return Paths.get("database").toAbsolutePath().normalize();
    }

    private static Path resolveSchemaPath() {
        for (Path candidate : databaseCandidates()) {
            Path schema = candidate.resolve(SCHEMA_FILE_NAME);
            if (Files.exists(schema)) {
                return schema.toAbsolutePath().normalize();
            }
        }

        Path schema = DB_DIR.resolve(SCHEMA_FILE_NAME);
        if (Files.exists(schema)) {
            return schema.toAbsolutePath().normalize();
        }

        return null;
    }

    private static List<Path> databaseCandidates() {
        List<Path> candidates = new ArrayList<>();
        candidates.add(Paths.get("database"));
        candidates.add(Paths.get("Tamagotchi", "database"));

        Path codePath = getCodePath();
        if (codePath != null) {
            Path base = Files.isRegularFile(codePath) ? codePath.getParent() : codePath;
            if (base != null) {
                candidates.add(base.resolve("database"));
                Path parent = base.getParent();
                if (parent != null) {
                    candidates.add(parent.resolve("database"));
                    candidates.add(parent.resolve("Tamagotchi").resolve("database"));
                }
            }
        }

        return candidates;
    }

    private static Path getCodePath() {
        try {
            return Paths.get(ConnectionFactory.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
        } catch (URISyntaxException | NullPointerException e) {
            return null;
        }
    }
}
