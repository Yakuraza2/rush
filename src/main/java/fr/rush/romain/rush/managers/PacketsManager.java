package fr.rush.romain.rush.managers;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.rush.romain.rush.Core;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;

import static fr.rush.romain.rush.Core.*;

public class PacketsManager implements PluginMessageListener {

/*Demander si un rush est WAITING,
Si oui: le stocker et le joueur est connecté au serveur et join(player rush)
Si non: Le plugin sur le lobby gèrera la Players Waiting List et a chaque game finie, un message est send au lobby pour lui faire connecter les joueurs
 */

    public static HashMap<Player, Rush> comingPlayer = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        logger("Packets received");
    }

    public static void sendPluginMessage(String subChannel, String arg, Player player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(serverName);
        out.writeUTF(arg);

        player.sendPluginMessage(Core.getPlugin(Core.class), "romain:rush", out.toByteArray());
    }

    public static void sendPluginMessage(String subChannel, String arg){
        if(Bukkit.getOnlinePlayers().isEmpty()) return;
        sendPluginMessage(subChannel, arg, Iterables.getFirst(Bukkit.getOnlinePlayers(), null));
    }

    public static void connectToServer(Player p, String serverName){

        //noms des serveurs: lobby, ou l'id du rush.

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);

        p.sendPluginMessage(Core.getPlugin(Core.class), "BungeeCord", out.toByteArray());
    }

    public static void sendSlotsToProxy(){
        int slots = 0;
        for(Rush rush : Core.waitingList){
            slots += rush.getSlots() - rush.getPlayers().size();
        }
        sendPluginMessage("Slots", "" + slots);
    }

}
