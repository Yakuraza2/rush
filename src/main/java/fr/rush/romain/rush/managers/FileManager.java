package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static void create(final String fileName){
        if(!Core.getPluginDataFolder().exists()){
            Core.getPluginDataFolder().mkdir();
            System.out.println("[FILES] Le dossier " + Core.getPluginDataFolder().getName() + " n'existe pas.");
            System.out.println("[FILES] Création du dossier " + Core.getPluginDataFolder().getName());
        }else{
            Core.logger("Le dossier 'rush' existe");}

        File file = new File(Core.getPluginDataFolder(), fileName + ".yml");

        if(!file.exists()){
            System.out.println("[FILES] Le fichier " + fileName + ".yml n'existe pas.");
            try {
                file.createNewFile();
                System.out.println("[FILES] Création du fichier " + fileName + ".yml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            Core.logger("Le fichier 'rush' existe");}
    }

    public static File get(String fileName){return new File(Core.getPluginDataFolder(), fileName + ".yml");}

    public static void set(String fileName, String path, int value){

    }
    public static void set(String fileName, String path, String value){

    }

    public static YamlConfiguration getConfig(String fileName){
        File gameFile = FileManager.get(fileName);
        return YamlConfiguration.loadConfiguration(gameFile);
    }

    public static YamlConfiguration getConfig(){
        return getConfig("config");
    }

    public static String prefix(){
        return getConfig("config").getString("messages.prefix") + " ";
    }

    public static String getConfigMessage(String path, Player player, boolean prefix, Rush rush, int timer){
        String message = " ";

        if(prefix){
            message = prefix() + getConfig("config").getString("messages." + path);
        }else{message = getConfig("config").getString("messages." + path);}

        if(player != null) message = message.replaceAll("<player>", player.getName());

        if(rush != null) {
            message = message.replaceAll("<time>", timer + "");
            message = message.replaceAll("<slots>", rush.getSlots() + "");
            message = message.replaceAll("<onlines>", rush.getPlayers().size() + "");
        }
        return message;
    }

    public static String getConfigMessage(String path, Player player, Rush rush){ return getConfigMessage(path, player, true, rush, 0); }
    public static String getConfigMessage(String path, Player player, Rush rush, boolean prefix){ return getConfigMessage(path, player, prefix, rush, 0); }
    public static String getConfigMessage(String path, Rush rush){ return getConfigMessage(path, null, true, rush, 0); }
    public static String getConfigMessage(String path, Rush rush, int timer){ return getConfigMessage(path, null, true, rush, timer); }

}
