package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.managers.InventoryManager;
import fr.rush.romain.rush.timers.AutoStart;
import fr.rush.romain.rush.GState;
import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static fr.rush.romain.rush.Core.logger;

public class Rush {
    private final String aRush_id;
    private final List<Player> aPlayers = new ArrayList<>();
    private final List<Player> aAlivePlayers = new ArrayList<>();
    private final Location aLobby;
    private final Location aSpectSpawn;
    private final HashMap<String, Team> aTeams;
    private final HashMap<Player, Team> playerTeam;
    private int aSlots;
    private GState aState;
    private final AutoStart autoStart;
    private final HashMap<Player, Integer> playerKills = new HashMap<>();
    private final HashMap<Player, Integer> playerDeaths = new HashMap<>();
    private final List<Zone> aZoneList = new ArrayList<>();

    public Rush(String rush_id){
        aSlots = 0;
        aState = GState.WAITING_FOR_PLAYERS;
        aTeams = new HashMap<>();
        playerTeam = new HashMap<>();

        for(String team : FileManager.getConfig(rush_id).getStringList("teams.list")){
            this.addTeam(rush_id, team);
            aSlots += aTeams.get(team).getSize();
        }

        YamlConfiguration config = FileManager.getConfig(rush_id);
        for(int i=0; i<=16; i++){
            logger("Searching for shops." + i);
            ConfigurationSection section = config.getConfigurationSection("zones." + i);

            if(section != null) break;
            Zone zone = new Zone(rush_id, ""+i);
            aZoneList.add(zone);
        }

        aRush_id = rush_id;

        World world = Bukkit.getWorld(config.getString(rush_id + ".world"));

        int x = config.getInt(rush_id + ".lobby.x");
        int y = config.getInt(rush_id + ".lobby.y");
        int z = config.getInt(rush_id + ".lobby.z");
        int yaw = config.getInt(rush_id + ".lobby.yaw");
        int pitch = config.getInt(rush_id + ".lobby.pitch");
        aLobby = new Location(world, x, y ,z, yaw, pitch);

        int x2 = config.getInt(rush_id + ".spectator-spawn.x");
        int y2 = config.getInt(rush_id + ".spectator-spawn.y");
        int z2 = config.getInt(rush_id + ".spectator-spawn.z");
        int yaw2 = config.getInt(rush_id + ".spectator-spawn.yaw");
        int pitch2 = config.getInt(rush_id + ".spectator-spawn.pitch");
        aSpectSpawn = new Location(world, x2, y2, z2, yaw2, pitch2);
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
    public HashMap<String, Team> getTeams(){ return this.aTeams; }
    public Team getPlayerTeam(Player p) { return this.playerTeam.get(p); }
    public Team setPlayerTeam(Player p, Team team) { return this.playerTeam.put(p, team); }

    public Location getLobby() { return this.aLobby; }

    public int getSlots() { return aSlots; }
    public String getID() { return aRush_id; }
    public AutoStart getAutoStart() { return autoStart; }

    public List<Zone> getZones() { return this.aZoneList; }



    public void reset(){
        Core.logger(1, "RESET du rush " + this.aRush_id + "...");
        this.playerDeaths.clear();
        this.playerKills.clear();
        this.playerTeam.clear();

        for(Player player : this.getPlayers()) GameManager.resetPlayer(player);
        for(Team team : this.aTeams.values()) team.reset();

        this.setState(GState.WAITING_FOR_PLAYERS);
        Core.addToWaiting(this);
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
            Team.applyHealBoost(player, this);

            getPlayerTeam(player).spawnPlayer(player);

            InventoryManager.giveSpawnKit(player);
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
        Team playerTeam = this.getPlayerTeam(player);

        Core.logger("Le joueur " + player.getName() + " est mort.");
        this.playerDeaths.put(player, this.getDeaths(player) + 1);

        if(playerTeam.hasBed()) spawnPlayer(player);
        else eliminatePlayer(player, playerTeam);
    }

    public void eliminatePlayer(Player player, Team playerTeam){
        getAlivePlayers().remove(player);
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

        for(String team : FileManager.getConfig(this.aRush_id).getStringList("teams.list")){
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
        this.setState(GState.FINISH);

    }

    public int getKills(Player p) { return this.playerKills.getOrDefault(p, 0); }

    public int getDeaths(Player p) { return this.playerDeaths.getOrDefault(p, 0); }

    public void addKills(Player p, int kills) { playerKills.put(p, kills + this.getKills(p)); }

    public boolean isPlayerInZone(Player player){
        for(Zone zone : this.getZones()){
            if(!zone.isPlayerIn(player)) return false;
        }
        return true;
    }
}
