package me.defender.cosmetics.database.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {

    private final JavaPlugin plugin;
    public HikariDataSource dataSource;
    public SQLite(JavaPlugin plugin){
        this.plugin = plugin;
        connect();
        createTable();
    }


    public void connect(){
        if(dataSource == null){
            String jdbcUrl = "jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/playerData.db";
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername("");
            config.setPassword("");
            config.setConnectionTestQuery("SELECT 1");
            config.setConnectionTimeout(5000);
            config.setMaximumPoolSize(100);
            config.setPoolName("COSMETICS-SQLITE");
            dataSource = new HikariDataSource(config);
        }
    }

    public void createTable(){
        if(dataSource != null){
            try (Connection connection = dataSource.getConnection()) {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS cosmetics_player_data (" +
                        "uuid TEXT PRIMARY KEY," +
                        "bed_destroy TEXT," +
                        "wood_skin TEXT," +
                        "victory_dance TEXT," +
                        "shopkeeper_skin TEXT," +
                        "glyph TEXT," +
                        "spray TEXT," +
                        "projectile_trail TEXT," +
                        "kill_message TEXT," +
                        "final_kill_effect TEXT," +
                        "island_topper TEXT," +
                        "death_cry TEXT" +
                        ")");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_owned_data (" +
                        "uuid TEXT PRIMARY KEY," +
                        "bed_destroy INT," +
                        "death_cry INT," +
                        "final_kill_effect INT," +
                        "glyph INT," +
                        "island_topper INT," +
                        "kill_message INT," +
                        "projectile_trail INT," +
                        "shopkeeper_skin INT," +
                        "spray INT," +
                        "victory_dance INT," +
                        "wood_skin INT" +
                        ")");
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to create player-data table: " + e.getMessage());
            }
        }
    }


}