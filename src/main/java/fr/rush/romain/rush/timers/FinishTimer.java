package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FinishTimer extends BukkitRunnable {

    private int timer;
    private final int staticTimer;
    private final Rush rush;
    public FinishTimer(Rush pRush) {

        this.rush = pRush;
        this.staticTimer = FileManager.getConfig(rush.getID()).getInt("timers.finish-waiting");
        this.timer = this.staticTimer;
    }

    @Override
    public void run() {

        if(!rush.isState(GState.FINISH)) {
            Core.logger(4, "The finish timer was started during the rush " + rush.getID() + " was not in Finish State !");
            cancel();
        }
        if(this.timer == this.staticTimer) {
            for (Player winner : rush.getAlivePlayers()) {
                winner.sendMessage(FileManager.getConfigMessage("winners-message", winner, rush));
            }
        }

        if(this.timer==this.staticTimer/2){
            for(Player winner : rush.getAlivePlayers()){
                winner.setGameMode(GameMode.SPECTATOR);
            }

            rush.broadcast(FileManager.getConfigMessage("last-seconds", rush));
        }

        if(this.timer <=0){
            rush.reset();
            this.timer = this.staticTimer;
            cancel();
        }
        this.timer--;
    }
}
