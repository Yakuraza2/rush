package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final List<Player> aPlayer;
    private Location aItemsSpawners;
    private final Location aSpawn;
    private int aSize;
    private final String team_id;

    private String DisplayName;

    private boolean bedStatus;
    private boolean eliminated;
    private final int[] aColor;

    public Team(String rush, String name){
        String path = rush + ".teams." + name;

        aPlayer = new ArrayList<>();
        aSpawn = FileManager.getConfig(rush).getLocation(path + ".spawn");
        aItemsSpawners = FileManager.getConfig(rush).getLocation(path + ".item-spawner");
        aSize= FileManager.getConfig(rush).getInt(path + ".slots");
        DisplayName =  FileManager.getConfig(rush).getString(path + ".display-name");
        team_id = name;

        int r = FileManager.getConfig(rush).getInt(path + ".color.red");
        int g = FileManager.getConfig(rush).getInt(path + ".color.green");
        int b = FileManager.getConfig(rush).getInt(path + ".color.blue");


        aColor = new int[]{r, g, b};

        bedStatus = true;
        eliminated = false;

    }

    public void addPlayer(final Player p) {
        this.aPlayer.add(p);
    }
    public void removePlayer(final Player p) {
        this.aPlayer.remove(p);
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
        this.eliminated = true;
    }


    public int[] getColor() {
        return aColor;
    }

    public void broadcast(String message){
        for(Player p : this.getPlayers()){
            p.sendMessage(message);
        }
    }

    public String getId() {
        return team_id;
    }
}
