package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.entity.Player;

public class GameManager {

    public static Rush selectRush(){
        if(Core.getWaitingList().isEmpty()){
            Core.logger(1, "Aucun rush ne semble disponible :'(");
            return null;
        }

        return Core.getWaitingList().get(0);
    }

    public static boolean Join(Player p, Rush rush){

        if(rush == null) return false;

        if(rush.getPlayers().isEmpty()) rush.getAutoStart().runTaskTimer(Core.getPlugin(Core.class), 0, 20);

        rush.addPlayer(p);
        Core.playersRush.put(p, rush);
        rush.spawnPlayer(p);
        for(Player player : rush.getPlayers()){
            player.sendMessage(FileManager.getConfigMessage("join-message", p, rush));
        }
        return true;
    }

    public static void Quit(Player p, Rush rush){
        rush.removePlayer(p);
    }

    public static void BrownBedBreak(Player player, Rush rush, Team team) {
                team.addHealBoost(1);
    }
}
