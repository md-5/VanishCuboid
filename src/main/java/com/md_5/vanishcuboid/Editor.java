package com.md_5.vanishcuboid;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class Editor {

    public static void vanish(BlockStorage bs) {
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

        int index = 0;
        byte[] blockID = new byte[width * height * length];
        byte[] blockData = new byte[width * height * length];
        byte replaceID = (byte) Bukkit.getServer().getWorld(bs.world).getBlockAt(bs.l1x, bs.l1y, bs.l1z).getTypeId();
        byte replaceData = (byte) Bukkit.getServer().getWorld(bs.world).getBlockAt(bs.l1x, bs.l1y, bs.l1z).getData();

        for (int x = start_x; x <= end_x; x++) {
            for (int y = start_y; y <= end_y; y++) {
                for (int z = start_z; z <= end_z; z++) {
                    blockID[index] = (byte) Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).getTypeId();
                    blockData[index] = (byte) Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).getData();
                    if ((Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).getTypeId() == replaceID)
                            && Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).getData() == replaceData) {
                        Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).setType(Material.AIR);
                    }
                    index++;
                }
            }
        }
        bs.replaceID = replaceID;
        bs.replaceData = replaceData;
        bs.blockID = blockID;
        bs.blockData = blockData;
    }

    public static void restore(BlockStorage bs) {
        int start_x = Math.min(bs.l1x, bs.l2x);
        int start_y = Math.min(bs.l1y, bs.l2y);
        int start_z = Math.min(bs.l1z, bs.l2z);
        int end_x = Math.max(bs.l1x, bs.l2x);
        int end_y = Math.max(bs.l1y, bs.l2y);
        int end_z = Math.max(bs.l1z, bs.l2z);

        int index = 0;
        byte[] blockID = bs.blockID;
        byte[] blockData = bs.blockData;
        byte replaceID = bs.replaceID;
        byte replaceData = bs.replaceData;

        for (int x = start_x; x <= end_x; x++) {
            for (int y = start_y; y <= end_y; y++) {
                for (int z = start_z; z <= end_z; z++) {
                    Byte setID = blockID[index];
                    Byte setData = blockData[index];
                    if (((setID == replaceID) && (setData == replaceData))
                            || (bs.name.charAt(0) == '#' && (setID == 3) || (setID == 80))) {
                        Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).setTypeId(blockID[index]);
                        Bukkit.getServer().getWorld(bs.world).getBlockAt(x, y, z).setData(blockData[index]);
                    }
                    index++;
                }
            }
        }
    }
}
