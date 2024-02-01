package fr.rush.romain.rush.timers;

import org.bukkit.scheduler.BukkitRunnable;

import static fr.rush.romain.rush.managers.PacketsManager.sendSlotsToProxy;

public class CoreTimer extends BukkitRunnable {

    private int timer = 0;
    @Override
    public void run() {
        if(timer == 5) {
            sendSlotsToProxy();
            timer=0;
        }

        timer++;
    }
}
