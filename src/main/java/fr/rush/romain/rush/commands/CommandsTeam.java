package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CommandsTeam implements CommandExecutor {
    public CommandsTeam(Core main) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Invalid Command !");
            return false;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 2) player.sendMessage("§cUsage: /team add <rush_id> <team_id>");
            if (!FileManager.getConfig("rush-list").getStringList(args[1]).contains(args[1])) {
                player.sendMessage("Le Rush \"" + args[1] + "\" semble ne pas exister !");
                return true;
            }
            FileManager.getConfig(args[1]).set("teams", FileManager.getConfig(args[1]).getStringList("teams").add(args[2]));

            FileManager.set(args[1], args[1] + ".teams." + args[2] + ".display-name", args[3]);
            FileManager.set(args[1], args[1] + ".teams." + args[2] + ".slots", 2);
            FileManager.set(args[1], args[1] + ".teams." + args[2] + ".color", "YELLOW");
            FileManager.set(args[1], args[1] + ".teams." + args[2] + ".spawn", String.valueOf(player.getLocation()));
            FileManager.set(args[1], args[1] + ".teams." + args[2] + ".item-spawner", String.valueOf(player.getLocation()));

            FileManager.set(args[1], args[1] + "teams." + args[2] + ".color.red", (int) (Math.random()*30));
            FileManager.set(args[1], args[1] + "teams." + args[2] + ".color.green", (int) (Math.random()*30));
            FileManager.set(args[1], args[1] + "teams." + args[2] + ".color.blue", (int) (Math.random()*30));

            return true;
        }

        /**
         * CHANGEMENT DES ATTRIBUTS D'UNE TEAM
         * Sous la forme /team [rush_id] set [attribut] [valeur]
         */

        if (args.length < 5) {
            player.sendMessage("Incorrect Usage: /team [rush_id] set [team] [setting] [value]");
            return false;
        }
        if(!args[2].equalsIgnoreCase("set")) {
            player.sendMessage("Unknown command !");
            return false;
        }

        String rush_id = args[0];
        String attribut = args[3];
        String team_id = args[2];
        String value = "";
        if (args.length >= 6) value = args[4];

        if (!FileManager.getConfig("rush-list").getStringList("rush-list").contains(rush_id)) {
            player.sendMessage("Le Rush \"" + rush_id + "\" semble ne pas exister !");
            return true;
        }

        Rush currentRush = Core.getRushsList().get(rush_id);

        if (!FileManager.getConfig(rush_id).getStringList("teams").contains(team_id)) {
            player.sendMessage("L'équipe \"" + team_id + "\" semble ne pas exister pour " + rush_id + " !");
            return true;
        }

        Team currentTeam = currentRush.getTeam(team_id);

        switch (attribut) {
            case "slots":
                currentTeam.setSize(Integer.parseInt(value));
                break;
            case "display-name":
                currentTeam.setDisplayName(value);
                break;
            case "spawn":
                FileManager.set(rush_id, "teams." + team_id + ".spawn.x", player.getLocation().getBlockX());
                FileManager.set(rush_id, "teams." + team_id + ".spawn.y", player.getLocation().getBlockY());
                FileManager.set(rush_id, "teams." + team_id + ".spawn.z", player.getLocation().getBlockZ());
                FileManager.set(rush_id, "teams." + team_id + ".spawn.yaw", (int) player.getLocation().getYaw());
                FileManager.set(rush_id, "teams." + team_id + ".spawn.pitch", (int) player.getLocation().getPitch());
                break;
            default:
                player.sendMessage("Given setting doesn't exists !");
                return false;

            //Pour changer les parametres des teams: mettre la fin du path en args
        }

        return true;
    }
}