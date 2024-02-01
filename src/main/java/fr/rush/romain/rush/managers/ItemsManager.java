package fr.rush.romain.rush.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemsManager {

    //Potentiellement un probleme de perfs si tous les items sont recréés à chauqe fois (ca va faire bcp d'objets
    public static ItemStack create(Material material, String name, int quantity, List<String> lore){
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();
        if(name != null) meta.setDisplayName(name);
        if(lore != null) meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack create(Material material, String name, int quantity){
        return create(material, name, quantity, null);
    }

}
