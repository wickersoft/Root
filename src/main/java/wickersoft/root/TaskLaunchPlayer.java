package wickersoft.root;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TaskLaunchPlayer extends BukkitRunnable {

    private final Player player;
    private final Location dest;
    private final double altitude;
    private final boolean previousAllowFlight;
    private final boolean previousFlying;
    private LaunchState state = LaunchState.ASCEND;
    private final Vector launchVelocity = new Vector(0, 1, 0);
    

    public TaskLaunchPlayer(Player player, Location dest) {
        this.player = player;
        this.dest = dest;
        this.altitude = dest.getY();
        this.state = LaunchState.ASCEND;
        previousAllowFlight = player.getAllowFlight();
        previousFlying = player.isFlying();
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
        }
        switch (state) {
            case ASCEND:
                if (player.getLocation().getY() < altitude) {
                    player.setVelocity(launchVelocity);
                    player.getWorld().createExplosion(player.getLocation(), 0);
                    break;
                }
                state = LaunchState.TRAVEL;
            case TRAVEL:
                player.setAllowFlight(true);
                player.setFlying(true);
                Location pos = player.getLocation();
                if (pos.distanceSquared(dest) > 16) {
                    double euclideanDist = Math.sqrt(
                            (dest.getBlockX() - pos.getBlockX()) * (dest.getBlockX() - pos.getBlockX()) 
                                    + (dest.getBlockY() - pos.getBlockY()) * (dest.getBlockY() - pos.getBlockY())
                                    + (dest.getBlockZ() - pos.getBlockZ()) * (dest.getBlockZ() - pos.getBlockZ()));
                    launchVelocity.setX((dest.getBlockX() - pos.getBlockX()) / euclideanDist);
                    launchVelocity.setY((dest.getBlockY() - pos.getBlockY()) / euclideanDist);
                    launchVelocity.setZ((dest.getBlockZ() - pos.getBlockZ()) / euclideanDist);
                    player.setVelocity(launchVelocity);
                    break;
                }
                state = LaunchState.FALL;
            case FALL:
                player.setFlying(false);
                player.setFallDistance(0);
                player.setAllowFlight(true);
                if (player.getLocation().add(0, -2, 0).getBlock().getType() == Material.AIR) {
                    FireworkEffectPlayer.playFirework(player.getLocation().add(new Vector(0, 1, 0)), FireworkEffect.Type.BALL, org.bukkit.Color.fromRGB(0xFF8800), true, false);
                    break;
                }
                cancel();
        }
    }

    @Override
    public void cancel() {
        player.setFallDistance(0);
        player.setAllowFlight(previousAllowFlight);
        player.setFlying(previousFlying);
        player.setVelocity(new Vector(0, 0, 0));
        player.removeMetadata("root.task.launch", Root.instance());
        super.cancel();
    }

    private static enum LaunchState {
        ASCEND, TRAVEL, FALL;
    }
}
