package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class ShopManager {

    private static final HashMap<String, Inventory> shopList = new HashMap<>();

    public static void loadShops() {
        Core.logger(1, "création des shops ");
        for(String shopID : FileManager.getConfig("shops").getStringList("shops.list")){
            createShop(shopID);
        }
    }

    public static void createShop(String shopID){
        Core.logger(1, "création du shop " + shopID);
        YamlConfiguration config = FileManager.getConfig("shops");
        try{
            int    size        = config.getInt("shops." + shopID + ".size");
            String displayName = config.getString("shops." + shopID + ".display-name");

            Inventory inventory = Bukkit.createInventory(null, size, displayName);

            for(String itemID : config.getStringList("shops." + shopID + ".items-list")){
                ShopItem item = new ShopItem(shopID, itemID);
                inventory.setItem(item.getSlot(), item.getItemStack());
            }

            Core.logger(1, shopID + " créé !");
            shopList.put(shopID, inventory);

        } catch(IllegalArgumentException e) {
            e.fillInStackTrace();
            Core.logger(4, "Error during loading of " + shopID + " : shops.yml file seems unfinished");
            Core.logger(4, "HELP: Verify shops' sizes: it must be multiples of 9");
            Core.logger(4, "HELP: Verify price-items -> item id need to be the perfect one !");
        } catch (Exception e){
            e.fillInStackTrace();
            Core.logger(4, "Erreur de chargement de " + shopID);
        }

    }

    public static boolean exists(String id) { return shopList.containsKey(id); }

    public static HashMap<String, Inventory> getShopList(){ return shopList; }
    public static Inventory get(String id){ return shopList.get(id); }


}
