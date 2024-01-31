package fr.rush.romain.rush.commands;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Zone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandsZone implements CommandExecutor {
    public CommandsZone(Core core) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        // /zone add <rush> <x1> <z1> <x2> <y2>
        Player player = (Player) sender;

        if(args.length != 5) {
            player.sendMessage("§cVeuillez utiliser la commande : /zone add <rush_id> <x1> <z1> <x2> <z2>");
            return false;
        }

        String rushID = args[0];
        if(!Core.getRushsList().containsKey(rushID)){
            player.sendMessage("Le rush " + rushID + " n'existe pas !");
            return true;
        }

        Rush rush = Core.getRushsList().get(rushID);

        int x1 = Integer.parseInt(args[1]);
        int x2 = Integer.parseInt(args[3]);
        int z1 = Integer.parseInt(args[2]);
        int z2 = Integer.parseInt(args[4]);

        if(x1>x2) {
            int c = x1;
            x1 = x2;
            x2 = c;
        }
        if(z1>z2) {
            int c = z1;
            z1 = z2;
            z2 = c;
        }
        String id = "" + rush.getZones().size();


        Zone zone = new Zone(rushID, id, x1, z1, x2, z2);

        return false;
    }
}
