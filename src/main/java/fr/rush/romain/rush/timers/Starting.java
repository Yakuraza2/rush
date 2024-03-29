package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Starting{

    private int timer = 0;
    private final Rush rush;

    public Starting(Rush pRush){
        rush = pRush;
    }

    public void StartingMethod(){
        if(rush.getPlayers().isEmpty()) return;

        if(rush.getPlayers().size() < rush.getSlots()){
            if(timer > 15) timer=0;
            if(!rush.isState(GState.WAITING_FOR_PLAYERS)) rush.setState(GState.WAITING_FOR_PLAYERS);
            if(timer==0) rush.broadcast(FileManager.getConfigMessage("slots-not-full", rush));
            timer++;
            return;
        }
        if(rush.isState(GState.WAITING_FOR_PLAYERS)) {
            timer = FileManager.getConfig(rush.getID()).getInt("timers.lobby-waiting");
            rush.setState(GState.STARTING);
        }

        for(Player player : rush.getPlayers()){
            player.setLevel(timer);
        }
        if(timer>=60 && timer%60==0){
            rush.broadcast(FileManager.prefix() + "§6Le jeu demarre dans §e" + timer/60 + "§6 minutes !");
            Core.logger("timer for " + rush.getID() + " : " + timer + "s");
        } else if(timer%5==0 || timer == 3 || timer == 2 || timer == 1){
            rush.broadcast(FileManager.getConfigMessage("countdown", rush, timer));
            Core.logger("timer for " + rush.getID() + " : " + timer + "s");
            for(Player player : rush.getPlayers()){
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            }
        }

        if(timer<=0){

            Core.logger("Passage en PLAYING");
            Core.removeOfWaiting(rush);
            rush.setState(GState.PLAYING);

            Collection<Team> teams = rush.getTeams().values();
            for(Player player : rush.getPlayers()){
                Core.logger("Liste des teams: " + teams);
                for(Team team : teams){
                    if(!(team.getPlayers().size() >= team.getSize())) {
                        player.sendMessage(FileManager.getConfigMessage("starting", rush));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 2);

                        Core.logger(player.getName() + " a rejoint l'équipe " + team.getDisplayName());

                        rush.setPlayerTeam(player, team);
                        team.addPlayer(player);
                        rush.addAlivePlayer(player);

                        rush.spawnPlayer(player);
                        break;
                    }
                    Core.logger(team.getId() + " is full !");
                }

            }
        }

        timer--;

    }
}
