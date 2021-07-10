package io.th0rgal.guardian.storage;

import io.th0rgal.guardian.exceptions.ExceptionHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PunishersDatabase extends SQLite {
    private final String name;
    private final Set<String> punishers;

    public PunishersDatabase(JavaPlugin plugin, Set<String> punishers, String name) {
        super(plugin, name);
        this.punishers = punishers;
        this.name = name;
    }

    public void load() {
        StringBuilder tokensTable = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (`uuid` varchar(32) NOT NULL,");
        for (String punisher : punishers)
            tokensTable.append("`").append(punisher).append("` FLOAT(24) NOT NULL default 0,");
        tokensTable.append("PRIMARY KEY (`uuid`));");
        load(tokensTable.toString());
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

    public void setScores(UUID uuid, Map<String, Double> punishersScore) {
        try (Connection connection = getSQLConnection(); PreparedStatement prepareStatement =
                connection.prepareStatement("REPLACE INTO " + table
                        + " (uuid," + String.join(", ", punishersScore.keySet())
                        + ") VALUES(?" + ",?".repeat(punishersScore.size()) + ")")) {
            prepareStatement.setString(1, uuid.toString());
            int i = 2;
            for (Double score : punishersScore.values())
                prepareStatement.setDouble(i++, score);
            prepareStatement.executeUpdate();
            System.out.println("gamma " + table);
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        }
    }

}