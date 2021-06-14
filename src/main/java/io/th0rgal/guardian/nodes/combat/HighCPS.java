package io.th0rgal.guardian.nodes.combat;

import io.th0rgal.guardian.GuardianPlayer;
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

import java.util.Arrays;

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

public class HighCPS extends Node implements Listener {

    public HighCPS(JavaPlugin plugin, PlayersManager playersManager, String name, NodeConfig configuration) {
        super(plugin, playersManager, name, configuration);
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
        if (configuration.getBoolean("alert.enabled")) {
            double cps = cpsQueue.getCPS();
            if (cps > configuration.getDouble("alert.min_cps")) {
                Bukkit.broadcastMessage("CPS: " + cps);
            }
        }
    }
}
