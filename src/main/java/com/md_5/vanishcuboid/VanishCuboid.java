package com.md_5.vanishcuboid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class VanishCuboid extends JavaPlugin {

    static final Logger logger = Bukkit.getServer().getLogger();
    public static HashMap<String, BlockStorage> startedCuboids = new HashMap<String, BlockStorage>();
    public static HashMap<String, BlockStorage> finishedCuboids = new HashMap<String, BlockStorage>();

    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();

        Configuration conf = new Configuration(new File(this.getDataFolder(), "config.yml"));
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            conf.save();
        }
        conf.load();

        VanishCuboidPlayerListener playerListener = new VanishCuboidPlayerListener(this);
        playerListener.registerEvents(pm);

        VanishCuboidBlockListener blockListener = new VanishCuboidBlockListener(this);
        blockListener.registerEvents(pm);

        loadFromDisk();

        logger.info(String.format("VanishCuboid v%1$s by md_5 enabled", this.getDescription().getVersion()));
    }

    public void onDisable() {
        logger.info(String.format("VanishCuboid v%1$s by md_5 disabled", this.getDescription().getVersion()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            return onPlayerCommand((Player) sender, command, label, args);
        } else {
            return onConsoleCommand(sender, command, label, args);
        }
    }

    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if ((args.length != 2 && !args[0].equalsIgnoreCase("list")) || args.length == 0) {
            player.sendMessage("VanishCuboid: Usage: /cv <command> <name>");
            return true;
        }

        if (args[0].equalsIgnoreCase("define")) {
            if (finishedCuboids.containsKey(args[1])) {
                player.sendMessage("VanishCuboid: Error that cuboid is already taken!");
                return true;
            }
            BlockStorage cuboid = startedCuboids.get(player.getName());
            if (cuboid.cx == 0) {
                player.sendMessage("VanishCuboid: Error that cuboid is not complete");
                return true;
            }
            if (finishedCuboids.containsKey(args[1])) {
                player.sendMessage("VanishCuboid: Error that cuboid name is already taken");
                return true;
            }
            if (!player.hasPermission("vanishcuboid.create.admin") && args[1].charAt(0) == '#'){
                player.sendMessage("VanishCuboid: Error you cannot create that type of cuboid");
                return true;
            }
            finishedCuboids.put(args[1], startedCuboids.get(player.getName()));
            finishedCuboids.get(args[1]).owner = player.getName();
            finishedCuboids.get(args[1]).name = args[1];
            save(finishedCuboids.get(args[1]), this.getDataFolder().getPath() + File.separator + "cuboids" + File.separator + args[1] + ".bin");
            startedCuboids.remove(player.getName());
            player.sendMessage("VanishCuboid: Cuboid created");
            return true;
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (!finishedCuboids.containsKey(args[1])) {
                player.sendMessage("VanishCuboid: Error that cuboid name does not exist");
                return true;
            }
            if (player.hasPermission("vanishcuboid.remove.others")
                    || (player.hasPermission("vanishcuboid.delete") && finishedCuboids.get(args[1]).owner.equals(player.getName()))) {
                Editor.restore(finishedCuboids.get(args[1]));
                finishedCuboids.remove(args[1]);
                player.sendMessage("VanishCuboid: Cuboid removed");
                return true;
            }
            player.sendMessage("VanishCuboid: You cannnot remove that cuboid");
            return true;
        } else if (args[0].equalsIgnoreCase("list")){
            String cuboids = "";
            for (BlockStorage bs : finishedCuboids.values()){
                cuboids += bs.name;
                cuboids += " (" + bs.owner + ") ";
            }
            player.sendMessage(cuboids);
            return true;
        }
        player.sendMessage("VanishCuboid: Error that is not a valid command");
        return true;
    }

    public boolean onConsoleCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(String.format("VanishCuboid v%1$s by md_5", this.getDescription().getVersion()));
        sender.sendMessage("VanishCuboid: This command can only be used by in-game players!");
        return true;
    }

    public void loadFromDisk() {
        File cuboidFolder = new File(this.getDataFolder().getPath() + File.separator + "cuboids");
        if (!cuboidFolder.exists()) {
            cuboidFolder.mkdirs();
        }
        File cuboidFiles[] = cuboidFolder.listFiles();
        for (File f : cuboidFiles) {
            BlockStorage bs = null;
            bs = load(f.getPath());
            finishedCuboids.put(bs.name, bs);
        }
    }
    public static void saveToDisk(BlockStorage bs){
        File cuboidFolder = new File("plugins" + File.separator + "VanishCuboid" + File.separator + "cuboids" + File.separator + bs.name + ".bin");
        save(bs, cuboidFolder.getPath());
    }

    public static void save(Object obj, String path) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            Logger.getLogger(VanishCuboid.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(VanishCuboid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static BlockStorage load(String path) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            Object result = ois.readObject();
            ois.close();
            return (BlockStorage) result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new BlockStorage();
    }
}