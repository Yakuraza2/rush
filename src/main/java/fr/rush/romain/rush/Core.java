package fr.rush.romain.rush;

import fr.rush.romain.rush.commands.CommandsRush;
import fr.rush.romain.rush.commands.CommandsShop;
import fr.rush.romain.rush.commands.CommandsTeam;
import fr.rush.romain.rush.commands.CommandsZone;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Shop;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Core extends JavaPlugin {

    private static final HashMap<String, Rush> rushs = new HashMap<>();
    public static HashMap<Player, Rush> playersRush = new HashMap<>();

    public static List<Rush> waitingList = new ArrayList<>();
    private static File dataFolder;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        dataFolder = getDataFolder();

        FileManager.create("rush-list");
        FileManager.create("shops");

        PluginManager pm = getServer().getPluginManager();
        logger(1, "Lancement du Listener: Rush");
        pm.registerEvents(new RushListener(this), this);

        logger("Initialisation des commandes");
        getCommand("rush").setExecutor(new CommandsRush(this));
        getCommand("team").setExecutor(new CommandsTeam(this));
        getCommand("shop").setExecutor(new CommandsShop(this));
        getCommand("zone").setExecutor(new CommandsZone(this));

        //créer tous les objets <parties de rush> via une procedure pour pouvoir aussi les reload
        loadGames();
        loadShops();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static File getPluginDataFolder() { return dataFolder; }

    public static void logger(String log) { logger(0, log);}
    public static void logger(int gravity, String log) {
        switch(gravity) {
            case 0:
                //if debug==false return;
                System.out.print("[Rush] ");
                break;
            case 1:
                System.out.print("§e[RUSH] ");
                break;
            case 2:
                System.out.print("§6[RUSH | PROBLEM] ");
                break;
            case 3:
                System.out.print("§c[RUSH | ERROR] ");
                break;
            default:
                System.out.print("§4[RUSH | ERROR] ");
                break;
        }

        System.out.println(log);

    }

    public static HashMap<String, Rush> getRushsList() { return rushs; }

    public static void addRushToList(String name, Rush game) { rushs.put(name, game); }
    public static void addToWaiting(Rush rush){ waitingList.add(rush); }
    public static void removeOfWaiting(Rush rush){ waitingList.remove(rush); }

    public static List<Rush> getWaitingList() { return waitingList; }

    public static boolean loadGames(){
        logger(1, "Chargement des parties de rush...");
        File file = FileManager.get("rush-list");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String game : config.getStringList("rush-list")){
            loadGame(game);
        }
        return true;
    }
    public static void loadGame(String name){
        logger(1, "Chargement de " + name + "...");
        Rush rush = new Rush(name);
        rushs.put(name, rush);
        addToWaiting(rush); //By default, a game is at Waiting State.
    }

    public void loadShops() {
        Core.logger(1, "création des shops ");
        for(String shopID : FileManager.getConfig("shops").getStringList("shops.list")){
            new Shop(shopID);
        }
    }

    public static void createGame(Player p, String rush_id) {

        YamlConfiguration config = FileManager.getConfig(rush_id);

        FileManager.set(config, rush_id + ".world", p.getWorld().getName());
        FileManager.setLocation(config,rush_id + ".lobby", p.getLocation());
        FileManager.setLocation(config,rush_id + ".spectator-spawn", p.getLocation());
        FileManager.set(config, "timers.lobby-waiting", 20);
        FileManager.set(config, "timers.finish-waiting", 20);
        FileManager.set(config, "timers.bronze", 5);
        FileManager.set(config, "timers.iron", 15);
        FileManager.set(config, "timers.gold", 20);
        FileManager.set(config, "timers.diamond", 30);

        FileManager.save(config, FileManager.get(rush_id));
    }
}
