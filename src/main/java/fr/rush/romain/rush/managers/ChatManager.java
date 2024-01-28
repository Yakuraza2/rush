package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.objects.Rush;
import org.bukkit.entity.Player;

public class ChatManager {

    public static void sendTeamMessage(Player p, Rush rush, String msg){
        rush.getPlayerTeam(p).broadcast(FileManager.getConfigMessage("team-chat", p, false, rush, 0)
                .replace("<team>", FileManager.getConfig(rush.getID()).getString(rush.getID() + ".teams."
                        + rush.getPlayerTeam(p).getId() + ".display-name") + " " + msg));
    }

    public static void sendMessage(Player p, Rush rush, String msg, boolean teams){
        String replace;
        if(teams) replace = FileManager.getConfig(rush.getID()).getString(rush.getID() + ".teams." + rush.getPlayerTeam(p).getId() + ".display-name");
        else replace = "";
        for(Player players : rush.getAlivePlayers()){
            players.sendMessage(FileManager.getConfigMessage("global-chat", p, rush, false)
                    .replace("<team>", replace) + " " + msg);
        }
    }

    public static void sendSpecMessage(Player p,Rush rush, String msg){
        for(Player players : rush.getPlayers()){
            if(!rush.getAlivePlayers().contains(players)){
                players.sendMessage(FileManager.getConfigMessage("spectator-chat", p, rush, false) + " " + msg);
            }
        }
    }

}
