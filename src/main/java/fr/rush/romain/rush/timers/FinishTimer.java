package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FinishTimer extends BukkitRunnable {

    private static int timer = 0;
    private static int staticTimer = 0;
    private final Rush rush;
    public FinishTimer(Rush pRush) {

        this.rush = pRush;
        timer = (int) FileManager.getConfig().get("timers.finishing");
        staticTimer = timer;
    }

    @Override
    public void run() {

        if(!rush.isState(GState.FINISH)) {
            Core.logger(4, "The finish timer was started during the rush " + rush.getID() + " was not in Finish State !");
            cancel();
        }
        for(Player winner : rush.getAlivePlayers()){
            winner.sendMessage(FileManager.getConfigMessage("winners-message", winner, rush));
        }

        if(timer==staticTimer/2){
            for(Player winner : rush.getAlivePlayers()){
                winner.setGameMode(GameMode.SPECTATOR);
            }

            rush.broadcast(FileManager.getConfigMessage("last-seconds", rush));
        }

        if(timer <=0){
            rush.reset();
            cancel();
        }
        timer--;
    }
}
