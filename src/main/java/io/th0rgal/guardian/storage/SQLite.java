package io.th0rgal.guardian.storage;

import org.bukkit.plugin.java.JavaPlugin;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Set;
import java.util.logging.Level;

public class SQLite extends Database {
    private final String name;
    private final Set<String> punishers;

    public SQLite(JavaPlugin plugin, Set<String> punishers, String name) {
        super(plugin, name);
        this.punishers = punishers;
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
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", exception);
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            StringBuilder tokensTable = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (`uuid` varchar(32) NOT NULL,");
            for (String punisher : punishers)
                tokensTable.append("`").append(punisher).append("` int(11) NOT NULL default 0,");
            tokensTable.append("PRIMARY KEY (`uuid`));");
            s.executeUpdate(tokensTable.toString());
            s.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        initialize();
    }
}