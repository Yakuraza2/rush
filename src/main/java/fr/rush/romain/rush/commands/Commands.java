package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class Commands implements CommandExecutor {
    public Commands(Core main) {
    }

    @Override
    public boolean onCommand(CommandSender player, Command command, String s, String[] args) {
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
            AtomicBoolean hasJoin = new AtomicBoolean(false);
            Core.getRushsList().forEach((id, rush) -> {
                if (rush.isState(GState.WAITING_FOR_PLAYERS) && !hasJoin.get()) {
                    GameManager.Join(p, rush);
                    hasJoin.set(true);
                }
            });
        }
        if(args[0].equalsIgnoreCase("reload")){
            Core.load();
        }

        if(args[0].equalsIgnoreCase("create")){
            if(args.length != 2 ) { player.sendMessage("§cUsage: /rush create <rush_id>"); }
            FileManager.getConfig("rush-list").set("rush-list", FileManager.getConfig("rush-list").getStringList("rush-list").add(args[1]));
            Core.createGame(args[1]);
            player.sendMessage("Création du fichier pour la partie " + args[1] + " il faudra redémarrer le serveur après configuration !");
        }



        return false;
    }

}
