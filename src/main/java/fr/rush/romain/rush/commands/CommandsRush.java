package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandsRush implements CommandExecutor {
    public CommandsRush(Core main) {
    }
    @Override
    public boolean onCommand(@NotNull CommandSender player, @NotNull Command command, @NotNull String s, String[] args) {
        Player p = (Player) player;
        boolean succeeded = false;

        if(args.length == 0) {
            player.sendMessage("§cUsage: /rush <command> <rush_id> <parameter>");
            return false;
        }
        if(args[0].equalsIgnoreCase("list")) succeeded = commandList(p, args);
        else if(args[0].equalsIgnoreCase("join")) succeeded = GameManager.join(p, GameManager.selectRush());
        else if(args[0].equalsIgnoreCase("reload")) succeeded = Core.loadGames();
        else if(args[0].equalsIgnoreCase("create")) succeeded = commandCreate(p, args);
        else if(args[0].equalsIgnoreCase("set")) succeeded = commandSet(p, args);


        return succeeded;
    }

    private boolean commandSet(Player p, String[] args) {
        if(args.length != 3 ) {
            p.sendMessage("§cUsage: /rush set <parameter> <rush_id> (<value>)");
            p.sendMessage("§6Parameters list: §elobby, spectator-spawn, lobby-waiting, finish-waiting, player-zone-verif, bronze, iron, gold, diamond");
        }

        String parameter = args[1];
        String rushid = args[2];

        if(!Core.getRushsList().containsKey(rushid)){
            p.sendMessage(rushid + " n'existe pas !");
            return true;
        }

        YamlConfiguration config = FileManager.getConfig(rushid);

        if(parameter.equalsIgnoreCase("lobby") || parameter.equalsIgnoreCase("spectator-spawn")){
            FileManager.setLocation(config, rushid + "." + parameter.toLowerCase(), p.getLocation());
            FileManager.save(config, FileManager.get(rushid));
            p.sendMessage(parameter + " changed !");
            return true;
        }
        else if(parameter.equalsIgnoreCase("lobby-waiting") || parameter.equalsIgnoreCase("finish-waiting")
                || parameter.equalsIgnoreCase("player-zone-verif") || parameter.equalsIgnoreCase("bronze") ||
                parameter.equalsIgnoreCase("iron") ||parameter.equalsIgnoreCase("gold") ||parameter.equalsIgnoreCase("diamond")){

            if(args.length < 4) {
                p.sendMessage("§cYou need to give a value for " + parameter);
                return false;
            }
            int value = Integer.parseInt(args[3]);
            FileManager.set(config, rushid + ".timers." + parameter.toLowerCase(), value);
            FileManager.save(config, FileManager.get(rushid));
            p.sendMessage(parameter + " changed !");
            return true;
        }

        p.sendMessage("Commande non reconnue");
        return false;
    }

    private boolean commandCreate(Player player, String[] args) {
        if(args.length != 2 ) {
            player.sendMessage("§cUsage: /rush create <rush_id>");
            return false;
        }

        YamlConfiguration config = FileManager.getConfig("rush-list");
        List<String> list = FileManager.getConfig("rush-list").getStringList("rush-list");
        String rushID = args[1];

        Core.logger("there is :" + list + " in rush-list." );
        list.add(rushID);
        config.set("rush-list", list);
        FileManager.save(config, FileManager.get("rush-list"));

        player.sendMessage("Création du fichier pour la partie " + args[1] + " il faudra redémarrer le serveur après configuration !");
        Core.createGame(player, args[1]);

        Core.loadGame(args[1]);
        return true;
    }

    private boolean commandList(Player player, String[] args) {
        if(Core.getRushsList().isEmpty()){
            player.sendMessage("there is any rush game !");
        }
        StringBuilder msg = new StringBuilder("Games list: ");
        Core.getRushsList().forEach((key, value) -> {
            msg.append(key).append(", ");
        });
        player.sendMessage(String.valueOf(msg));
        return true;
    }

}
