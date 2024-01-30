package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class Shop {

    private static final HashMap<String, Shop> shopList = new HashMap<>();
    private static final HashMap<String, Shop> shopFromDisplayName = new HashMap<>();
    private final HashMap<Integer, ShopItem> itemInSlot = new HashMap<>();
    private final Inventory aInventory;
    private final String displayName;
    private final int size;

    public Shop(String shopID){
        Core.logger(1, "création du shop " + shopID);
        YamlConfiguration config = FileManager.getConfig("shops");

        size        = config.getInt("shops." + shopID + ".size");
        displayName = config.getString("shops." + shopID + ".display-name");
        aInventory = Bukkit.createInventory(null, size, displayName);

        shopFromDisplayName.put(displayName, this);

        for(String itemID : config.getStringList("shops." + shopID + ".items-list")){
            ShopItem item = new ShopItem(shopID, itemID);
            aInventory.setItem(item.getSlot(), item.getItemStack());
            this.itemInSlot.put(item.getSlot(), item);
        }

        Core.logger(1, shopID + " créé !");
        shopList.put(shopID, this);
    }
    public static HashMap<String, Shop> getList(){ return shopList; }
    public static Shop get(String id){ return shopList.getOrDefault(id, null); }
    public static Shop getFromDisplayName(String displayName){ return shopFromDisplayName.getOrDefault(displayName, null); }

    public ShopItem getItem(int slot){ return this.itemInSlot.getOrDefault(slot, null); }
    public Inventory getInventory(){ return this.aInventory; }
    public String getDisplayName(){ return this.displayName; }
    public int getSize() { return this.size; }

    public static void openShop(Player player, String shopID){
        Shop shop = Shop.get(shopID);
        Core.logger("Opening " + shopID + " for " + player.getName());
        player.openInventory(shop.getInventory());
    }


}
