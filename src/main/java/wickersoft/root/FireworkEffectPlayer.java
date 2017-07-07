package wickersoft.root;

import java.util.List;
import net.minecraft.server.v1_12_R1.EntityFireworks;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityStatus;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FireworkEffectPlayer {

    private static boolean nmsDetected = false;

    static {
        try {
            Class.forName("net.minecraft.server.v1_12_R1.EntityFireworks");
            nmsDetected = true;
            System.out.println("Compatible NMS version detected");
        } catch (Exception e) {
            System.out.println("NMS version not compatible!");
        }
    }
    
    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, org.bukkit.Color color, org.bukkit.Color fade, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.withFade(fade);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }
        playFirework(loc, bu.build());
    }

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, org.bukkit.Color color, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }
        playFirework(loc, bu.build());
    }

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, List<org.bukkit.Color> color, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }
        playFirework(loc, bu.build());
    }

    public static void playFirework(Location loc, org.bukkit.FireworkEffect.Type type, List<org.bukkit.Color> color, List<org.bukkit.Color> fade, boolean trail, boolean twinkle) {
        FireworkEffect.Builder bu = FireworkEffect.builder();
        bu.withColor(color);
        bu.withFade(fade);
        bu.with(type);
        if (trail) {
            bu.withTrail();
        }
        if (twinkle) {
            bu.withFlicker();
        }

        playFirework(loc, bu.build());
    }

    public static void playFirework(Location location, FireworkEffect... effects) {
        if (!nmsDetected) {
            return;
        }
        ItemStack is = new ItemStack(Material.FIREWORK, 1);
        FireworkMeta meta = (FireworkMeta) is.getItemMeta();
        meta.addEffects(effects);
        is.setItemMeta(meta);
        net.minecraft.server.v1_12_R1.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        CustomEntityFirework_1_12_R1 firework = new CustomEntityFirework_1_12_R1(location, nmsIs);
        firework.perform();
    }

    //CustomEntityFirework class by recon88: https://github.com/recon88/Instant-Fireworks/blob/master/src/CustomEntityFirework.java
    private static class CustomEntityFirework_1_12_R1 extends EntityFireworks {

        private final Player[] players = new Player[]{};
        private final Location location;
        private boolean gone = false;

        protected CustomEntityFirework_1_12_R1(Location loc, net.minecraft.server.v1_12_R1.ItemStack metaContainer) {
            super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), metaContainer);
            Bukkit.getOnlinePlayers().toArray(players);
            this.a(0.25F, 0.25F);
            this.location = loc;
        }

        public boolean perform() {
            try {
                if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(this)) {
                    setInvisible(true);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void B_() {
            if (gone) {
                return;
            }
            gone = true;
            if (players != null && players.length > 0) {
                for (Player player : players) {
                    (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
                }
            } else {
                world.broadcastEntityEffect(this, (byte) 17);
            }
            this.die();
        }
    }
}
