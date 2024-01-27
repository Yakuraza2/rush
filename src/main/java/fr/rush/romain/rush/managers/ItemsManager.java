package fr.rush.romain.rush.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemsManager {

    //Potentiellement un probleme de perfs si tous les items sont recréés à chauqe fois (ca va faire bcp d'objets
    public static ItemStack create(Material material, String name, int quantity){
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();
        if(name != null) meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

}
