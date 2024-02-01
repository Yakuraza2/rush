package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.rush.romain.rush.Core.logger;
import static fr.rush.romain.rush.managers.FileManager.getConfig;
import static fr.rush.romain.rush.managers.FileManager.getConfigMessage;
import static fr.rush.romain.rush.managers.InventoryManager.armorGiving;
import static fr.rush.romain.rush.managers.InventoryManager.removeItemFromInventory;

public class ShopItem {

    private final ItemStack aItemStack;
    private final String aDisplayName;
    private final int aPrice;
    private final Material aPriceItem;
    private final String aItemID;
    private final int aSlot;
    private String aPriceItemName = "§cError";
    private static final HashMap<String, ShopItem> itemList = new HashMap<>();
    public ShopItem(String shopID, String itemID){
        logger(1, "création de l'item " + itemID + " dans " + shopID);
        YamlConfiguration config = getConfig();
        String path = "shops." + shopID + ".items." + itemID;

        Material aMaterial = Material.valueOf(getConfig("shops").getString(path + ".material"));
        int quantity = getConfig("shops").getInt(path + ".quantity");

        this.aPrice = getConfig("shops").getInt(path + ".price");
        this.aSlot = getConfig("shops").getInt(path + ".slot");
        this.aPriceItem = Material.matchMaterial(getConfig("shops").getString(path + ".price-item"));
        this.aItemID = itemID;
        this.aDisplayName = getConfig("shops").getString(path + ".display-name");

        if (this.aPriceItem.equals(Material.COPPER_INGOT))    aPriceItemName = config.getString("shops.display-names.bronze");
        else if (this.aPriceItem.equals(Material.IRON_INGOT)) aPriceItemName = config.getString("shops.display-names.iron");
        else if (this.aPriceItem.equals(Material.GOLD_INGOT)) aPriceItemName = config.getString("shops.display-names.gold");
        else if (this.aPriceItem.equals(Material.DIAMOND))    aPriceItemName = config.getString("shops.display-names.diamond");
        else { logger("price-item error in the config file for : " + this.getID()); }

        List<String> lore = new ArrayList<>();
        lore.add(getConfig().getString("shops.display-names.price-prefix")+" " + this.aPrice + "x " + this.aPriceItemName);
        this.aItemStack = ItemsManager.create(aMaterial, this.aDisplayName, quantity, lore);

        itemList.put(itemID, this);
    }

    public ShopItem get(String id){ return itemList.getOrDefault(id, null); }

    public ItemStack getItemStack(){ return this.aItemStack; }
    public String getID(){ return this.aItemID; }
    public int getPrice(){ return this.aPrice; }
    public Material getPriceItemMaterial(){ return this.aPriceItem; }
    public String getDisplayName(){ return this.aDisplayName; }

    public int getSlot() { return aSlot; }

    //public ShopItem getFromShop(String shopID, int slot){return null;}

    public void buy(Player player){
        ItemStack priceItem = ItemsManager.create(this.aPriceItem, this.aPriceItemName, this.aPrice);

        //Verifier si le joueur à l'argent nécéssaire
        if (player.getInventory().contains(this.aPriceItem, this.aPrice)) {

            //On retirer l'argent de l'inventaire
            removeItemFromInventory(player.getInventory(), priceItem);
            logger(player.getName() + " just buy " + this.aItemID + " for " + this.aPrice + " " + this.aPriceItem);
            player.sendMessage(getConfigMessage("item-buy", Core.playersRush.get(player))
                    .replace("<item>", this.aDisplayName));

            //Si l'item acheté est une armure alors il faut lui mettre directement
            if(aItemStack.getItemMeta() instanceof ArmorMeta){
                logger("ARMOR BUYING");
                armorGiving(player, this.aItemStack);
            }else{
                player.getInventory().addItem(this.aItemStack);
            }
        } else {
            logger(player.getName() + " can't buy " + this.getID() + " : not enough money");
            player.sendMessage("&cVous n'avez pas assez d'argent pour cela !");
        }
    }
}
