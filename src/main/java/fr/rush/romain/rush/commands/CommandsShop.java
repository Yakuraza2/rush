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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandsShop implements CommandExecutor {
    public CommandsShop(Core core) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        boolean succeeded = false;
        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage("§cUsage: /rush <command> <rush_id> <parameter>");
            return false;
        }
        if(args[0].equalsIgnoreCase("create")) succeeded = commandCreateShop(player, args);
        if(args[0].equalsIgnoreCase("spawn")) succeeded = commandSpawnShop(player, args);

        return succeeded;
    }

    private boolean commandCreateShop(Player player, String[] args) {
        if(args.length <= 3) {
            player.sendMessage("Incorrect Usage ! /rush spawnshop <shop_id> <display name>");
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

        YamlConfiguration config = FileManager.getConfig("shops");

        List<String> shopsList = config.getStringList("shops.list");
        shopsList.add(shopID);

        config.set("shops.list", shopsList);
        config.set("shops." + shopID + ".display-name", displayName);
        config.set("shops." + shopID + ".size", size);

        FileManager.save(config, FileManager.get("shops"));
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
