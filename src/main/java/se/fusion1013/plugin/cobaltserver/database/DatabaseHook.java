package se.fusion1013.plugin.cobaltserver.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.Database;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.warp.Warp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseHook {

    private static final Database database = CobaltCore.getInstance().getRDatabase();

    public static String SQLiteCreateWarpsTable = "CREATE TABLE IF NOT EXISTS warps (" +
            "`id` INTEGER NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "`owner_uuid` varchar(32) NOT NULL," +
            "`world` varchar(32) NOT NULL," +
            "`pos_x` real NOT NULL," +
            "`pos_y` real NOT NULL," +
            "`pos_z` real NOT NULL," +
            "`privacy` varchar(32) NOT NULL," +
            "PRIMARY KEY (`name`)," +
            "CHECK (privacy in ('public','private'))" +
            ");";

    public static void instantiateTables() {
        CobaltCore.getInstance().getLogger().info("Creating database tables");
        database.executeString(SQLiteCreateWarpsTable);
    }

    // ----- WARPS -----

    /**
     * Deletes the warps with the given name
     * @param name the name of the warp(s)
     * @return the number of deleted warps
     */
    public static int deleteWarp(String name){
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM warps WHERE name = ?");
            st.setString(1, name);
            int deletedWarps = st.executeUpdate();
            st.close();
            return deletedWarps;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns a list of all warps
     * @return a list of all warps
     */
    public static List<Warp> getWarps(){
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM warps");
            ResultSet rs = stmt.executeQuery();
            List<Warp> warps = new ArrayList<>();

            while (rs.next()){
                String id = rs.getString("id");
                String name = rs.getString("name");
                UUID uuid = UUID.fromString(rs.getString("owner_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("pos_x");
                double y = rs.getDouble("pos_y");
                double z = rs.getDouble("pos_z");
                String privacy = rs.getString("privacy");

                Warp warp = new Warp(name, uuid, new Location(world, x, y, z));
                warp.setPrivacyLevel(privacy);

                warps.add(warp);
            }

            stmt.close();

            return warps;

        } catch (SQLException e){
            CobaltServer.getInstance().getLogger().log(Level.SEVERE, "SQLException while retrieving data from database", e);
        }

        return null;
    }

    /**
     * Returns a list of warps with the given name
     * @param name name of the warps to find
     * @return a list of warps
     */
    public static List<Warp> getWarpsByName(String name){
        try {
            Connection conn = database.getSQLConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM warps WHERE name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            List<Warp> warps = new ArrayList<>();

            while (rs.next()){
                String id = rs.getString("id");
                UUID uuid = UUID.fromString(rs.getString("owner_uuid"));
                World world = Bukkit.getWorld(rs.getString("world"));
                double x = rs.getDouble("pos_x");
                double y = rs.getDouble("pos_y");
                double z = rs.getDouble("pos_z");
                String privacy = rs.getString("privacy");

                Warp warp = new Warp(name, uuid, new Location(world, x, y, z));
                warp.setPrivacyLevel(privacy);

                warps.add(warp);
            }
            stmt.close();

            return warps;
        } catch (SQLException e){
            CobaltServer.getInstance().getLogger().log(Level.SEVERE, "SQLException while retrieving data from database", e);
        }
        return null;
    }

    /**
     * Insert a warp into the database
     * @param warp the warp to insert
     */
    public static void insertWarp(Warp warp){
        int id = warp.getId();
        String name = warp.getName();
        UUID owner = warp.getOwner();
        Location location = warp.getLocation();
        String privacyLevel = warp.getPrivacyLevel().name().toLowerCase();

        int rowsInserted = 0;

        try {
            PreparedStatement ps = database.getSQLConnection().prepareStatement("INSERT INTO warps(id, name, owner_uuid, world, pos_x, pos_y, pos_z, privacy) VALUES(?,?,?,?,?,?,?,?)");

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, owner.toString());
            ps.setString(4, location.getWorld().getName());
            ps.setDouble(5, location.getX());
            ps.setDouble(6, location.getY());
            ps.setDouble(7, location.getZ());
            ps.setString(8, privacyLevel);

            rowsInserted = ps.executeUpdate();
            ps.close();

        } catch (SQLException e){
            CobaltServer.getInstance().getLogger().log(Level.FINE, "SQLException when inserting into database: ", e);
        }

        CobaltServer.getInstance().getLogger().info("Inserted new warp '" + name + "' into database. " + rowsInserted + " rows inserted");
    }
}
