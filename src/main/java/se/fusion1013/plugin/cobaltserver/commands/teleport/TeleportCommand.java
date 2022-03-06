package se.fusion1013.plugin.cobaltserver.commands.teleport;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.RotationArgument;
import dev.jorel.commandapi.wrappers.Rotation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltserver.CobaltServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TeleportCommand {

    // ----- REGISTER -----

    public static void register() {

        // Tp Entity(s) to Entity
        createTpToEntityCommand().register();
        createTpManyToEntityCommand().register();

        // Tp Entity(s) to Location
        createTpToPosCommand().register();
        createTpManyToPosCommand().register();

        // Tp Entity(s) to Location with Rotation
        createTpManyToPosRotationCommand().register();

        // Tp Entity(s) to Location Facing
        // TODO: createTpManyToPosFacingEntityCommand().register();
        // TODO: createTpManyToPosFacingLocationCommand().register();
        // TODO: The two above do not work for some reason.

        // Tphere
        createTpHereCommand().register();

        // Tpall
        createTpAllCommand().register();

    }

    // ----- TPALL COMMAND -----

    private static CommandAPICommand createTpAllCommand() {
        return new CommandAPICommand("tpall")
                .withPermission("cobalt.commands.teleport")
                .executesEntity(((sender, args) -> {
                    List<Entity> entityList = new ArrayList<>(Bukkit.getOnlinePlayers());
                    teleportManyEntitiesToLocation(sender, entityList, sender.getLocation());
                })).executesCommandBlock(((sender, args) -> {
                    List<Entity> entityList = new ArrayList<>(Bukkit.getOnlinePlayers());
                    teleportManyEntitiesToLocation(sender, entityList, sender.getBlock().getLocation());
                }));
    }

    // ----- TPHERE COMMAND -----

    private static CommandAPICommand createTpHereCommand() {
        return new CommandAPICommand("tphere")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
                .executesEntity(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    teleportManyEntitiesToLocation(sender, entities, sender.getLocation());
                })).executesCommandBlock(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    teleportManyEntitiesToLocation(sender, entities, sender.getBlock().getLocation());
                }));
    }

    // ----- TP TO ENTITY COMMAND -----

    private static CommandAPICommand createTpToEntityCommand() {
        return new CommandAPICommand("teleport")
                .withAliases("tp")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("destination"))
                .executesEntity(((sender, args) -> {
                    Location targetLocation = ((Entity) args[0]).getLocation();
                    teleportEntityToLocation(sender, sender, targetLocation);
                }));
    }

    // ----- TP TO MANY TO ENTITY COMMAND -----

    private static CommandAPICommand createTpManyToEntityCommand() {
        return new CommandAPICommand("teleport")
                .withAliases("tp")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
                .withArguments(new EntitySelectorArgument("destination"))
                .executes(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    Location location = ((Entity)args[1]).getLocation();
                    teleportManyEntitiesToLocation(sender, entities, location);
                }));
    }

    // ----- TP TO LOCATION COMMAND -----

    private static CommandAPICommand createTpToPosCommand() {
        return new CommandAPICommand("teleport")
                .withAliases("tp", "tppos")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new LocationArgument("location"))
                .executesEntity(((sender, args) -> {
                    Location targetLocation = (Location) args[0];
                    teleportEntityToLocation(sender, sender, targetLocation);
                }));
    }

    // ----- TP MANY TO LOCATION COMMAND -----

    private static CommandAPICommand createTpManyToPosCommand() {
        return new CommandAPICommand("teleport")
                .withAliases("tp", "tppos")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
                .withArguments(new LocationArgument("location"))
                .executes(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    Location location = (Location) args[1];
                    teleportManyEntitiesToLocation(sender, entities, location);
                }));
    }

    // ----- TP MANY TO LOCATION WITH ROTATION COMMAND -----

    private static CommandAPICommand createTpManyToPosRotationCommand() {
        return new CommandAPICommand("teleport")
                .withAliases("tp", "tppos")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
                .withArguments(new LocationArgument("location"))
                .withArguments(new RotationArgument("rotation"))
                .executes(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    Location location = (Location) args[1];
                    Rotation rotation = (Rotation) args[2];
                    location.setYaw(rotation.getYaw());
                    location.setPitch(rotation.getPitch());
                    teleportManyEntitiesToLocation(sender, entities, location);
                }));
    }

    // ----- FACING LOCATION COMMAND -----

    private static CommandAPICommand createTpManyToPosFacingLocationCommand() {
        return new CommandAPICommand("facing")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
                .withArguments(new LocationArgument("location"))
                .withArguments(new LiteralArgument("facing"))
                .withArguments(new LocationArgument("facingLocation"))
                .executes(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    Location location = (Location) args[1];
                    Location facingLocation = (Location) args[2];

                    teleportFacing(sender, entities, location, facingLocation);
                }));
    }

    // ----- FACING ENTITY COMMAND -----

    private static CommandAPICommand createTpManyToPosFacingEntityCommand() {
        return new CommandAPICommand("facing")
                .withPermission("cobalt.commands.teleport")
                .withArguments(new EntitySelectorArgument("targets", EntitySelectorArgument.EntitySelector.MANY_ENTITIES))
                .withArguments(new LocationArgument("location"))
                .withArguments(new LiteralArgument("facing"))
                .withArguments(new EntitySelectorArgument("facingEntity", EntitySelectorArgument.EntitySelector.ONE_ENTITY))
                .executes(((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Entity> entities = (Collection<Entity>) args[0];
                    Location location = (Location) args[1];
                    Entity facingEntity = (Entity) args[2];

                    teleportFacing(sender, entities, location, facingEntity.getLocation());
                }));
    }

    // ----- GENERAL TP METHODS -----

    private static void teleportFacing(CommandSender sender, Collection<Entity> entities, Location location, Location facingLocation) {
        location.setDirection(genVec(location, facingLocation));
        teleportManyEntitiesToLocation(sender, entities, location);
    }

    private static Vector genVec(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        //If you want to: vector = vector.normalize();

        return new Vector(x, z, y);
    }

    /**
     * Teleports multiple entities to a location.
     *
     * @param sender the sender of the command.
     * @param entities the entities that are being teleported.
     * @param location the location to teleport the entities to.
     */
    private static void teleportManyEntitiesToLocation(CommandSender sender, Collection<Entity> entities, Location location) {
        for (Entity e : entities) {
            location.setPitch(e.getLocation().getPitch());
            location.setYaw(e.getLocation().getYaw());
            e.teleport(location);
        }

        if (sender instanceof Player player) {
            if (entities.size() == 1) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("entity", entities.stream().toList().get(0).getName())
                        .addPlaceholder("location", location)
                        .build();
                LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), player, "commands.teleport.entity_to_location", placeholders);
            } else {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("entity_count", entities.size())
                        .addPlaceholder("location", location)
                        .build();
                LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), player, "commands.teleport.entities_to_location", placeholders);
            }
        }
    }

    /**
     * Teleports an entity to a location.
     *
     * @param sender the sender of the command.
     * @param entity the entity that is being teleported.
     * @param location the location to teleport the entity to.
     */
    private static void teleportEntityToLocation(Entity sender, Entity entity, Location location) {
        location.setPitch(entity.getLocation().getPitch());
        location.setYaw(entity.getLocation().getYaw());
        entity.teleport(location);

        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("entity", entity.getName())
                    .addPlaceholder("location", location)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltServer.getInstance(), player, "commands.teleport.entity_to_location", placeholders);
        }
    }
}
