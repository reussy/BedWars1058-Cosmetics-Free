 

package me.defender.cosmetics;

import com.hakan.core.HCore;
import com.zaxxer.hikari.HikariDataSource;
import me.defender.cosmetics.api.BwcAPI;
import me.defender.cosmetics.api.categories.victorydances.VictoryDance;
import me.defender.cosmetics.api.utils.StartupUtils;
import me.defender.cosmetics.api.configuration.ConfigUtils;
import me.defender.cosmetics.api.utils.MainMenuUtils;
import me.defender.cosmetics.api.configuration.DefaultsUtils;
import me.defender.cosmetics.api.utils.Utility;
import me.defender.cosmetics.command.MainCommand;
import me.defender.cosmetics.config.*;
import me.defender.cosmetics.database.PlayerData;
import me.defender.cosmetics.database.PlayerOwnedData;
import me.defender.cosmetics.database.mysql.MySQL;
import me.defender.cosmetics.database.sqlite.SQLite;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLException;
import java.util.HashMap;

public class Cosmetics extends JavaPlugin
{
    public MainMenuData menuData;
    public static HikariDataSource db;

    public boolean forcedDisable = false;

    @Override
    public void onEnable() {
        if(!StartupUtils.checkDependencies()){
            getServer().getPluginManager().disablePlugin(this);
            getLogger().severe("Cosmetics addon will now disable, make sure you have all dependencies installed!");
            forcedDisable = true;
            return;
        }

        getLogger().info("All dependencies found, continuing with plugin startup.");
        HCore.initialize(this);
        // Download Glyphs
        StartupUtils.downloadGlyphs();
        ConfigUtils.getBedDestroys().save();
        ConfigUtils.getDeathCries().save();
        ConfigUtils.getFinalKillEffects().save();
        ConfigUtils.getGlyphs().save();
        ConfigUtils.getIslandToppers().save();
        ConfigUtils.getKillMessages().save();
        ConfigUtils.getProjectileTrails().save();
        ConfigUtils.getShopKeeperSkins().save();
        ConfigUtils.getSprays().save();
        ConfigUtils.getVictoryDances().save();
        ConfigUtils.getWoodSkins().save();
        ConfigUtils.getMainConfig().save();
        this.menuData = new MainMenuData(this);

        getLogger().info("Configuration file successfully loaded.");
        if(new BwcAPI().isMySQL()){
            getLogger().info("Loading MySQL database..");
            db = new MySQL(this).dataSource;
        }else{
            getLogger().info("Loading SQLite database..");
            db = new SQLite(this).dataSource;
        }

        // Load all the list
        Utility.playerDataList = new HashMap<>();
        Utility.playerOwnedDataList = new HashMap<>();
        StartupUtils.loadLists();
        getLogger().info("Cosmetics list successfully loaded.");

        getLogger().info("Saving data to configs...");
        MainMenuUtils.saveLores();
        StartupUtils.updateConfigs();
        getLogger().info("Creating folders...");
        StartupUtils.createFolders();
        getLogger().info("Registering event listeners...");
        StartupUtils.registerEvents();
        getLogger().info("Registering command to HCore...");
        HCore.registerCommands(new MainCommand());
        getLogger().info("Loading data from resources in jar...");
        DefaultsUtils defaultsUtils = new DefaultsUtils();
        defaultsUtils.saveAllDefaults();
        getLogger().info("Loading cosmetics...");
        StartupUtils.loadCosmetics();
        getLogger().info("Addon have been loaded and enabled!");
        VictoryDance.getDefault(null);
    }

    
    public void onDisable() {
        if(forcedDisable){
            getLogger().severe("Detected forced disable! plugin will not unload anything!");
            return;
        }
        if(!new BwcAPI().isMySQL()){
            getLogger().info("Saving player data to SQLite database...");
            getLogger().info("Please wait it may take some time!");
            for(PlayerData playerData : Utility.playerDataList.values()){
                playerData.save();
            }
            for (PlayerOwnedData playerOwnedData : Utility.playerOwnedDataList.values()){
                playerOwnedData.save();
            }
            getLogger().info("Player data saved to SQLite database!");
        }
    	try {
			db.getConnection().close();
		} catch (SQLException e) {
			getLogger().severe("There was an error while closing connection to database: " + e.getMessage());
		}


    }

    public static HikariDataSource getDB(){
        return db;
    }

    public static void downloadFile(URL url, String filePath) {
        try {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


