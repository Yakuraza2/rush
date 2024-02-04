package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.Material.YELLOW_BED;

public class Team {

    private final List<Player> aPlayer;
    private Location aItemsSpawners;
    private final Location aSpawn;
    private int aSize;
    private final String team_id;
    private Material aBedMaterial;

    private String DisplayName;
    private final String rushID;

    private boolean bedStatus;
    private boolean eliminated;
    private final int[] aColor;
    private int aHealBoost = 0;

    public Team(String rush, String name){
        String path = "teams." + name;

        aPlayer = new ArrayList<>();
        aSize= FileManager.getConfig(rush).getInt(path + ".slots");
        DisplayName =  FileManager.getConfig(rush).getString(path + ".display-name").replace("&","§");
        team_id = name;

        World world = Bukkit.getWorld(FileManager.getConfig(rush).getString(rush + ".world"));

        int x = FileManager.getConfig(rush).getInt("teams." + name + ".spawn.x");
        int y = FileManager.getConfig(rush).getInt("teams." + name + ".spawn.y");
        int z = FileManager.getConfig(rush).getInt("teams." + name + ".spawn.z");
        int yaw = FileManager.getConfig(rush).getInt("teams." + name + ".spawn.yaw");
        int pitch = FileManager.getConfig(rush).getInt("teams." + name + ".spawn.pitch");
        aSpawn = new Location(world, x, y ,z, yaw, pitch);

        int x2 = FileManager.getConfig(rush).getInt("teams." + name + ".item-spawner.x");
        int y2 = FileManager.getConfig(rush).getInt("teams." + name + ".item-spawner.y");
        int z2 = FileManager.getConfig(rush).getInt("teams." + name + ".item-spawner.z");
        aItemsSpawners = new Location(world, x2, y2, z2, 1, 1);

        int r = FileManager.getConfig(rush).getInt(path + ".color.red");
        int g = FileManager.getConfig(rush).getInt(path + ".color.green");
        int b = FileManager.getConfig(rush).getInt(path + ".color.blue");

        aBedMaterial = Material.matchMaterial(FileManager.getConfig(rush).getString("teams." + team_id + ".bed-material"));
        aColor = new int[]{r, g, b};

        rushID = rush;

        bedStatus = true;
        eliminated = false;

    }

    public void addPlayer(final Player p) {

        this.aPlayer.add(p);
        p.setPlayerListName(this.DisplayName + "§e " + p.getDisplayName() );
        p.setDisplayName(this.DisplayName + " " + p.getName() + ChatColor.RESET);
    }
    public void removePlayer(final Player p) {

        this.aPlayer.remove(p);
        p.setPlayerListName(p.getName() );
        p.setDisplayName(p.getName());
    }
    public List<Player> getPlayers() {
        return aPlayer;
    }

    public boolean contains(final Player p) { return this.aPlayer.contains(p); }

    public Location getSpawn() { return aSpawn; }

    public int getSize() {return aSize;}
    public void setSize(int pSize) { this.aSize = pSize; }

    public void setDisplayName(String displayName) { this.DisplayName = displayName; }
    public String getDisplayName() { return this.DisplayName; }

    public Location getItemsSpawners() { return aItemsSpawners; }
    public void setItemsSpawners(Location loc) { this.aItemsSpawners = loc; }

    public boolean hasBed(){ return this.bedStatus; }

    public void setBed(boolean statut) { this.bedStatus = statut;}

    public void spawnPlayer(Player p) { p.teleport(aSpawn); }

    public boolean isEliminated() { return eliminated; }
    public void eliminate() {
        Core.logger("l'équipe " + this.team_id + " de " + this.rushID + " a été éliminée !");
        this.eliminated = true;
    }


    public int[] getColor() {
        return aColor;
    }

    public String getId() {
        return team_id;
    }

    public Material getBedMaterial() { return aBedMaterial; }

    public void broadcast(String message){
        for(Player p : this.getPlayers()){
            p.sendMessage(message);
        }
    }

    public void breakBed(Player p, Rush rush){
        this.setBed(false);
        for(Player player : rush.getPlayers()){
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, 1, 1);
        }

    }

    public int getHealBoost() { return this.aHealBoost; }
    public void setHealBoost(int healBoost) { this.aHealBoost = healBoost; }
    public void addHealBoost(int healBoost) {
        this.aHealBoost += healBoost;
        this.applyHealBoost();
    }

    public void applyHealBoost() {
        for(Player player : this.getPlayers()){
            player.setMaxHealth(20 + 2*this.getHealBoost());
        }
    }

    public static void applyHealBoost(Player p, Rush rush) {
        p.setMaxHealth(20 + 2*rush.getPlayerTeam(p).getHealBoost());
    }

    public void reset(){
        this.getPlayers().clear();
        this.bedStatus = true;
        this.eliminated = false;
    }

    public void setBedMaterial(Material bed) { this.aBedMaterial = bed; }
}
