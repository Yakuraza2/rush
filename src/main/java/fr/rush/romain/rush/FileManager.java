package fr.rush.romain.rush;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static void create(final String fileName){
        if(!Main.getPluginDataFolder().exists()){
            Main.getPluginDataFolder().mkdir();
            System.out.println("[FILES] Le dossier " + Main.getPluginDataFolder().getName() + " n'existe pas.");
            System.out.println("[FILES] Création du dossier " + Main.getPluginDataFolder().getName());
        }else{Main.logger("Le dossier 'rush' existe");}

        File file = new File(Main.getPluginDataFolder(), fileName + ".yml");

        if(!file.exists()){
            System.out.println("[FILES] Le fichier " + fileName + ".yml n'existe pas.");
            try {
                file.createNewFile();
                System.out.println("[FILES] Création du fichier " + fileName + ".yml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{Main.logger("Le fichier 'rush' existe");}
    }

    public static File get(String fileName){return new File(Main.getPluginDataFolder(), fileName + ".yml");}

    public static void set(String fileName, String path, int value){

    }
    public static void set(String fileName, String path, String value){

    }

    public static YamlConfiguration getConfig(String fileName){
        File gameFile = FileManager.get(fileName);
        return YamlConfiguration.loadConfiguration(gameFile);
    }

}
