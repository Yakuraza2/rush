package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.entity.Player;

public class GameManager {

    public static Rush selectRush(){
        if(Core.getWaitingList().isEmpty()){
            Core.logger(1, "Aucun rush ne semble disponible :'(");
            return null;
        }

        return Core.getWaitingList().get(0);
    }

    public static void Join(Player p, Rush rush){

        if(rush == null) return;

        if(rush.getAutoStart().isCancelled()) rush.getAutoStart().runTaskTimer(Core.getPlugin(Core.class), 0, 20);

        rush.addPlayer(p);
        rush.spawnPlayer(p);
        for(Player player : rush.getPlayers()){
            player.sendMessage(FileManager.getConfigMessage("join-message", p, rush));
        }
    }

    public static void Quit(Player p, Rush rush){
        rush.removePlayer(p);
    }

    public static void giveSpawnKit(Player player) {

    }

    public static void putHealBoost(Player player) {
    }
}
