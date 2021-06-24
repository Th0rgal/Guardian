package io.th0rgal.guardian.commands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record InspectData(Player target, Location location, ItemStack[] inventory, GameMode gameMode, boolean invisible) {

}