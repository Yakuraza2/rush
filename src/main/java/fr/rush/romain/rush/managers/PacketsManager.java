package fr.rush.romain.rush.managers;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.rush.romain.rush.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import static fr.rush.romain.rush.Core.logger;

public class PacketsManager implements PluginMessageListener {

/*Demander si un rush est WAITING,
Si oui: le stocker et le joueur est connecté au serveur et join(player rush)
Si non: Le plugin sur le lobby gèrera la Players Waiting List et a chaque game finie, un message est send au lobby pour lui faire connecter les joueurs
 */

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("CanJoin")) {
            String rushID = GameManager.selectRushID();
            if(rushID==null){
                logger("Player can't join !");
                sendPluginMessage("Rush", "No");
            }else{
                logger("Player will join " + rushID);
                sendPluginMessage("Rush", rushID);
            }
            return;
        }

        /*if(subchannel.equals("isJoining")){
            String rushID = in.readUTF();
        }*/
    }

    public void sendPluginMessage(String subChannel, String arg, Player player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(arg);

        player.sendPluginMessage(Core.getPlugin(Core.class), "BungeeCord", out.toByteArray());
    }

    public void sendPluginMessage(String subChannel, String arg){
        sendPluginMessage(subChannel, arg, Iterables.getFirst(Bukkit.getOnlinePlayers(), null));
    }

    public static void connectToServer(Player p, String serverName){

        //noms des serveurs: lobby, ou l'id du rush.

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(Core.serverList.get(serverName));

        p.sendPluginMessage(Core.getPlugin(Core.class), "BungeeCord", out.toByteArray());
    }

}
