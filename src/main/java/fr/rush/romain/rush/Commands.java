package fr.rush.romain.rush;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    public Commands(Main main) {
    }

    @Override
    public boolean onCommand(CommandSender player, Command command, String s, String[] args) {
        Player p = (Player) player;

        if(args.length == 1) {
            player.sendMessage("§cUsage: /rush <command> <rush_id> <parameter>");
            return true;
        }
        if(args[1].equalsIgnoreCase("list")) {

            if(Main.getRushsList().isEmpty()){
                player.sendMessage("there is any rush game !");
                return true;
            }
            StringBuilder msg = new StringBuilder("Games list: ");
            Main.getRushsList().forEach((key, value) -> {
                msg.append(key).append(", ");
            });
            player.sendMessage(String.valueOf(msg));
            return true;
        }
        if(args[1].equalsIgnoreCase("reload")){
            Main.load();
        }

        if(args[1].equalsIgnoreCase("create")){
            if(args.length != 3 ) { player.sendMessage("§cUsage: /rush create <rush_id>"); }
            Main.createGame(args[2]);
            player.sendMessage("Création du fichier pour la partie " + args[2] + " il faudra redémarrer le serveur après configuration !");
        }



        return false;
    }

}
