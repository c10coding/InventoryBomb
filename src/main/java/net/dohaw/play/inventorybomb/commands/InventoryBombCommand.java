package net.dohaw.play.inventorybomb.commands;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import me.c10coding.coreapi.chat.ChatFactory;
import me.c10coding.coreapi.helpers.MathHelper;
import net.dohaw.play.inventorybomb.InventoryBomb;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
/*
    Plugin: InventoryBomb
    For: mithzan
    Description: A plugin that drops all the person's items (minus equipped things) on the floor around a player at a certain radius
 */
public class InventoryBombCommand implements CommandExecutor {

    private InventoryBomb plugin;
    private ChatFactory chatFactory;
    private MathHelper mathHelper;
    private final String PREFIX;
    private int radius;
    private int pickupTimeout;

    public InventoryBombCommand(InventoryBomb plugin){
        this.plugin = plugin;
        this.chatFactory = plugin.getAPI().getChatFactory();
        this.PREFIX = plugin.getPrefix();
        this.mathHelper = plugin.getAPI().getMathHelper();
        this.radius = plugin.getRadius();
        this.pickupTimeout = plugin.getPickupTimeout();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args[0].equalsIgnoreCase("t") || args[0].equalsIgnoreCase("p")){
            if(args.length == 2){
                String playerName = args[1];
                if(args[0].equalsIgnoreCase("p")){
                    if(Bukkit.getPlayer(playerName) != null){
                        Player player = Bukkit.getPlayer(playerName);
                        bombInventory(player);
                    }else{
                        chatFactory.sendPlayerMessage("This is not a valid player!", true, sender, PREFIX);
                    }
                }else if(args[0].equalsIgnoreCase("t")){
                    String teamName = args[1];
                    if(Bukkit.getScoreboardManager().getMainScoreboard() != null){
                        if(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName) != null){
                            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
                            bombInventory(team);
                        }else{
                            chatFactory.sendPlayerMessage("This is not a valid team!", true, sender, PREFIX);
                        }
                    }
                }
            }
        }else if(args[0].equalsIgnoreCase("reload")){
            plugin.reloadConfig();
            this.radius = plugin.getRadius();
            this.pickupTimeout = plugin.getPickupTimeout();
            chatFactory.sendPlayerMessage("&aReloaded the config!", true, sender, PREFIX);
        }
        return false;
    }

    private void bombInventory(Player player){

        PlayerInventory playerInventory = player.getInventory();
        List<ItemStack> invItems = Arrays.asList(playerInventory.getStorageContents());
        final Location playerLocation = player.getLocation();
        List<Material> exclusions = Arrays.asList(new Material[]{Material.PLAYER_HEAD});

        for(int x = 0; x < invItems.size(); x++){
            if(playerInventory.getItem(x) != null){

                ItemStack currentItem = invItems.get(x);
                Material mat = currentItem.getType();

                if(!exclusions.contains(mat)){

                    playerInventory.setItem(x, null);
                    int randXAlter = mathHelper.getRandomInteger(radius, radius * -1);
                    int randZAlter = mathHelper.getRandomInteger(radius, radius * -1);
                    Location itemDropLocation = new Location(player.getWorld(), playerLocation.getX() + randXAlter, playerLocation.getY() + 2, playerLocation.getZ() + randZAlter);

                    itemDropLocation.getWorld().dropItem(itemDropLocation, currentItem);
                }
            }
        }

        player.getWorld().spawnParticle(Particle.END_ROD, playerLocation, 50);
        player.getWorld().playSound(playerLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        player.setCanPickupItems(false);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.setCanPickupItems(true);
        }, pickupTimeout * 20);

    }

    private void bombInventory(Team team){

        Set<OfflinePlayer> setTeamPlayers = team.getPlayers();
        List<OfflinePlayer> listOfflinePlayers = new ArrayList<>(setTeamPlayers);
        List<Player> listOnlinePlayers = new ArrayList<>();

        for(OfflinePlayer op : listOfflinePlayers){
            if(op.isOnline()){
                listOnlinePlayers.add(op.getPlayer());
            }
        }

        int randomIndex = mathHelper.getRandomInteger(listOnlinePlayers.size(), 0);
        Player randomPlayer = listOnlinePlayers.get(randomIndex);
        bombInventory(randomPlayer);
    }


}
