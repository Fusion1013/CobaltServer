package se.fusion1013.plugin.cobaltserver.database.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import se.fusion1013.plugin.cobaltcore.database.location.ILocationDao;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltserver.CobaltServer;
import se.fusion1013.plugin.cobaltserver.warp.Warp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class WarpDaoSQLite extends Dao implements IWarpDao {

    // ----- VARIABLES -----

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

    public static String SQLiteCreateWarpInformationView = "CREATE VIEW IF NOT EXISTS warp_information AS" +
            " SELECT warps.name, warps.owner_uuid, warps.owner_name, warps.location_uuid, warps.privacy, locations.world, locations.x_pos, locations.y_pos, locations.z_pos, locations.yaw, locations.pitch" +
            " FROM warps" +
            " INNER JOIN locations ON locations.uuid = warps.location_uuid;";

    // ----- METHODS -----

    @Override
    public void deleteWarp(String name, UUID playerUUID) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM warps WHERE name = ? AND owner_uuid = ?");
            st.setString(1, name);
            st.setString(2, playerUUID.toString());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveWarps(Map<String, Warp> warps) {
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

                UUID locationUUID = warp.getLocationUUID();
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

    @Override
    public Map<String, Warp> getWarps() {
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

                UUID locationUUID = UUID.fromString(rs.getString("location_uuid"));

                Warp warp = new Warp(name, uuid, ownerName, new Location(world, x, y, z, (float)yaw, (float)pitch), locationUUID);
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

    // ----- INIT -----

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateWarpsTable);
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateWarpInformationView);
    }
}
