package io.th0rgal.guardian.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import io.th0rgal.guardian.exceptions.ExceptionHandler;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {
    protected final JavaPlugin plugin;
    private final String table;
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

    public Double getScore(UUID uuid, String punisher) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet resultSet;
        try {
            connection = getSQLConnection();
            prepareStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + uuid + "';");
            resultSet = prepareStatement.executeQuery();
            while (resultSet.next())
                if (resultSet.getString("uuid").equalsIgnoreCase(uuid.toString()))
                    return resultSet.getDouble(punisher);
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        } finally {
            close(prepareStatement, connection);
        }
        return 0D;
    }

    public void setScore(UUID uuid, String punisher, Double score) {
        try (Connection connection = getSQLConnection(); PreparedStatement prepareStatement =
                connection.prepareStatement("REPLACE INTO " + table + " (uuid," + punisher + ") VALUES(?,?)")) {
            prepareStatement.setString(1, uuid.toString());
            prepareStatement.setDouble(2, score);
            prepareStatement.executeUpdate();
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 1)
                try (Connection connection = getSQLConnection(); PreparedStatement prepareStatement =
                        connection.prepareStatement("ALTER TABLE " + table + " ADD `" + punisher + "` FLOAT(24) NOT NULL default 0")) {
                    prepareStatement.executeUpdate();
                    setScore(uuid, punisher, score);
                    return;
                } catch (SQLException throwables) {
                    exception = throwables;
                }
            new ExceptionHandler(exception).fire(plugin.getLogger());
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