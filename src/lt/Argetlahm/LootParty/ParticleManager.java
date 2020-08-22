package lt.Argetlahm.LootParty;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by User on 2019-01-01.
 */
public class ParticleManager {

    private Plugin plugin;
    private Config config;
    public ParticleManager() {
        this.plugin = Main.plugin;
        this.config = Main.config;
    }

    enum ShapeType {
        RING, RING_WAVE, LINE_UP
    }

    public void spawnShape(ShapeType shapeType, EnumParticle particle, Location location, Player player, int amount, double radius) {

        if (player != null) {
        } else {
            switch (shapeType) {
                case RING_WAVE:
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            for(double size = 0; size < radius; size+=0.5) {
                                for (int degree = 0; degree < 360; degree++) {
                                    double radians = Math.toRadians(degree);
                                    double x = Math.cos(radians) * size;
                                    double z = Math.sin(radians)*size;
                                    location.add(x, 0, z);
                                    for (Player online : Bukkit.getOnlinePlayers()) {
                                        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) (location.getX() + x), (float) (location.getY()), (float) (location.getZ() + z), 0, 0, 0, 0, 1);
                                        ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                                    }
                                    location.subtract(x, 0, z);
                                }
                            }
                        }
                    }.runTaskAsynchronously(Main.plugin);
                    break;
                case LINE_UP:
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            for(double size = 0; size < radius; size+=0.1){
                                //location.add(0,size,0);
                                for (Player online : Bukkit.getOnlinePlayers()) {
                                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true, (float) location.getX(), (float) (location.getY() + size), (float) location.getZ(), 0, 0, 0, 0, 1);
                                    ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                                }
                            }
                        }
                    }.runTaskAsynchronously(Main.plugin);
                    break;
            }
        }

    }

}