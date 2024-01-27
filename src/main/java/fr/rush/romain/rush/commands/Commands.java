package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import fr.rush.romain.rush.managers.ShopManager;
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

public class Commands implements CommandExecutor {
    public Commands(Core main) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender player, @NotNull Command command, @NotNull String s, String[] args) {
        Player p = (Player) player;

        if(args.length == 0) {
            player.sendMessage("§cUsage: /rush <command> <rush_id> <parameter>");
            return true;
        }
        if(args[0].equalsIgnoreCase("list")) {

            if(Core.getRushsList().isEmpty()){
                player.sendMessage("there is any rush game !");
                return true;
            }
            StringBuilder msg = new StringBuilder("Games list: ");
            Core.getRushsList().forEach((key, value) -> {
                msg.append(key).append(", ");
            });
            player.sendMessage(String.valueOf(msg));
            return true;
        }
        if(args[0].equalsIgnoreCase("join")) {
            GameManager.Join(p, GameManager.selectRush());
        }
        if(args[0].equalsIgnoreCase("reload")){
            Core.loadGames();
        }

        if(args[0].equalsIgnoreCase("create")){
            if(args.length != 2 ) { player.sendMessage("§cUsage: /rush create <rush_id>"); }

            YamlConfiguration config = FileManager.getConfig("rush-list");
            if(FileManager.getConfig("rush-list").getStringList("rush-list").isEmpty()) {
                config.set("rush-list", args[1]);
            } else {
                config.set("rush-list", FileManager.getConfig("rush-list").getStringList("rush-list").add(args[1]));
            }
            FileManager.save(config, FileManager.get("rush-list"));

            player.sendMessage("Création du fichier pour la partie " + args[1] + " il faudra redémarrer le serveur après configuration !");
            Core.createGame(p, args[1]);

            Core.loadGame(args[1]);
        }

        if(args[0].equalsIgnoreCase("spawnshop")){
            if(args.length < 2) {
                p.sendMessage("Incorrect Usage ! /rush spawnshop <shop_id> <Villager's name>");
                return false;
            }
            World world = p.getWorld();
            Location loc = p.getLocation();
            String shopID = args[1];

            if(!ShopManager.exists(shopID)){
                p.sendMessage(shopID + " n'existe pas !");
                return true;
            }

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

            categoriesEntity.setCustomName(str.toString());
        }

        if(args[0].equalsIgnoreCase("set")){
            if(args.length != 3 ) { player.sendMessage("§cUsage: /rush set <parameter> <rush_id>"); }

            String parameter = args[1];
            String rushid = args[2];

            if(!Core.getRushsList().containsKey(rushid)){
                p.sendMessage(rushid + " n'existe pas !");
                return false;
            }

            YamlConfiguration config = FileManager.getConfig(rushid);

            if(parameter.equalsIgnoreCase("lobby")){
                FileManager.setLocation(config, rushid + ".lobby", p.getLocation());
                FileManager.save(config, FileManager.get(rushid));
                p.sendMessage("Lobby changed !");
                return true;
            }

            p.sendMessage("Commande non reconnue");
        }



        return false;
    }

}
