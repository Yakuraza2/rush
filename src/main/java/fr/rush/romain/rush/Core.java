package fr.rush.romain.rush;

import fr.rush.romain.rush.commands.Commands;
import fr.rush.romain.rush.commands.CommandsTeam;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.ShopManager;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.ShopItem;
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
    public static HashMap<Player, Rush> playersRush = new HashMap<Player, Rush>();

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
        getCommand("rush").setExecutor(new Commands(this));
        getCommand("team").setExecutor(new CommandsTeam(this));

        //créer tous les objets <parties de rush> via une procedure pour pouvoir aussi les reload
        loadGames();
        ShopManager.loadShops();

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

    public static void loadGames(){
        logger(1, "Chargement des parties de rush...");
        File file = FileManager.get("rush-list");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String game : config.getStringList("list")){
            loadGame(game);
        }
    }
    public static void loadGame(String name){
        logger(1, "Chargement de " + name + "...");
        Rush rush = new Rush(name);
        rushs.put(name, rush);
        addRushToList(name, rush);
        addToWaiting(rush); //By default, a game is at Waiting State.
    }

    public static void createGame(Player p, String rush_id) {

        YamlConfiguration config = FileManager.getConfig(rush_id);

        FileManager.set(config, rush_id + ".world", p.getWorld().getName());
        FileManager.setLocation(config,rush_id + ".lobby", p.getLocation());
        FileManager.setLocation(config,rush_id + ".spectator-spawn", p.getLocation());

        FileManager.save(config, FileManager.get(rush_id));
    }
}
