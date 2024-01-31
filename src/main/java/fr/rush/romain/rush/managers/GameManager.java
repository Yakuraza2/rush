package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.GState;
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

    public static boolean join(Player p, Rush rush){

        if(rush == null) return false;

        if(rush.getPlayers().isEmpty()) rush.getAutoStart().runTaskTimer(Core.getPlugin(Core.class), 0, 20);

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
        }
        ScoreBoardManager.clearScoreBoard(player);
        Core.playersRush.remove(player);
    }

    public static boolean isPlaying(Player p) { return Core.playersRush.containsKey(p);}
}
