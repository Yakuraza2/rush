package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.objects.Rush;
import org.bukkit.scheduler.BukkitRunnable;

import static fr.rush.romain.rush.Core.logger;


public class RushTimer extends BukkitRunnable {
    private final Rush rush;
    private Starting starting;
    private Playing playing;
    private Finish finish;

    public RushTimer(Rush pRush){
        rush = pRush;
        starting = new Starting(this.rush);
        playing = new Playing(this.rush);
        finish = new Finish(this.rush);
    }

    @Override
    public void run() {

        if(this.rush.isState(GState.WAITING_FOR_PLAYERS) || this.rush.isState(GState.STARTING)) starting.StartingMethod();
        else if(this.rush.isState(GState.PLAYING)) playing.PlayingMethod();
        else if(this.rush.isState(GState.FINISH)) finish.FinishMethod();

        else logger(4, "Le rush " + rush.getID() + " ne semble être dans aucun des 4 états disponibles.");

    }
}
