package fr.rush.romain.rush;

import fr.rush.romain.rush.managers.ChatManager;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import fr.rush.romain.rush.managers.ShopManager;
import fr.rush.romain.rush.objects.Rush;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.Material.YELLOW_BED;

public class RushListener implements Listener {

    private final Core main;

    public RushListener(Core main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player p = e.getPlayer();

        if (p.hasPermission("rush.admin")) {
            p.sendMessage("Vous avez rejoint le serveur en tant que Staff, /rush join pour rejoindre une partie");
        } else GameManager.Join(p, GameManager.selectRush());


    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Player p = e.getPlayer();

        if (!isPlaying(p)) return;

        Rush rush = Core.playersRush.get(p);

        if (rush.isState(GState.PLAYING)) {
            rush.eliminatePlayer(p);
        }
        Core.playersRush.remove(p);
        rush.removePlayer(p);
        rush.broadcast(p.getName() + " a quitté la partie");
    }

    /*@EventHandler
    public void onBreak(BlockBreakEvent e){
        Player player = e.getPlayer();

        if(!(main.isState(GState.PLAYING) && main.getPlayers().contains(player)) && !player.hasPermission("rush.admin")) {
            player.sendMessage(main.getConfigMessage("no-break", player));
            e.setCancelled(true);
            return;
        }

        if(e.getBlock().getBlockData().getMaterial() == YELLOW_BED){
            PlayingListener.YellowBedBreak(player, e);

            if(e.isCancelled()) return;
            for(Player joueurs : main.getPlayers()){
                joueurs.playSound(player, Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, 1, 1);
            }
        }

        if(e.getBlock().getBlockData().getMaterial() == Material.PURPLE_BED){
            PlayingListener.PurpleBedBreak(player, e);

            if(e.isCancelled()) return;
            for(Player joueurs : main.getPlayers()){
                joueurs.playSound(joueurs, Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, 1, 1);
            }
        }

        if(e.getBlock().getBlockData().getMaterial() == Material.BROWN_BED){
            PlayingListener.BrownBedBreak(player, e);
        }
    }*/

    @EventHandler
    public void OnPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player p = e.getPlayer();

        if (p.hasPermission("rush.admin")) return;

        if (isBed(block.getType())) {
            e.setCancelled(true);
            if (isBed(p.getInventory().getItemInMainHand().getType())) {
                p.getInventory().getItemInMainHand().setAmount(0);
            } else {
                p.getInventory().getItemInOffHand().setAmount(0);
            }
        }
    }

    @EventHandler
    public void OnRecup(EntityPickupItemEvent e) {
        if (e.getEntity().hasPermission("rush.admin")) return;

        Item item = e.getItem();
        if (isBed(item.getItemStack().getType())) {
            e.setCancelled(true);
            item.getItemStack().setAmount(0);
        }
    }

    public boolean isBed(Material block) {
        return block == YELLOW_BED || block == Material.BROWN_BED || block == Material.PURPLE_BED;
    }

    @EventHandler
    public void Explosion(EntityExplodeEvent e) {
        for (Block b : e.blockList()) {
            if (b.getType() != Material.SANDSTONE) {
                e.blockList().remove(b);
            }
            if (b.getType() == YELLOW_BED || b.getType() == Material.PURPLE_BED) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if (e.isCancelled()) return;
        if (!isPlaying(p)) return;

        Rush rush = Core.playersRush.get(p);

        e.setCancelled(true);
        if (!rush.isState(GState.PLAYING)) {
            ChatManager.sendMessage(p, rush, msg, false);
        } else {
            if (rush.getAlivePlayers().contains(p)) {
                if (msg.startsWith("@")) {
                    ChatManager.sendMessage(p, rush, msg, true);
                } else {
                    ChatManager.sendTeamMessage(p, rush, msg);
                }
            } else {
                ChatManager.sendSpecMessage(p, rush, msg);
            }
        }
    }

    @EventHandler
    public void EntityInteractEvent(PlayerInteractEntityEvent e){
        Entity entity = e.getRightClicked();
        Player player = e.getPlayer();
        Rush rush = Core.playersRush.getOrDefault(player, null);

        if (rush == null || !(rush.isState(GState.PLAYING)) || !(entity.getScoreboardTags().contains("rush")))
            return;

        e.setCancelled(true);
        for(String shopID : FileManager.getConfig("shops").getStringList("shops")){
            if(entity.getScoreboardTags().contains(shopID)) player.openInventory(ShopManager.get(shopID));
        }

    }
    private boolean isPlaying(Player p) { return Core.playersRush.containsKey(p);}
}
