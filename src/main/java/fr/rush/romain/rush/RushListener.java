package fr.rush.romain.rush;

import fr.rush.romain.rush.managers.ChatManager;
import fr.rush.romain.rush.managers.FileManager;
import fr.rush.romain.rush.managers.GameManager;
import fr.rush.romain.rush.managers.PacketsManager;
import fr.rush.romain.rush.objects.*;
import fr.rush.romain.rush.timers.GState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ArmorMeta;

import static fr.rush.romain.rush.Actions.PLACE;
import static fr.rush.romain.rush.Core.allowedBlocks;
import static fr.rush.romain.rush.Core.playersRush;
import static fr.rush.romain.rush.managers.PacketsManager.connectToServer;

public class RushListener implements Listener {

    private final Core main;

    public RushListener(Core main) {
        this.main = main;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();

        if (player.hasPermission("rush.admin")) {
            player.sendMessage("Vous avez rejoint le serveur en tant que Staff, /rush join pour rejoindre une partie");
            return;
        }
        if(PacketsManager.comingPlayer.containsKey(player)){
            GameManager.join(player, PacketsManager.comingPlayer.get(player));
        } else {
            Rush rush = GameManager.selectRush();
            if(rush == null) {
                connectToServer(player, "lobby");
                return;
            }
            GameManager.join(player, rush);
        }


    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");
        Player p = e.getPlayer();
        GameManager.resetPlayer(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e){
        Player player = e.getPlayer();

        if(!Core.playersRush.containsKey(player)) return;

        Rush rush = Core.playersRush.get(player);
        Team playerTeam = rush.getPlayerTeam(player);
        Block block = e.getBlock();
        Material brokenBlockMaterial = block.getType();

        boolean bed = false;

        // Prevent players from grief the map if they are in lobby or if the game is finish
        if(!rush.isState(GState.PLAYING)) {
            player.sendMessage(FileManager.getConfigMessage("no-break", rush));
            e.setCancelled(true);
            return;
        }

        if(isBed(brokenBlockMaterial)) {
            e.setCancelled(true);
            if(brokenBlockMaterial.equals(Material.BROWN_BED)) {
                GameManager.BrownBedBreak(player, rush, playerTeam);
                bed = true;
            }else{
                //else, go found the team's destroyed bed
                for(Team team : rush.getTeams().values()){
                    if(brokenBlockMaterial.equals(team.getBedMaterial())) {
                        if (team.equals(playerTeam)) {
                            player.sendMessage(FileManager.getConfigMessage("ally-bed-destroy", rush));
                            return;
                        }
                        team.breakBed(player, rush);
                        bed = true;
                    }
                }
            }
            BedBlock bedBlock = new BedBlock(block);
            bedBlock.remove();
            rush.addBedDestroy(bedBlock);
        }

        if(!allowedBlocks.contains(brokenBlockMaterial) && !bed){
            e.setCancelled(true);
            return;
        }

        playersRush.get(player).addBlockDestroy(block.getLocation(), brokenBlockMaterial);


    }

    @EventHandler
    public void OnPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player p = e.getPlayer();

        if(!playersRush.containsKey(p)) return;

        if (isBed(block.getType())) {
            e.setCancelled(true);
            if (isBed(p.getInventory().getItemInMainHand().getType())) {
                p.getInventory().getItemInMainHand().setAmount(0);
            } else {
                p.getInventory().getItemInOffHand().setAmount(0);
            }
            return;
        }

        if(!allowedBlocks.contains(block.getType())){
            e.setCancelled(true);
            return;
        }

        playersRush.get(p).addBlockChange(PLACE, block);

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

    public static boolean isBed(Material block) {
        return block.name().toLowerCase().contains("bed");
    }

    @EventHandler
    public void Explosion(EntityExplodeEvent e) {
        e.blockList().removeIf(block -> block.getType() != Material.SANDSTONE);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = e.getMessage();

        if (e.isCancelled()) return;
        if (!GameManager.isPlaying(p)) return;

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

        if (rush == null || !(rush.isState(GState.PLAYING)) || !(entity.getScoreboardTags().contains("shop")))
            return;

        e.setCancelled(true);
        for(String shopID : FileManager.getConfig("shops").getStringList("shops.list")){
            if(entity.getScoreboardTags().contains(shopID)) { Shop.openShop(player, shopID); break; }
        }
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e){
        String displayName = e.getView().getTitle();
        Shop shop = Shop.getFromDisplayName(displayName);
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if(e.getCurrentItem() == null) return;

        if(e.getCurrentItem().getItemMeta() instanceof ArmorMeta) e.setCancelled(true);

        if(shop == null) { Core.logger("Shop is null"); return; }

        e.setCancelled(true);
        ShopItem shopItem = shop.getItem(slot);

        if(shopItem == null) { Core.logger("shopItem is null"); return; }

        shopItem.buy(player);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e instanceof EntityDamageByEntityEvent) return;
        if(e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            Core.logger("Damage détéctés sur " + player.getName());

            Rush rush = Core.playersRush.getOrDefault(player, null);
            if(rush == null) return;

            if(!rush.isState(GState.PLAYING) && rush.getPlayers().contains(player)){
                e.setCancelled(true);
                return;
            }

            if(player.getHealth()<=e.getDamage()){
                Core.logger("Mort de " + player.getName() + " d'origine inconnue");
                e.setDamage(0);
                rush.broadcast(FileManager.getConfigMessage("death", player, rush));

                rush.killPlayer(player);
            }
        }
    }
    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e){
        Entity damaged = e.getEntity();
        if(damaged instanceof Player) {
            Player victim = (Player) damaged;


            Rush rush = Core.playersRush.getOrDefault(victim, null);

            if(rush == null) return;

            Entity damager = e.getDamager();
            Player killer = victim;

            Core.logger("PVP détéctés sur " + victim.getName());

            if (victim.getHealth() <= e.getDamage()) {

                Core.logger("Mort par PVP détéctés sur " + victim.getName());
                if (damager instanceof Player) {
                    killer = (Player) damager;
                }
                if (damager instanceof Arrow) {
                    Arrow arrow = (Arrow) damager;
                    if (arrow.getShooter() instanceof Player) {
                        killer = ((Player) arrow.getShooter());
                    }
                }
                if(!(rush.isState(GState.PLAYING) && rush.getAlivePlayers().contains(killer))){
                    e.setCancelled(true);
                    killer.sendMessage(FileManager.getConfigMessage("no-pvp", killer, rush));
                    return;
                }

                Core.logger("Le tueur de " + victim.getName() + " est : " + killer.getName());

                killer.sendMessage(FileManager.getConfigMessage("killer-message", victim, rush));
                killer.playSound(killer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 2, 1);
                rush.broadcast(FileManager.getConfigMessage("killed-by-player", victim, rush).replaceAll("<killer>", killer.getName()));
                e.setDamage(0);
                rush.killPlayer(victim);
                rush.addKills(killer, 1);
            }
        } else if (e.getEntity() instanceof Villager) {
            e.setCancelled(true);
            Entity entity = e.getEntity();
            Player player = (Player) e.getDamager();
            Rush rush = Core.playersRush.getOrDefault(player, null);

            if (rush == null || !(rush.isState(GState.PLAYING)) || !(entity.getScoreboardTags().contains("shop")))
                return;

            e.setCancelled(true);
            for (String shopID : FileManager.getConfig("shops").getStringList("shops")) {
                if (entity.getScoreboardTags().contains(shopID)) { Shop.openShop(player, shopID); break; }
            }
        }
    }
}
