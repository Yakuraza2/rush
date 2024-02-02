package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.timers.GState;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.entity.Player;

import static fr.rush.romain.rush.Core.lobby;
import static fr.rush.romain.rush.managers.PacketsManager.connectToServer;

public class GameManager {

    public static Rush selectRush(){
        if(Core.getWaitingList().isEmpty()){
            Core.logger(1, "Aucun rush ne semble disponible :'(");
            return null;
        }

        return Core.getWaitingList().get(0);
    }

    public static String selectRushID(){
        if(Core.getWaitingList().isEmpty()){
            Core.logger(1, "Aucun rush ne semble disponible :'(");
            return null;
        }

        return Core.getWaitingList().get(0).getID();
    }

    public static boolean join(Player p, Rush rush){

        if(rush == null) {
            p.sendMessage("§4Erreur: §cAucune game de libre, retour au lobby.");
            connectToServer(p, lobby);
            return false;
        }

        rush.addPlayer(p);
        Core.playersRush.put(p, rush);
        rush.spawnPlayer(p);
        ScoreBoardManager.clearScoreBoard(p);
        for(Player player : rush.getPlayers()){
            player.sendMessage(FileManager.getConfigMessage("join-message", p, rush));
        }
        return true;
    }


    public static void BrownBedBreak(Player player, Rush rush, Team team) {
                team.addHealBoost(1);
    }

    public static void resetPlayer(Player player) {
        if (!isPlaying(player)) return;

        Rush rush = Core.playersRush.get(player);

        if (rush.isState(GState.PLAYING) && rush.getAlivePlayers().contains(player)) {
            rush.eliminatePlayer(player, rush.getPlayerTeam(player));
            rush.broadcast(player.getName() + " a quitté la partie");
            rush.checkWin();
        }
        ScoreBoardManager.clearScoreBoard(player);
        rush.getPlayers().remove(player);
        Core.playersRush.remove(player);
    }

    public static boolean isPlaying(Player p) { return Core.playersRush.containsKey(p);}
}
