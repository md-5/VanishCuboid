package com.md_5.vanishcuboid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.PluginManager;

public class VanishCuboidBlockListener extends BlockListener {

    private final VanishCuboid plugin;

    public VanishCuboidBlockListener(final VanishCuboid plugin) {
        this.plugin = plugin;
    }

    public void registerEvents(final PluginManager pm) {
        pm.registerEvent(Type.REDSTONE_CHANGE, this, Priority.Normal, plugin);
        pm.registerEvent(Type.BLOCK_BREAK, this, Priority.Normal, plugin);
    }

    @Override
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        for (BlockStorage bs : VanishCuboid.finishedCuboids.values()) {
            Block cb = new Location(Bukkit.getWorld(bs.world), bs.cx, bs.cy, bs.cz).getBlock();
            Block cb2 = new Location(Bukkit.getWorld(bs.world), bs.c2x, bs.c2y, bs.c2z).getBlock();
            if (event.getBlock().equals(cb) || event.getBlock().equals(cb2)) {
                if (bs.vanished) {
                    Editor.restore(bs);
                    bs.vanished = false;
                } else {
                    Editor.vanish(bs);
                    bs.vanished = true;
                }
                VanishCuboid.saveToDisk(bs);
            }
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        List<String> remove = new ArrayList<String>();
        for (BlockStorage bs : VanishCuboid.finishedCuboids.values()) {
            Location cb = new Location(Bukkit.getServer().getWorld(bs.world), bs.cx, bs.cy, bs.cz);
            Location cb2 = new Location(Bukkit.getServer().getWorld(bs.world), bs.c2x, bs.c2y, bs.c2z);
            if (event.getBlock().getLocation().equals(cb)  || event.getBlock().getLocation().equals(cb2)) {
                if (bs.vanished) {
                    Editor.restore(bs);
                }
                remove.add(bs.name);
            }
        }
        for (String bs : remove) {
            File data = new File(plugin.getDataFolder().getPath() + File.separator + "cuboids" + File.separator + bs + ".bin");
            data.delete();
            VanishCuboid.finishedCuboids.remove(bs);
        }
    }
}
