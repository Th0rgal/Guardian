package io.th0rgal.guardian.storage;

import io.th0rgal.guardian.exceptions.ExceptionHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public abstract class SQLite extends Database {
    private final String name;

    public SQLite(JavaPlugin plugin, String name) {
        super(plugin, name);
        this.name = name;
    }

    public Connection getSQLConnection() {
        File storageFolder = new File(plugin.getDataFolder(), "storage");
        storageFolder.mkdir();
        File dataFolder = new File(storageFolder, name + ".db");
        if (!dataFolder.exists())
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + name + ".db");
            }

        try {
            if (connection != null && !connection.isClosed())
                return connection;
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + dataFolder);
            return dataSource.getConnection();
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        }
        return null;
    }

    public void load(String tokensTable) {
        connection = getSQLConnection();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(tokensTable);
            statement.close();
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        }
        initialize();
    }

}