package fr.rush.romain.rush;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final List<Player> aPlayer;
    private final Location aSpawn;
    private int aSize;

    private String DisplayName;

    public Team(String rush, String name){
        String path = rush + ".teams." + name;

        aPlayer = new ArrayList<>();
        aSpawn = new Location(Bukkit.getWorld(FileManager.getConfig(rush).getString(path+".name")),
                FileManager.getConfig(rush).getDouble(path + ".x"), FileManager.getConfig(rush).getDouble(path + ".y"),FileManager.getConfig(rush).getDouble(path + ".z"),
                (float) FileManager.getConfig(rush).getDouble(path + ".yaw"), (float) FileManager.getConfig(rush).getDouble(path + ".pitch"));
        aSize= FileManager.getConfig(rush).getInt(path + ".slots");
        DisplayName =  FileManager.getConfig(rush).getString(path + ".display-name");

    }

    public void addPlayer(final Player p) {
        this.aPlayer.add(p);
    }
    public List<Player> getPlayer() {
        return aPlayer;
    }

    public boolean contains(final Player p) { return this.aPlayer.contains(p); }

    public Location getSpawn() { return aSpawn; }

    public int getSize() {return aSize;}
    public void setSize(int pSize) { this.aSize = pSize; }

    public void setDisplayName(String displayName) { this.DisplayName = displayName; }
    public String getDisplayName() { return this.DisplayName; }
}
