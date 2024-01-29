package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.managers.ItemsManager;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.ScoreBoardManager;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayingTimer extends BukkitRunnable {

    private final Rush rush;
    private int timer = 1;
    private final int bronzeDelay;
    private final int ironDelay;
    private final int goldDelay;
    private final int diamondDelay;
    private final FinishTimer finishTimer;
    HashMap<Player, Integer> playerTimer = new HashMap<>();

    public PlayingTimer(Rush pRush) {
        this.rush = pRush;
        this.bronzeDelay = FileManager.getConfig(rush.getID()).getInt("timers.bronze");
        this.ironDelay = FileManager.getConfig(rush.getID()).getInt("timers.iron");
        this.goldDelay = FileManager.getConfig(rush.getID()).getInt("timers.gold");
        this.diamondDelay = FileManager.getConfig(rush.getID()).getInt("timers.diamond");
        this.finishTimer = new FinishTimer(pRush);
    }

    @Override
    public void run() {
        if(!rush.isState(GState.PLAYING)) cancel();

        if(timer%this.bronzeDelay == 0)  spawnGem(Material.COPPER_INGOT, "bronze");
        if(timer%this.ironDelay == 0)    spawnGem(Material.IRON_INGOT, "iron");
        if(timer%this.goldDelay == 0)    spawnGem(Material.GOLD_INGOT, "gold");
        if(timer%this.diamondDelay == 0) spawnGem(Material.DIAMOND, "diamond");

        /*if(timer%FileManager.getConfig(rush.getID()).getInt("timers.player-zone-verif")==0){
            zones Zones = new zones(main);
            for(Player p : rush.getPlayers()){
                if(Zones.isPlayerInZone(p)){
                    if(playerTimer.containsKey(p) && playerTimer.get(p) > 0){
                        playerTimer.put(p, 0);
                    }
                }else{

                    if(!playerTimer.containsKey(p)){
                        playerTimer.put(p, 1);
                    }else if(playerTimer.get(p) < 5){
                        playerTimer.put(p, playerTimer.get(p) + FileManager.getConfig(rush.getID()).getInt("timers.player-zone-verif"));
                        p.sendMessage("§cRevenez dans la zone de jeu !");
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1);
                    }else{
                        rush.killPlayer(p);
                        playerTimer.put(p, 0);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 0.5F, 1);
                    }

                }
            }
        }*/
        for(Player player : rush.getPlayers()){
            ScoreBoardManager.updateScoreboard(player, rush, timer);
        }
        timer++;

        if(rush.isState(GState.FINISH)){
            finishTimer.runTaskTimer(Core.getPlugin(Core.class),0,20);

            this.timer = 1;
            cancel();
        }
    }

    private void spawnGem(Material material, String name){
        for(String teamID : FileManager.getConfig(rush.getID()).getStringList("teams.list")){
            Location Loc = rush.getTeam(teamID).getItemsSpawners();
            Loc.getWorld().dropItem(Loc, ItemsManager.create(material, FileManager.getConfig().getString("shops.display-names." + name), 1));
        }
    }

}
