package net.royalmind.stats.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private JavaPlugin plugin;
    private HikariConfig config;
    private HikariDataSource ds;

    private static final String PROPERTIES_NAME = "database.properties";

    public DataSource(final JavaPlugin plugin) {
        this.plugin = plugin;
        config = new HikariConfig(this.getProperties());
        ds = new HikariDataSource(config);
    }

    public <T> T execute(final ConnectionCallback<T> callback) {
        try (final Connection conn = this.ds.getConnection()) {
            return callback.doInConnection(conn);
        } catch (final SQLException e) {
            throw new IllegalStateException("Error during execution.", e);
        }
    }

    public interface ConnectionCallback<T> {
        public T doInConnection(final Connection conn) throws SQLException;
    }

    public void close() {
        if (this.ds == null || !(this.ds.isClosed())) return;
        this.ds.close();
    }

    private String getProperties() {
        final File file = new File(this.plugin.getDataFolder(), PROPERTIES_NAME);
        if (!(file.exists())) {
            file.getParentFile().mkdirs();
            this.plugin.saveResource(PROPERTIES_NAME, false);
        }
        return file.getPath();
    }
}
