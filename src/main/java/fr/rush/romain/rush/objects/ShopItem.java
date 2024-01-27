package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class ShopItem {

    private final ItemStack aItemStack;
    private final String aDisplayName;
    private final int aPrice;
    private final Material aPriceItem;
    private final String aItemID;
    private final int aSlot;
    private static final HashMap<String, ShopItem> itemList = new HashMap<>();

    public ShopItem(String shopID, String itemID){
        Core.logger(1, "création de l'item " + itemID + " dans " + shopID);

        String path = "shops." + shopID + ".items." + itemID;

        Material material = Material.valueOf(FileManager.getConfig("shops").getString(path + ".material"));
        int quantity = FileManager.getConfig("shops").getInt(path + ".quantity");

        this.aPrice = FileManager.getConfig("shops").getInt(path + ".price");
        this.aSlot = FileManager.getConfig("shops").getInt(path + ".slot");
        this.aPriceItem = Material.valueOf(FileManager.getConfig("shops").getString(path + ".price-item"));
        this.aItemID = itemID;
        this.aDisplayName = FileManager.getConfig("shops").getString(path + ".display-name");

        ItemStack item = new ItemStack(material, quantity);
        ItemMeta itemM = item.getItemMeta();
        itemM.setDisplayName(this.aDisplayName);

        item.setItemMeta(itemM);

        this.aItemStack = item;


        itemList.put(itemID, this);
    }

    public ShopItem get(String id){ return itemList.getOrDefault(id, null); }

    public ItemStack getItemStack(){ return this.aItemStack; }
    public String getID(){ return this.aItemID; }
    public int getPrice(){ return this.aPrice; }
    public Material getPriceItemMaterial(){ return this.aPriceItem; }
    public String getDisplayName(){ return this.aDisplayName; }

    public int getSlot() { return aSlot; }

    public ShopItem getFromShop(String shopID, int slot){
        return null;
    }
}
