package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class ShopManager {

    private static HashMap<String, Inventory> shopList = new HashMap<>();

    public static void loadShops() {
        Core.logger(1, "création des shops ");
        for(String shopID : FileManager.getConfig("shops").getStringList("shops")){
            createShop(shopID);
        }
    }

    public static void createShop(String shopID){
        Core.logger(1, "création du shop " + shopID);

        int size = FileManager.getConfig("shops").getInt(shopID + ".size");
        String displayName = FileManager.getConfig("shops").getString(shopID + ".display-name");

        Inventory inventory = Bukkit.createInventory(null, size, displayName);

        for(String itemID : FileManager.getConfig("shops").getStringList("shops." + shopID + ".items")){
            ShopItem item = new ShopItem(shopID, itemID);

            inventory.setItem(item.getSlot(), item.getItemStack());
        }

        shopList.put(shopID, inventory);
    }

    public static boolean exists(String id) { return shopList.containsKey(id); }
    public static Inventory get(String id){ return shopList.get(id); }


}
