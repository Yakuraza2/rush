package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class CommandsTeam implements CommandExecutor {
    public CommandsTeam(Core main) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        boolean succeeded = false;
        Player player = (Player) sender;
        if (args.length < 1) player.sendMessage("Invalid Command !");

        if (args[0].equalsIgnoreCase("add")) succeeded = commandAdd(player, args);
        if(args[1].equalsIgnoreCase("set")) succeeded = commandSet(player, args);

        return succeeded;
    }

    private boolean commandSet(Player player, String[] args) {

        if (args.length < 4) {
            player.sendMessage("Incorrect Usage: /team [rush_id] set [team] [setting] [value]");
            return false;
        }

        String rush_id = args[0];
        String attribut = args[3];
        String team_id = args[2];

        StringBuilder str = new StringBuilder();
        if(args.length > 4){
            str.append(args[4]);
            for(int i=5; i<args.length; i++) str.append(" ").append(args[i]);
        }
        String value = str.toString();


        if (!FileManager.getConfig("rush-list").getStringList("rush-list").contains(rush_id))
            player.sendMessage("Le Rush \"" + rush_id + "\" semble ne pas exister !");

        Rush currentRush = Core.getRushsList().get(rush_id);
        YamlConfiguration config = FileManager.getConfig(rush_id);

        if (!config.getStringList("teams.list").contains(team_id)) player.sendMessage("L'équipe \"" + team_id + "\" semble ne pas exister pour " + rush_id + " !");

        Team currentTeam = currentRush.getTeam(team_id);

        switch (attribut) {
            case "slots":
                currentTeam.setSize(Integer.parseInt(value));
                FileManager.set(config, "teams." + team_id + ".slots", Integer.parseInt(value));
                break;
            case "display-name":
                currentTeam.setDisplayName(value);
                FileManager.set(config, "teams." + team_id + ".display-name", value);
                break;
            case "spawn":
                FileManager.setLocation(config, "teams." + team_id + ".spawn", player.getLocation());
                break;
            case "item-spawner":
                FileManager.setLocation(config, "teams." + team_id + ".item-spawner", player.getLocation());
                currentTeam.setItemsSpawners(player.getLocation());
                break;
            default:
                player.sendMessage("Given setting doesn't exists !");
                return false;
            //Pour changer les parametres des teams: mettre la fin du path en args
        }

        FileManager.save(config, FileManager.get(rush_id));
        player.sendMessage("Vous avez changé la valeur de " + attribut + " a " + value);
        return true;
    }

    private boolean commandAdd(Player player, String[] args) {
        if (args.length != 4) {
            player.sendMessage("§cUsage: /team add <rush_id> <team_id> <display-name>");
            return false;
        }
        String teamID = args[2].toLowerCase();
        String rushID = args[1].toLowerCase();
        YamlConfiguration config = FileManager.getConfig(rushID);

        if (!Core.getRushsList().containsKey(rushID)) player.sendMessage("Le Rush \"" + rushID + "\" semble ne pas exister !");
        //Adding the team to the rush's team-list
        List<String> list = config.getStringList("teams.list");
        list.add(teamID);
        config.set("teams.list", list);

        FileManager.set(config, "teams." + teamID + ".display-name", args[3]);
        FileManager.set(config, "teams." + teamID + ".slots", 2);
        FileManager.set(config, "teams." + teamID + ".color", "YELLOW");
        FileManager.setLocation(config, "teams." + teamID + ".spawn", player.getLocation());
        FileManager.setLocation(config, "teams." + teamID + ".item-spawner", player.getLocation());

        FileManager.set(config, "teams." + teamID + ".color.red", (int) (Math.random()*255));
        FileManager.set(config, "teams." + teamID + ".color.green", (int) (Math.random()*255));
        FileManager.set(config, "teams." + teamID + ".color.blue", (int) (Math.random()*255));

        FileManager.set(config, "teams." + teamID + ".bed-material", "BLACK_BED");

        FileManager.save(config, FileManager.get(rushID));
        return true;
    }
}