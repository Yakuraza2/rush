package fr.rush.romain.rush;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandsTeam implements CommandExecutor {
    public CommandsTeam(Main main) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage("Invalid Command !");
            return false;
        }

        if (args[1].equalsIgnoreCase("add")) {
            if (args.length <= 3) player.sendMessage("§cUsage: /team add <rush_id> <team_id>");
            FileManager.set(args[2], "teams", args[3]);

            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".display-name", args[3]);
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".slots", 2);
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".color", "YELLOW");
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".spawn.x", player.getLocation().getBlockX());
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".spawn.y", player.getLocation().getBlockY());
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".spawn.z", player.getLocation().getBlockZ());
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".spawn.yaw", (int) player.getLocation().getYaw());
            FileManager.set(args[2], args[2] + ".teams." + args[3] + ".spawn.pitch", (int) player.getLocation().getPitch());

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

        String rush_id = args[1];
        String attribut = args[4];
        String team_id = args[3];
        String value = "";
        if (args.length >= 6) value = args[5];

        if (!FileManager.getConfig("rush-teams").getStringList("rush-teams").contains(rush_id)) {
            player.sendMessage("Le Rush \"" + rush_id + "\" semble ne pas exister !");
            return true;
        }

        Rush currentRush = Main.getRushsList().get(rush_id);

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