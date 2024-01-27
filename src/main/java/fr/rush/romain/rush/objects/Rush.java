package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.timers.AutoStart;
import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Rush {
    private final String aRush_id;
    private List<Player> aPlayers;
    private List<Player> aAlivePlayers;
    private final Location aLobby;
    private final Location aSpectSpawn;
    private final HashMap<String, Team> aTeams;
    private final HashMap<Player, Team> playerTeam;
    private final int aSlots;
    private GState aState;

    private final AutoStart autoStart;

    public Rush(String rush_id){
        AtomicInteger vSlots = new AtomicInteger();
        aState = GState.WAITING_FOR_PLAYERS;
        aTeams = new HashMap<>();
        playerTeam = new HashMap<>();

        for(String team : FileManager.getConfig(rush_id).getStringList("teams")){
            this.addTeam(rush_id, team);
        }

        this.aTeams.forEach((id, team) -> {
            vSlots.addAndGet(team.getSize());
        });
        aSlots = vSlots.get();

        aRush_id = rush_id;
        aLobby = FileManager.getConfig(rush_id).getLocation(rush_id + ".lobby");
        aSpectSpawn = FileManager.getConfig(rush_id).getLocation(rush_id + ".spectator-spawn");
        autoStart = new AutoStart(this);
    }

    public boolean isState(GState state) { return this.aState == state; }
    public void setState(GState state){ this.aState = state; }

    public List<Player> getPlayers() {return aPlayers;}
    public void addPlayer(Player p) { this.aPlayers.add(p); }
    public void removePlayer(Player p) { this.aPlayers.remove(p); }

    public List<Player> getAlivePlayers() {return aAlivePlayers;}
    public void addAlivePlayer(Player p) { this.aAlivePlayers.add(p); }

    public Team getTeam(String name) {
        if(aTeams.isEmpty()) { Core.logger(2, "Aucune team pour la partie"); }
        return aTeams.getOrDefault(name, null);
    }

    public void addTeam(String rush, String teamName){ this.aTeams.put(teamName, new Team(rush, teamName)); }
    public Team getPlayerTeam(Player p) { return this.playerTeam.get(p); }

    public Location getLobby() { return this.aLobby; }

    public int getSlots() { return aSlots; }
    public String getID() { return aRush_id; }
    public AutoStart getAutoStart() { return autoStart; }



    public void reset(){

    }

    public void broadcast(String message){
        for(Player p : this.getPlayers()){
            p.sendMessage(message);
        }
    }

    public void spawnPlayer(Player player){
        if(player.getGameMode() != GameMode.SURVIVAL) player.setGameMode(GameMode.SURVIVAL);

        Core.logger("Spawn de " + player.getName() + "...");
        player.setHealth(20);
        player.setMaxHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();

        //tablist tabList = new tablist(main);
        //tabList.present(player);

        if(this.isState(GState.PLAYING)){
            GameManager.putHealBoost(player);

            getPlayerTeam(player).spawnPlayer(player);

            GameManager.giveSpawnKit(player);
        } else if(this.isState(GState.FINISH)){
            spawnSpectator(player);
        }else{
            player.teleport(this.aLobby);
            Core.logger("PLAYER TELEPORTED LOBBY");
        }

    }

    public void spawnSpectator(Player player){
        Core.logger(player.getName() + "a spawn en spectator");
        player.setGameMode(GameMode.SPECTATOR);

        //tablist tabList = new tablist(main);
        //tabList.setplayer(player, 's');

        player.teleport(this.aSpectSpawn);
        Core.logger("PLAYER TELEPORTED SPECTATOR");
    }

    public void killPlayer(Player player){

        Core.logger("Le joueur " + player.getName() + " est mort.");
        //main.addDeath(player);


    }

    public void eliminatePlayer(Player player){
        getAlivePlayers().remove(player);
        Team playerTeam = this.getPlayerTeam(player);
        playerTeam.removePlayer(player);

        if(playerTeam.getPlayers().isEmpty()){
            playerTeam.eliminate();
        }

        this.broadcast(player.getName() + " a été éliminé !");

        spawnSpectator(player);

        checkWin();
    }

    private void checkWin() {
        int alives = 0;
        Team winners = null;

        for(String team : FileManager.getConfig(this.aRush_id).getStringList("teams")){
            if(!getTeam(team).isEliminated()) {
                alives += 1;
                winners = getTeam(team);
            }
            if(alives >= 2) return;
        }

        //S'il reste moins de 2 équipes en vie alors on a notre équipe 'winners' gagnante !
        if(winners == null){
            Core.logger("Il semblerait n'y avoir aucun gagnant...");
            this.broadcast(FileManager.getConfigMessage("no-winner", this));
            return;
        }

        this.broadcast(FileManager.getConfigMessage("winning-broadcast", this).replaceAll("<team>", winners.getDisplayName()));

    }
}
