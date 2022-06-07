package se.fusion1013.plugin.cobaltserver.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.player.IPlayerDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.Database;
import se.fusion1013.plugin.cobaltcore.database.system.SQLite;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.manager.DiscordManager;
import se.fusion1013.plugin.cobaltserver.warp.Warp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public class DatabaseHook {

    // ----- VARIABLES -----

    private static final Database database = DataManager.getInstance().getSqliteDb();

    // ----- TABLES -----

    public static String SQLiteCreateWarpsTable = "CREATE TABLE IF NOT EXISTS warps (" +
            "`id` INTEGER NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "`owner_uuid` varchar(36) NOT NULL," +
            "`owner_name` varchar(32) NOT NULL," +
            "`location_uuid` varchar(36) NOT NULL," +
            "`privacy` varchar(32) NOT NULL," +
            "PRIMARY KEY (`name`, `owner_uuid`)," +
            "FOREIGN KEY(location_uuid) REFERENCES locations(uuid) ON DELETE CASCADE," +
            "CHECK (privacy in ('public','private'))" +
            ");";

    public static String SQLiteCreateDiscordChannelsTable = "CREATE TABLE IF NOT EXISTS discord_channels (" +
            "`channel_key` varchar(32) NOT NULL," +
            "`option` varchar(32) NOT NULL," +
            "PRIMARY KEY (`channel_key`,`option`)," +
            "CHECK (option in ('GLOBAL','STATUS','AWARDS','DEATHS','JOIN','LEAVE','PLAYERLIST'))" +
            ");";

    public static String SQLiteCreatePlayerJoinTimesTable = "CREATE TABLE IF NOT EXISTS player_join_times (" +
            "`uuid` varchar(36) NOT NULL," +
            "`last_joined` datetime," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    public static String SQLiteCreatePlayerLeaveTimesTable = "CREATE TABLE IF NOT EXISTS player_leave_times (" +
            "`uuid` varchar(36) NOT NULL," +
            "`last_left` datetime NOT NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    public static String SQLiteCreateNicknameTable = "CREATE TABLE IF NOT EXISTS player_nicknames (" +
            "`uuid` varchar(36) NOT NULL," +
            "`nickname` varchar(32) NOT NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    public static String SQLiteCreateTagTable = "CREATE TABLE IF NOT EXISTS tag_stats (" +
            "`player_uuid` varchar(36) NOT NULL," +
            "`tag_count` INTEGER NOT NULL," +
            "PRIMARY KEY (`player_uuid`)" +
            ");";

    // ----- VIEWS -----

    public static String SQLiteCreateWarpInformationView = "CREATE VIEW IF NOT EXISTS warp_information AS" +
            " SELECT warps.name, warps.owner_uuid, warps.owner_name, warps.location_uuid, warps.privacy, locations.world, locations.x_pos, locations.y_pos, locations.z_pos, locations.yaw, locations.pitch" +
            " FROM warps" +
            " INNER JOIN locations ON locations.uuid = warps.location_uuid;";

    public static String SQLiteCreatePlayerTimesView = "CREATE VIEW IF NOT EXISTS player_information AS" +
            " SELECT players.uuid, players.name, player_join_times.last_joined, player_leave_times.last_left, locations.world, locations.x_pos, locations.y_pos, locations.z_pos, locations.yaw, locations.pitch" +
            " FROM players" +
            " INNER JOIN player_join_times ON player_join_times.uuid = players.uuid" +
            " INNER JOIN player_leave_times ON player_leave_times.uuid = players.uuid" +
            " INNER JOIN locations ON locations.uuid = players.uuid;";

    public static void instantiateTables() {
        // database.executeString(SQLiteCreateWarpsTable);
        database.executeString(SQLiteCreateDiscordChannelsTable);
        database.executeString(SQLiteCreatePlayerJoinTimesTable);
        database.executeString(SQLiteCreatePlayerLeaveTimesTable);
        database.executeString(SQLiteCreateNicknameTable);
        database.executeString(SQLiteCreateTagTable);

        database.executeString(SQLiteCreatePlayerTimesView);
        // database.executeString(SQLiteCreateWarpInformationView);
    }

    // ----- TAG GAME -----

    public static List<UUID> getTagLeaderboard() {
        List<UUID> leaderboard = new ArrayList<>();

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tag_stats ORDER BY tag_count DESC;");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                leaderboard.add(uuid);
            }

            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return leaderboard;
    }

    public static Map<UUID, Integer> getTagStats() {
        Map<UUID, Integer> tagStats = new HashMap<>();

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tag_stats");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                int count = rs.getInt("tag_count");
                tagStats.put(uuid, count);
            }
            ps.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return tagStats;
    }

    public static void insertTagStats(Map<UUID, Integer> tagStats) {
        try {
            Connection conn = database.getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO tag_stats(player_uuid, tag_count) VALUES(?,?)");
            for (UUID uuid : tagStats.keySet()) {
                ps.setString(1, uuid.toString());
                ps.setInt(2, tagStats.get(uuid));
                ps.executeUpdate();
            }
            conn.commit(); // TODO: DO THIS FOR ALL REQUESTS
            ps.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- NICKNAMES -----

    public static Map<UUID, String> getPlayerNicknames() {
        Map<UUID, String> nicknameMap = new HashMap<>();

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_nicknames");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String nickname = rs.getString("nickname");
                nicknameMap.put(uuid, nickname);
            }
            ps.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return nicknameMap;
    }

    public static int updatePlayerNickname(UUID uuid, String nickname) {
        int rowsInserted = 0;

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_nicknames(uuid, nickname) VALUES(?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, nickname);
            rowsInserted = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rowsInserted;
    }

    // ----- DISCORD -----

    public static int removeDiscordChannel(String channelKey, DiscordManager.ChannelOption option) {
        int rowsUpdated = 0;

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM discord_channels WHERE channel_key = ? AND option = ?");
            ps.setString(1, channelKey);
            ps.setString(2, option.name());
            rowsUpdated = ps.executeUpdate();
            ps.close();

        } catch (SQLException ex) {
            CobaltServer.getInstance().getLogger().log(Level.FINE, "SQLException when inserting into database: ", ex);
        }

        return rowsUpdated;
    }

    /**
     * Returns an array of discord channel keys that subscribe to the given option.
     *
     * @param option the option to search for.
     * @return an array of channel keys.
     */
    public static String[] getDiscordChannelKeys(DiscordManager.ChannelOption option) {
        List<String> discordChannelList = new ArrayList<>();

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM discord_channels WHERE option = ?");
            ps.setString(1, option.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String channelKey = rs.getString("channel_key");
                discordChannelList.add(channelKey);
            }
            ps.close();

        } catch (SQLException ex) {
            CobaltServer.getInstance().getLogger().log(Level.FINE, "SQLException when inserting into database: ", ex);
        }

        return discordChannelList.toArray(new String[0]);
    }

    /**
     * Inserts a discord channel into the database.
     *
     * @param channelKey the key of the channel to insert.
     * @param option the option that the channel is subscribed to.
     */
    public static int insertDiscordChannel(String channelKey, DiscordManager.ChannelOption option) {
        int rowsInserted = 0;

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO discord_channels(channel_key, option) VALUES(?, ?)");
            ps.setString(1, channelKey);
            ps.setString(2, option.name());

            rowsInserted = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            CobaltServer.getInstance().getLogger().log(Level.FINE, "SQLException when inserting into database: ", ex);
        }

        CobaltServer.getInstance().getLogger().info("Inserted new discord channel '" + channelKey + "' into database with option " + option + ". " + rowsInserted + " rows inserted");
        return rowsInserted;
    }

    // ----- PLAYERS -----

    public static PlayerUtil.PlayerStorage getPlayerStorage(String playerName) {
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM player_information WHERE name = ?");
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("x_pos");
                double y = rs.getDouble("y_pos");
                double z = rs.getDouble("z_pos");

                Date lastJoinDate = rs.getDate("last_joined");
                Date lastLeaveDate = rs.getDate("last_left");

                Location location = new Location(world, x, y, z);

                PlayerUtil.PlayerStorage storage = new PlayerUtil.PlayerStorage(uuid, name, world, location, lastJoinDate, lastLeaveDate, Duration.ZERO); // TODO: Add playtime duration

                stmt.close();

                return storage;
            }

        } catch (SQLException e){
            CobaltServer.getInstance().getLogger().log(Level.SEVERE, "SQLException while retrieving data from database", e);
        }

        return null;
    }

    public static void insertLeavingPlayer(Player player) {
        DataManager.getInstance().getDao(IPlayerDao.class).insertPlayer(player);
        DataManager.getInstance().getDao(ILocationDao.class).insertLocation(player.getUniqueId(), player.getLocation());

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_leave_times(uuid, last_left) VALUES(?, datetime('now','localtime'))");
            ps.setString(1, player.getUniqueId().toString());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertJoiningPlayer(Player player) {
        DataManager.getInstance().getDao(IPlayerDao.class).insertPlayer(player);
        DataManager.getInstance().getDao(ILocationDao.class).insertLocation(player.getUniqueId(), player.getLocation());

        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_join_times(uuid, last_joined) VALUES(?, datetime('now','localtime'))");
            ps.setString(1, player.getUniqueId().toString());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- WARPS -----

    /*
    public static int deleteWarp(String name, UUID playerUUID) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM warps WHERE name = ? AND owner_uuid = ?");
            st.setString(1, name);
            st.setString(2, playerUUID.toString());
            int deletedWarps = st.executeUpdate();
            st.close();
            return deletedWarps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void saveWarps(Map<String, Warp> warps) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO warps(id, name, owner_uuid, owner_name, location_uuid, privacy) VALUES(?,?,?,?,?, ?)");
            conn.setAutoCommit(false);

            for (Warp warp : warps.values()) {
                int id = warp.getId();
                String name = warp.getName();
                UUID owner = warp.getOwner();
                Location location = warp.getLocation();
                String privacyLevel = warp.getPrivacyLevel().name().toLowerCase();

                UUID locationUUID = UUID.randomUUID();
                DataManager.getInstance().getDao(ILocationDao.class).insertLocation(locationUUID, location);

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, owner.toString());
                ps.setString(4, warp.getOwnerName());
                ps.setString(5, locationUUID.toString());
                ps.setString(6, privacyLevel);

                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, Warp> getWarps(){
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM warp_information");
            ResultSet rs = stmt.executeQuery();
            Map<String, Warp> warps = new HashMap<>();

            while (rs.next()){
                String name = rs.getString("name");
                UUID uuid = UUID.fromString(rs.getString("owner_uuid"));
                String ownerName = rs.getString("owner_name");
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("x_pos");
                double y = rs.getDouble("y_pos");
                double z = rs.getDouble("z_pos");

                double yaw = rs.getDouble("yaw");
                double pitch = rs.getDouble("pitch");

                String privacy = rs.getString("privacy");

                Warp warp = new Warp(name, uuid, ownerName, new Location(world, x, y, z, (float)yaw, (float)pitch));
                warp.setPrivacyLevel(privacy);

                warps.put(warp.getExpandedName(), warp);
            }

            stmt.close();

            return warps;

        } catch (SQLException e){
            CobaltServer.getInstance().getLogger().log(Level.SEVERE, "SQLException while retrieving data from database", e);
        }

        return null;
    }
     */
}
