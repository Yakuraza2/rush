package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AutoStart extends BukkitRunnable {

    private int timer;
    private final Rush rush;

    public AutoStart(Rush pRush){
        rush = pRush;
        timer = 0;
    }

    @Override
    public void run() {
        if(rush.getPlayers().size() < rush.getSlots()){
            if(timer > 5) timer=0;
            if(!rush.isState(GState.WAITING_FOR_PLAYERS)) rush.setState(GState.WAITING_FOR_PLAYERS);
            if(rush.getPlayers().isEmpty())return;
            if(timer==0) rush.broadcast(FileManager.getConfigMessage("slots-not-full", rush));
            timer++;
            return;
        }
        if(rush.isState(GState.WAITING_FOR_PLAYERS)) {
            timer = FileManager.getConfig(rush.getID()).getInt("timers.starting");
            rush.setState(GState.STARTING);
        }

        Core.logger("compteur: " + timer + "s");
        for(Player player : rush.getPlayers()){
            player.setLevel(timer);
        }
        if(timer>=60 && timer%60==0) rush.broadcast(FileManager.prefix() + "§6Le jeu demarre dans §e" + timer/60 + "§6 minutes !");
        else if(timer%5==0 || timer == 3 || timer == 2 || timer == 1){
            rush.broadcast(FileManager.getConfigMessage("countdown", rush, timer));
            for(Player player : rush.getPlayers()){
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            }
        }

        if(timer<=0){

            Core.logger("Passage en PLAYING");
            Core.removeOfWaiting(rush);
            rush.setState(GState.PLAYING);

            Core.logger("Lancement des spawners à items");

            PlayingTimer playingTimer = new PlayingTimer(rush);
            playingTimer.runTaskTimer(Core.getPlugin(Core.class),0,20);


            List<String> teams = FileManager.getConfig(rush.getID()).getStringList("teams");
            int i = 0;
            for(Player player : rush.getPlayers()){
                Team team = rush.getTeam(teams.get(i));

                player.sendMessage(FileManager.getConfigMessage("starting", rush));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);

                Core.logger(player.getName() + " a rejoint l'équipe " + team.getDisplayName());

                team.addPlayer(player);
                rush.addAlivePlayer(player);

                rush.spawnPlayer(player);

                if(team.getPlayers().size() >= team.getSize()) i++;
            }
            cancel();
        }

        timer--;
    }
}
