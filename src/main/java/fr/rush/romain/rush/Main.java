package fr.rush.romain.rush;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public final class Main extends JavaPlugin {

    private static final HashMap<String, Rush> rushs = new HashMap<>();
    private static File dataFolder;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        FileManager.create("rush-list");

        dataFolder = getDataFolder();

        PluginManager pm = getServer().getPluginManager();
        logger(1, "Lancement du Listener: Rush");
        pm.registerEvents(new RushListener(this), this);

        logger("Initialisation des commandes");
        getCommand("rush").setExecutor(new Commands(this));
        getCommand("team").setExecutor(new CommandsTeam(this));

        //créer tous les objets <parties de rush> via une procedure pour pouvoir aussi les reload
        createGames();

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

    public static void createGames(){
        logger(1, "Création de toutes les parties de rush en config...");
        File file = FileManager.get("rush-list");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String game : config.getStringList("list")){
            createGame(game);
        }
    }

    public static void createGame(String name){
        logger(1, "Création du fichier " + name + "...");
        File gameFile = FileManager.get(name);
        YamlConfiguration gameConfig = YamlConfiguration.loadConfiguration(gameFile);

        rushs.put(name, new Rush(name));
    }

    public static void load(){
        //pour tous les rushs en config : Main.addRushToList(args[2], new Rush());
    }

/**
 * Pour les ID des parties de rush: créer une hashmap key = String et Object = Rush
 */

}
