package net.dohaw.play.inventorybomb;

import net.dohaw.corelib.CoreLib;
import net.dohaw.corelib.helpers.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/*
    Plugin: InventoryBomb
    For: mithzan
    Description: A plugin that drops all the person's items (minus equipped things) on the floor around a player at a certain radius
 */
public final class InventoryBombPlugin extends JavaPlugin {

    private final String PREFIX = "[&bInventoryBomb&r]";

    @Override
    public void onEnable() {

        CoreLib.setInstance(this);
        getLogger().info("CoreAPI Hooked!");

        validateFiles();
        registerCommands();
    }

    @Override
    public void onDisable() { }

    private void registerCommands(){
        Bukkit.getServer().getPluginCommand("inventorybomb").setExecutor(new InventoryBombCommand(this));
        getLogger().info("Registered commands...");
    }

    private void validateFiles(){
        File[] files = new File[]{new File(getDataFolder(), "config.yml")};
        for(File f : files){
            if(!f.exists()){
                saveResource(f.getName(), false);
            }
        }
        getLogger().info("Validated files!");
    }

    public int getRadius(){
        if(getConfig().get("Radius") != null){
            if(MathHelper.isInt(getConfig().getString("Radius"))){
                return getConfig().getInt("Radius");
            }
        }
        return 3;
    }

    public int getPickupTimeout(){
        if(getConfig().get("Pickup Timeout") != null){
            if(MathHelper.isInt(getConfig().getString("Pickup Timeout"))){
                return getConfig().getInt("Pickup Timeout");
            }
        }
        return 5;
    }

    public String getPrefix(){
        return PREFIX;
    }

}
