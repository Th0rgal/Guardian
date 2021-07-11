package io.th0rgal.guardian.storage;

import io.th0rgal.guardian.exceptions.ExceptionHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class JournalDatabase extends SQLite {
    private final String name;
    private final String[] journals;

    public JournalDatabase(JavaPlugin plugin, String name, String... journals) {
        super(plugin, name);
        this.name = name;
        this.journals = journals;
    }

    public void load() {
        StringBuilder tokensTable = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (`uuid` varchar(32) NOT NULL");
        for (String journal : journals)
            tokensTable.append(", `").append(journal).append("` BOOLEAN DEFAULT(FALSE)");
        tokensTable.append(")");
        load(tokensTable.toString());
    }

    public boolean isSubscribed(UUID uuid, String journal) {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet resultSet;
        try {
            connection = getSQLConnection();
            prepareStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + uuid + "';");
            resultSet = prepareStatement.executeQuery();
            while (resultSet.next())
                if (resultSet.getString("uuid").equalsIgnoreCase(uuid.toString()))
                    return resultSet.getBoolean(journal);
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        } finally {
            close(prepareStatement, connection);
        }
        return false;
    }

    public void subscribes(UUID uuid, Map<String, Boolean> journals) {
        try (Connection connection = getSQLConnection(); PreparedStatement prepareStatement =
                connection.prepareStatement("REPLACE INTO " + table
                        + " (uuid," + String.join(", ", journals.keySet())
                        + ") VALUES(?" + ",?".repeat(journals.size()) + ")")) {
            prepareStatement.setString(1, uuid.toString());
            int i = 2;
            for (Boolean enabled : journals.values())
                prepareStatement.setBoolean(i++, enabled);
            prepareStatement.executeUpdate();
        } catch (SQLException exception) {
            new ExceptionHandler(exception).fire(plugin.getLogger());
        }
    }

}