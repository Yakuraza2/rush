package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class InventoryManager {

    public static void removeItemFromInventory(Inventory inv, ItemStack item) {
        if(inv.contains(item)) { // contains the exact item
            inv.remove(item); // remove first time it find this item
        } else { // doesn't contains this item
            for(ItemStack invItem : inv.getContents()) {
                if(invItem != null && invItem.getType().equals(item.getType())) { // if it's this type of item.
                    // You can add other check specially for ItemMeta ...
                    int amount = invItem.getAmount(); // amount of actual item
                    int stay = item.getAmount(); // keep amount

                    if(amount >= stay) { // too many item, just change amount
                        invItem.setAmount(amount - stay); // change amount to remove it
                        break; // stop loop
                    } else { // not enough item
                        invItem.setAmount(0); // you can also remove the item by setting air to this slot
                        item.setAmount(stay - amount); // reduce amount of item to delete
                    }
                }
            }
        }
    }

    public static void armorGiving(Player p, ItemStack item){
        if(item.getType().name().toLowerCase().contains("helmet")) p.getInventory().setHelmet(item);
        else if(item.getType().name().toLowerCase().contains("chestplate")) p.getInventory().setChestplate(item);
        else if(item.getType().name().toLowerCase().contains("legging")) p.getInventory().setLeggings(item);
        else if(item.getType().name().toLowerCase().contains("boots")) p.getInventory().setBoots(item);
        else p.getInventory().addItem(item);
    }

    public static void giveSpawnKit(Player p){
        Rush rush = Core.playersRush.getOrDefault(p, null);
        if(rush==null) { Core.logger(1, p.getName() + " n'est dans aucun rush !"); return; }

        Team team = rush.getPlayerTeam(p);
        if(team==null) { Core.logger(1, p.getName() + " n'est dans aucune équipe dans " + rush.getID()); return; }

        giveColoredArmor(p, team.getColor());

    }

    public static void giveColoredArmor(Player p, int[] rgb){
        Material[] lArmor = new Material[]{Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS};
        for(Material LeatherPiece : lArmor) {
            ItemStack piece = new ItemStack(LeatherPiece, 1);
            LeatherArmorMeta lch = (LeatherArmorMeta) piece.getItemMeta();
            lch.setColor(Color.fromRGB(rgb[0], rgb[1], rgb[2]));
            piece.setItemMeta(lch);
            armorGiving(p, piece);
        }
    }

}
