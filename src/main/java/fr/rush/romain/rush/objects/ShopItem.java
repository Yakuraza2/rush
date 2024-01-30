package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.ItemsManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

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
    private final Material aMaterial;
    private static final HashMap<String, ShopItem> itemList = new HashMap<>();

    public ShopItem(String shopID, String itemID){
        logger(1, "création de l'item " + itemID + " dans " + shopID);

        String path = "shops." + shopID + ".items." + itemID;

        aMaterial = Material.valueOf(getConfig("shops").getString(path + ".material"));
        int quantity = getConfig("shops").getInt(path + ".quantity");

        this.aPrice = getConfig("shops").getInt(path + ".price");
        this.aSlot = getConfig("shops").getInt(path + ".slot");
        this.aPriceItem = Material.valueOf(getConfig("shops").getString(path + ".price-item"));
        this.aItemID = itemID;
        this.aDisplayName = getConfig("shops").getString(path + ".display-name");

        ItemStack item = new ItemStack(aMaterial, quantity);
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

    public void buy(Player player){

        String name = " ";
        YamlConfiguration config = getConfig();

        if (this.aPriceItem.equals(Material.COPPER_INGOT))    name = config.getString("shops.diplay-names.bronze");
        else if (this.aPriceItem.equals(Material.IRON_INGOT)) name = config.getString("shops.diplay-names.bronze");
        else if (this.aPriceItem.equals(Material.GOLD_INGOT)) name = config.getString("shops.diplay-names.bronze");
        else if (this.aPriceItem.equals(Material.DIAMOND))    name = config.getString("shops.diplay-names.bronze");
        else { logger("price-item error in the config file for : " + this.getID()); return; }

        ItemStack priceItem = ItemsManager.create(this.aPriceItem, name, this.aPrice);

        //Verifier si le joueur à l'argent nécéssaire
        if (player.getInventory().contains(this.aPriceItem, this.aPrice)) {

            //On retirer l'argent de l'inventaire
            removeItemFromInventory(player.getInventory(), priceItem);
            logger(player.getName() + " just buy " + this.aItemID);
            player.sendMessage(getConfigMessage("item-buy", Core.playersRush.get(player))
                    .replace("<item>", name));

            //Si l'item acheté est une armure alors il faut lui mettre directement
            if(aItemStack.getItemMeta() instanceof ArmorMeta){
                logger("ARMOR BUYING");
                armorGiving(player, this.aItemStack);
            }else{
                player.getInventory().addItem(this.aItemStack);
            }
        } else {
            logger(player.getName() + " can't buy " + this.getID() + " : not enough money");
        }

        removeItemFromInventory(player.getInventory(), priceItem);
    }
}
