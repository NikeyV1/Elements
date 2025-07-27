package de.nikey.elements.Abilities;

import de.nikey.elements.Elements;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class EarthAbilities implements Listener {
    public static HashMap<UUID, Integer> cooldown = new HashMap<>();
    private static final HashMap<UUID, BossBar> bossbars = new HashMap<>();

    public static void steinhagel(Player player) {
        if (cooldown.containsKey(player.getUniqueId()))return;
        cooldown.put(player.getUniqueId(), 28);

        BossBar bar = BossBar.bossBar(
                Component.text("Cooldown: " + 28 + "s"),
                1.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );
        bar.addViewer(player);
        bossbars.put(player.getUniqueId(), bar);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldown.get(player.getUniqueId()) == 0) {
                    cooldown.remove(player.getUniqueId());
                    BossBar bossBar = bossbars.remove(player.getUniqueId());
                    if (bossBar != null) bossBar.removeViewer(player);
                    cancel();
                    return;
                }
                cooldown.replace(player.getUniqueId(),cooldown.get(player.getUniqueId())-1);

                BossBar bossBar = bossbars.get(player.getUniqueId());
                if (bossBar != null) {
                    bossBar.name(Component.text("Cooldown: " + cooldown.get(player.getUniqueId()) + "s"));
                    bossBar.progress((float) cooldown.get(player.getUniqueId()) / 28);
                }
            }
        }.runTaskTimer(Elements.getPlugin(),0,20);

        World world = player.getWorld();
        Location center = player.getLocation();

        new BukkitRunnable() {
            int ticks = 0;
            final Random random = new Random();

            @Override
            public void run() {
                if (ticks++ >= 18) {
                    cancel();
                    return;
                }

                // zufällige Position im Umkreis
                double angle = Math.toRadians(random.nextInt(360));
                double distance = 2 + random.nextDouble() * 6;
                double x = Math.cos(angle) * distance;
                double z = Math.sin(angle) * distance;

                Location spawn = center.clone().add(x, 10, z);

                // Falling Block als Stein simulieren
                FallingBlock stone = world.spawn(spawn, FallingBlock.class);
                stone.setCancelDrop(true);
                stone.setBlockData(Material.COBBLESTONE.createBlockData());
                stone.setHurtEntities(false);
                stone.setVelocity(new Vector(0, -1.2, 0));

                world.playSound(spawn, Sound.BLOCK_STONE_BREAK, 0.4f, 0.8f + random.nextFloat() * 0.4f);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (LivingEntity le : world.getNearbyLivingEntities(stone.getLocation(), 1.8, 1.8, 1.8)) {
                            if (!le.equals(player)) {
                                le.damage(28.0, player);
                                le.setVelocity(new Vector(0, 0.8, 0));
                                if (new Random().nextDouble() < 0.3) {
                                    le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20*8, 1));
                                }
                                world.spawnParticle(Particle.BLOCK_CRUMBLE, le.getLocation(), 6, 0.2, 0.3, 0.2, Material.COBBLESTONE.createBlockData());
                            }
                        }
                        stone.remove();
                    }
                }.runTaskLater(Elements.getPlugin(),20);
            }
        }.runTaskTimer(Elements.getPlugin(), 0L, 5);

        player.sendMessage(Component.text("Steinhagel beschwört!", NamedTextColor.DARK_GRAY));
    }

    public static void wurzelkaefig(Player player) {
        if (cooldown.containsKey(player.getUniqueId()))return;
        cooldown.put(player.getUniqueId(), 20);

        BossBar bar = BossBar.bossBar(
                Component.text("Cooldown: " + 20 + "s"),
                1.0f,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );
        bar.addViewer(player);
        bossbars.put(player.getUniqueId(), bar);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldown.get(player.getUniqueId()) == 0) {
                    cooldown.remove(player.getUniqueId());
                    BossBar bossBar = bossbars.remove(player.getUniqueId());
                    if (bossBar != null) bossBar.removeViewer(player);
                    cancel();
                    return;
                }
                cooldown.replace(player.getUniqueId(),cooldown.get(player.getUniqueId())-1);

                BossBar bossBar = bossbars.get(player.getUniqueId());
                if (bossBar != null) {
                    bossBar.name(Component.text("Cooldown: " + cooldown.get(player.getUniqueId()) + "s"));
                    bossBar.progress((float) cooldown.get(player.getUniqueId()) / 20);
                }
            }
        }.runTaskTimer(Elements.getPlugin(),0,20);

        Location center = player.getLocation();
        World world = player.getWorld();
        double radius = 6;

        for (int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * radius;
            double z = Math.sin(rad) * radius;
            Location rootPos = center.clone().add(x, 0, z);
            world.spawnParticle(Particle.BLOCK_CRUMBLE, rootPos, 10, 0.2, 0.5, 0.2, Material.MANGROVE_ROOTS.createBlockData());
        }

        world.playSound(center, Sound.BLOCK_ROOTED_DIRT_STEP, 1f, 0.8f);

        for (LivingEntity le : world.getNearbyLivingEntities(center, radius, 3, radius)) {
            if (!le.equals(player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*8, 2));
                le.setVelocity(new Vector(0, 0, 0));
                le.setFreezeTicks(70);
                le.damage(25, player);
                world.spawnParticle(Particle.HAPPY_VILLAGER, le.getLocation(), 5, 0.2, 0.4, 0.2, 0.01);
            }
        }

        player.sendMessage(Component.text("Wurzelkäfig erschafft!", NamedTextColor.GREEN));
    }
}
