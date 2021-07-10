package io.th0rgal.guardian.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.th0rgal.guardian.exceptions.ExceptionHandler;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {
    protected final JavaPlugin plugin;
    protected final String table;
    protected Connection connection;

    public Database(JavaPlugin plugin, String table) {
        this.plugin = plugin;
        this.table = table;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement prepareStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?");
            ResultSet rs = prepareStatement.executeQuery();
            close(prepareStatement, rs);
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger(), "Unable to retreive connection");
        }
    }

    public void close(PreparedStatement preparedStatement, AutoCloseable closeable) {
        try {
            if (preparedStatement != null)
                preparedStatement.close();
            if (closeable != null)
                closeable.close();
        } catch (Exception exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        }
    }
}