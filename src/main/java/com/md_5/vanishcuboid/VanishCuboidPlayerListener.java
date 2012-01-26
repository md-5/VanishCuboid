package com.md_5.vanishcuboid;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

public class VanishCuboidPlayerListener extends PlayerListener {

    private final VanishCuboid plugin;

    public VanishCuboidPlayerListener(final VanishCuboid plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(final PluginManager pm) {
        pm.registerEvent(Type.PLAYER_QUIT, this, Priority.Normal, plugin);
        pm.registerEvent(Type.PLAYER_INTERACT, this, Priority.Normal, plugin);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        VanishCuboid.startedCuboids.remove(event.getPlayer().getName());
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!(event.getPlayer().getItemInHand().getTypeId() == Config.tool) || !(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        Location click = event.getClickedBlock().getLocation();
        if (VanishCuboid.startedCuboids.containsKey(event.getPlayer().getName())) {
            BlockStorage bs = VanishCuboid.startedCuboids.get(event.getPlayer().getName());
            if (bs.l2x != 0 && bs.cx == 0) {
                Material blockType = event.getClickedBlock().getType();
                if (!(blockType.equals(Material.STONE_BUTTON) || blockType.equals(Material.LEVER)
                        || blockType.equals(Material.WOOD_PLATE) || blockType.equals(Material.STONE_PLATE))) {
                    event.getPlayer().sendMessage("That is not a valid block!");
                    return;
                }
                event.setCancelled(true);
                bs.cx = click.getBlockX();
                bs.cy = click.getBlockY();
                bs.cz = click.getBlockZ();
                event.getPlayer().sendMessage("Got All Blocks, use command to set or click an optional secondary control");
                return;
            } else if (bs.l2x != 0 && bs.cx != 0 && bs.c2x == 0) {
                Material blockType = event.getClickedBlock().getType();
                if (!(blockType.equals(Material.STONE_BUTTON) || blockType.equals(Material.LEVER)
                        || blockType.equals(Material.WOOD_PLATE) || blockType.equals(Material.STONE_PLATE))) {
                    event.getPlayer().sendMessage("That is not a valid block!");
                    return;
                }
                event.setCancelled(true);
                bs.c2x = click.getBlockX();
                bs.c2y = click.getBlockY();
                bs.c2z = click.getBlockZ();
                event.getPlayer().sendMessage("Control block 2 set");
                return;
            } else if (bs.c2x != 0) {
                VanishCuboid.startedCuboids.remove(event.getPlayer().getName());
                event.getPlayer().sendMessage("All locations reset");
                return;
            }
            bs.l2x = click.getBlockX();
            bs.l2y = click.getBlockY();
            bs.l2z = click.getBlockZ();
            int start_x = Math.min(bs.l1x, bs.l2x);
            int start_y = Math.min(bs.l1y, bs.l2y);
            int start_z = Math.min(bs.l1z, bs.l2z);
            int end_x = Math.max(bs.l1x, bs.l2x);
            int end_y = Math.max(bs.l1y, bs.l2y);
            int end_z = Math.max(bs.l1z, bs.l2z);

            int width = end_x - start_x;
            int height = end_y - start_y;
            int length = end_z - start_z;

            width += 1;
            height += 1;
            length += 1;

            if (width > Config.maxWidth) {
                bs.l2x = 0;
                bs.l2y = 0;
                bs.l2z = 0;
                event.getPlayer().sendMessage("Error! That Cuboids width is too big!");
                VanishCuboid.startedCuboids.remove(event.getPlayer().getName());
                return;
            }
            if (height > Config.maxHeight) {
                bs.l2x = 0;
                bs.l2y = 0;
                bs.l2z = 0;
                event.getPlayer().sendMessage("Error! That Cuboids height is too big!");
                VanishCuboid.startedCuboids.remove(event.getPlayer().getName());
                return;
            }
            if (length > Config.maxLength) {
                bs.l2x = 0;
                bs.l2y = 0;
                bs.l2z = 0;
                event.getPlayer().sendMessage("Error! That Cuboids length is too big!");
                VanishCuboid.startedCuboids.remove(event.getPlayer().getName());
                return;
            }
            event.getPlayer().sendMessage("Got Block 2, now Rightclick the Control Block");
            return;
        } else {
            BlockStorage newCubeoid = new BlockStorage();
            newCubeoid.l1x = click.getBlockX();
            newCubeoid.l1y = click.getBlockY();
            newCubeoid.l1z = click.getBlockZ();
            newCubeoid.world = click.getWorld().getName();
            VanishCuboid.startedCuboids.put(event.getPlayer().getName(), newCubeoid);
            event.getPlayer().sendMessage("Got Block 1, now Rightclick Block 2");
            return;
        }
    }
}
