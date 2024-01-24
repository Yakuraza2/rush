package fr.rush.romain.rush;

import org.bukkit.entity.Player;

public class GameManager {

    public static void Join(Player p, Rush rush){
        rush.addPlayer(p);
        p.teleport(rush.getLobby());
    }

}
