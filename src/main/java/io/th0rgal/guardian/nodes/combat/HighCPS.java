package io.th0rgal.guardian.nodes.combat;

import io.th0rgal.guardian.GuardianPlayer;
import io.th0rgal.guardian.PunishersManager;
import io.th0rgal.guardian.config.NodeConfig;
import io.th0rgal.guardian.PlayersManager;
import io.th0rgal.guardian.nodes.Node;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tomlj.TomlTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

final class CPSQueue {
    private final long[] queue;
    private int index;

    public CPSQueue(int size) {
        queue = new long[size];
        Arrays.fill(queue, 0);
        index = -1;
    }

    public double getCPS() {
        double sum = 0;
        for (int i = 1; i < queue.length - 1; i++) {
            long a = queue[(index + i) % queue.length];
            long b = queue[(index + i + 1) % queue.length];
            if (a == 0)
                return 0;
            sum += b - a;
        }
        return (1000 * (queue.length - 1)) / sum;
    }

    public void update() {
        index = (index + 1) % queue.length;
        queue[index] = System.currentTimeMillis();
    }
}

record PunisherTrigger(String name, boolean concurrent, double minCps, double addition, double multiply) {
}

public class HighCPS extends Node implements Listener {

    private final List<PunisherTrigger> triggers;

    public HighCPS(JavaPlugin plugin, PlayersManager playersManager, PunishersManager punishersManager, String name, NodeConfig configuration) {
        super(plugin, playersManager, punishersManager, name, configuration);
        triggers = new ArrayList<>();
        if (configuration.isArray("punishers"))
            for (Object punisherObject : configuration.getArray("punishers").toList()) {
                TomlTable punisherTable = (TomlTable) punisherObject;
                triggers.add(new PunisherTrigger(punisherTable.getString("punisher"),
                        punisherTable.getBoolean("concurrent"),
                        punisherTable.getDouble("min_cps"),
                        punisherTable.isDouble("add") ? punisherTable.getDouble("add") : 0,
                        punisherTable.isDouble("multiply") ? punisherTable.getDouble("multiply") : 1));
            }
        triggers.sort(Comparator.comparing(PunisherTrigger::minCps).reversed());
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR)
            return;
        GuardianPlayer player = playersManager.getPlayer(event.getPlayer());
        CPSQueue cpsQueue = (CPSQueue) player.getData(this.getClass());
        if (cpsQueue == null) {
            cpsQueue = new CPSQueue((int) configuration.getLong("historic"));
            player.setData(this.getClass(), cpsQueue);
        }
        cpsQueue.update();
        double cps = cpsQueue.getCPS();
        for (int i = 0; i < triggers.size(); i++) {
            PunisherTrigger trigger = triggers.get(i);
            if ((i != 0 && !trigger.concurrent()) || cps < trigger.minCps())
                continue;
            punishersManager.add(player, trigger.name(), trigger.addition());
        }
    }
}
