package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Shop;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static fr.rush.romain.rush.managers.FileManager.*;

public class CommandsShop implements CommandExecutor {
    public CommandsShop(Core core) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        boolean succeeded = false;
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage("§cUsage: /shop create/spawn/addItem");
            return false;
        }
        if(args[0].equalsIgnoreCase("create")) succeeded = commandCreateShop(player, args);
        else if(args[0].equalsIgnoreCase("spawn")) succeeded = commandSpawnShop(player, args);
        else if(args[0].equalsIgnoreCase("addItem")) succeeded = commandaddItem(player, args);

        return succeeded;
    }

    private boolean commandaddItem(Player player, String[] args) {
        if(args.length<6) {
            player.sendMessage("§cIncorrect usage ! /shop addItem <shop_id> <shopItem_id> <price> <priceItem> <slot>");
            player.sendMessage("INFO: priceItems are: COPPER_INGOT, IRON_INGOT, GOLD_INGOT and DIAMOND.");
            return false;
        }
        if(player.getItemInHand().isEmpty()) {
            player.sendMessage("§cYou need to have the item to sell in your hand !");
            return false;
        }
        String shopID = args[1];
        String shopItemID = args[2];
        int price = Integer.parseInt(args[3]);
        String priceItem = args[4];
        int slot = Integer.parseInt(args[5]);
        ItemStack item = player.getItemInHand();
        YamlConfiguration config = getConfig("shops");

        if(!config.getStringList("shops.list").contains(shopID)){
            player.sendMessage("§cShop " + shopID + " doesn't exists");
            return true;
        }
        if(slot >= config.getInt("shops." + shopID + ".size")){
            player.sendMessage("§cShop " + shopID + " has less than " + slot + " slots");
            return true;
        }
        String path = "shops." + shopID + ".items." + shopItemID;
        config.set(path + ".display-name", item.getItemMeta().getDisplayName());
        config.set(path + ".material", item.getType().name());
        config.set(path + ".quantity", item.getAmount());
        config.set(path + ".price", price);
        config.set(path + ".priceItem", priceItem);
        config.set(path + ".slot", slot);

        FileManager.save(config, get("shops"));

        return false;
    }

    private boolean commandCreateShop(Player player, String[] args) {
        if(args.length <= 3) {
            player.sendMessage("Incorrect Usage ! /rush create <shop_id> <display name>");
            return false;
        }

        String shopID = args[1];
        if(Shop.getList().containsKey(shopID)) {
            player.sendMessage("Le shop " + shopID + " existe déjà !");
            return true;
        }

        int size = Integer.parseInt(args[2]);
        if(size%9!=0) {
            player.sendMessage("La taille du shop doit être un multiple de 9 !");
            return true;
        }

        StringBuilder str = new StringBuilder();
        for(int i =3; i< args.length; i++){
            str.append(args[i]).append(" ");
        }
        String displayName = str.toString().replace("&", "§");

        YamlConfiguration config = getConfig("shops");

        List<String> shopsList = config.getStringList("shops.list");
        shopsList.add(shopID);

        config.set("shops.list", shopsList);
        config.set("shops." + shopID + ".display-name", displayName);
        config.set("shops." + shopID + ".size", size);

        player.sendMessage("&6Un nouveau shop " + displayName + " vient d'être créé !");

        FileManager.save(config, get("shops"));
        return true;
    }

    private boolean commandSpawnShop(Player p, String[] args) {
        if(args.length <= 3) {
            p.sendMessage("Incorrect Usage ! /rush spawnshop <shop_id> <display name>");
            return false;
        }
        World world = p.getWorld();
        Location loc = p.getLocation();
        String shopID = args[1];

        if(!Shop.getList().containsKey(shopID)) p.sendMessage(shopID + " n'existe pas !");

        Villager categoriesEntity = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
        categoriesEntity.setProfession(Villager.Profession.ARMORER);
        categoriesEntity.setCustomNameVisible(true);
        categoriesEntity.setAI(false);
        categoriesEntity.setInvulnerable(true);
        categoriesEntity.addScoreboardTag(shopID);
        categoriesEntity.addScoreboardTag("shop");

        StringBuilder str = new StringBuilder();
        for(int i =2; i< args.length; i++){
            str.append(args[i]).append(" ");
        }

        categoriesEntity.setCustomName(str.toString().replace("&", "§"));
        return true;
    }
}
