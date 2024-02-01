package fr.rush.romain.rush.timers;

import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.rush.romain.rush.managers.PacketsManager.connectToServer;
import static fr.rush.romain.rush.managers.PacketsManager.sendSlotsToProxy;

public class Finish{

    private int timer;
    private final int staticTimer;
    private final Rush rush;

    private final List<Player> EndingPlayers = new ArrayList<>();
    public Finish(Rush pRush) {
        this.rush = pRush;
        this.staticTimer = FileManager.getConfig(rush.getID()).getInt("timers.finish-waiting");
        this.timer = this.staticTimer;
    }

    public void FinishMethod() {

        if(!rush.isState(GState.FINISH)) {
            Core.logger(4, "The finish timer was started during the rush " + rush.getID() + " was not in Finish State !");
        }

        if(this.timer == this.staticTimer) {
            for (Player winner : rush.getAlivePlayers()) {
                winner.sendMessage(FileManager.getConfigMessage("winners-message", winner, rush));
            }
            EndingPlayers.clear();
        }

        if(this.timer==this.staticTimer/2){
            for(Player winner : rush.getAlivePlayers()){
                winner.setGameMode(GameMode.SPECTATOR);
            }
            EndingPlayers.addAll(rush.getPlayers());
            rush.broadcast(FileManager.getConfigMessage("last-seconds", rush));
        }

        if(this.timer <=0){
            rush.reset();
            sendSlotsToProxy();
            this.timer = this.staticTimer;

            for(Player player : EndingPlayers){
                player.sendMessage("§6Retour sur le serveur lobby...");
                connectToServer(player, Core.lobby);
            }
        }
        this.timer--;
    }
}
