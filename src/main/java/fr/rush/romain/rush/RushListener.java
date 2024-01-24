package fr.rush.romain.rush;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RushListener implements Listener {

    private final Main main;
    public RushListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        final boolean[] hasJoin = {false};

        if(p.hasPermission("rush.admin")) {
            p.sendMessage("Vous avez rejoint le serveur en tant que Staff, /rush join pour rejoindre une partie");
        }

        Main.getRushsList().forEach((id, rush) -> {
            if(rush.isWaiting() && !hasJoin[0]){
                GameManager.Join(p, rush);
                hasJoin[0] = true;
            }
        });
    }

}
