package fr.rush.romain.rush.objects;

import fr.rush.romain.rush.managers.FileManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Zone {
    private int aX1;
    private int aZ1;
    private int aX2;
    private int aZ2;

    public Zone(String rushID, String id){
        String path = "zones." + id + ".";
        YamlConfiguration config = FileManager.getConfig(rushID);

        this.aX1 = config.getInt(path + "x1");
        this.aZ1 = config.getInt(path + "z1");
        this.aX2 = config.getInt(path + "x2");
        this.aZ2 = config.getInt(path + "z2");
    }

    public Zone(String rushID, String id, int x1, int z1, int x2, int z2){
        this.aX1 = x1;
        this.aZ1 = z1;
        this.aX2 = x2;
        this.aZ2 = z2;
    }

    public boolean isPlayerIn(Player player) {
        Location pLoc = player.getLocation();
        return pLoc.getBlockX() >= this.aX1 && pLoc.getBlockZ() >= this.aZ1 && pLoc.getBlockX() <= this.aX2 && pLoc.getBlockZ() <= this.aZ2;
    }
}
