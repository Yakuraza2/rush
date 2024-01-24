package fr.rush.romain.rush;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Rush {

    private List<Player> aPlayers;
    private Location aLobby;

    private final HashMap<String, Team> Teams;

    private final int aSlots;

    private GState aState;
    public Rush(String name){
        AtomicInteger vSlots = new AtomicInteger();
        aState = GState.WAITING_FOR_PLAYERS;
        Teams = new HashMap<>();

        FileManager.getConfig(name).getStringList("teams");

        this.Teams.forEach((id, team) -> {
            vSlots.addAndGet(team.getSize());
        });
        aSlots = vSlots.get();
    }

    public GState getaState() { return this.aState; }
    public void setState(GState state){ this.aState = state; }

    public List<Player> getPlayers() {return aPlayers;}
    public void addPlayer(Player p) { this.aPlayers.add(p); }

    public Team getTeam(String name) {
        if(Teams.isEmpty()) { Main.logger(2, "Aucune team pour la partie"); }
        return Teams.get(name);
    }

    public void addTeam(String rush, String name){ this.Teams.put(name, new Team(rush, name)); }

    public boolean isWaiting(){ return this.aState==GState.WAITING_FOR_PLAYERS; }

    public Location getLobby() { return this.aLobby; }

    public int getSlots() { return aSlots; }
}
