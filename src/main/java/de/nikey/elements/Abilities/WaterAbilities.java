package de.nikey.elements.Abilities;

import de.nikey.elements.Elements;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WaterAbilities implements Listener {
    public static HashMap<UUID, Integer> cooldown = new HashMap<>();
    private static final HashMap<UUID, BossBar> bossbars = new HashMap<>();

    public static void geysirAusbruch(Player player) {
        if (cooldown.containsKey(player.getUniqueId()))return;
        cooldown.put(player.getUniqueId(), 22);

        BossBar bar = BossBar.bossBar(
                Component.text("💧 Cooldown: " + 22 + "s"),
                1.0f,
                BossBar.Color.BLUE,
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
                    bossBar.name(Component.text("💧 Cooldown: " + cooldown.get(player.getUniqueId()) + "s"));
                    bossBar.progress((float) cooldown.get(player.getUniqueId()) / 22);
                }
            }
        }.runTaskTimer(Elements.getPlugin(),0,20);

        World world = player.getWorld();
        Location center = player.getLocation();
        Random random = new Random();

        int geysirCount = 6 + random.nextInt(3);

        for (int i = 0; i < geysirCount; i++) {
            double angle = Math.toRadians(random.nextInt(360));
            double distance = 2 + random.nextDouble() * 6;
            Location pos = center.clone().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);

            world.spawnParticle(Particle.DRIPPING_WATER, pos.clone().add(0, 0.5, 0), 15, 0.2, 0.5, 0.2, 0.01);

            Bukkit.getScheduler().runTaskLater(Elements.getPlugin(), () -> {
                world.spawnParticle(Particle.SPLASH, pos, 30, 0.5, 0.5, 0.5, 0.2);
                world.playSound(pos, Sound.ENTITY_PLAYER_SPLASH, 0.8f, 0.8f);

                for (LivingEntity le : world.getNearbyLivingEntities(pos, 2.5, 2.5, 2.5)) {
                    if (!le.equals(player)) {
                        le.setVelocity(new Vector(0, 1.0, 0));
                        le.damage(30,player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20*10, 0));
                    }
                }
            }, 20L);
        }

        player.sendMessage(Component.text("💧 Geysir ausbrechen ausgelöst!", NamedTextColor.AQUA));
    }

    private static final List<UUID> hitEntities = new ArrayList<>();

    public static void flutkraft(Player player) {
        if (cooldown.containsKey(player.getUniqueId()))return;
        cooldown.put(player.getUniqueId(), 28);

        BossBar bar = BossBar.bossBar(
                Component.text("💧 Cooldown: " + 28 + "s"),
                1.0f,
                BossBar.Color.BLUE,
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
                    bossBar.name(Component.text("💧 Cooldown: " + cooldown.get(player.getUniqueId()) + "s"));
                    bossBar.progress((float) cooldown.get(player.getUniqueId()) / 28);
                }
            }
        }.runTaskTimer(Elements.getPlugin(),0,20);

        World world = player.getWorld();
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();

        for (int i = 1; i <= 12; i++) {
            Location wavePos = loc.clone().add(dir.clone().multiply(i));
            world.spawnParticle(Particle.SPLASH, wavePos, 15, 0.3, 0.3, 0.3, 0.05);
            world.playSound(wavePos, Sound.ENTITY_DOLPHIN_SPLASH, 0.6f, 1.4f);

            for (LivingEntity le : world.getNearbyLivingEntities(wavePos, 1.5, 1.5, 1.5)) {
                if (!le.equals(player)) {
                    Vector push = dir.clone().multiply(1.2).setY(0.2);
                    le.setVelocity(push);
                    le.damage(25, player);
                    hitEntities.add(le.getUniqueId());

                    le.sendActionBar(Component.text("You are vulnerable for 10 sek").color(NamedTextColor.GRAY));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            hitEntities.remove(le.getUniqueId());
                        }
                    }.runTaskLater(Elements.getPlugin(),20*10);
                }
            }
        }

        player.sendMessage(Component.text("🌊 Flutwelle entfesselt!", NamedTextColor.AQUA));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (hitEntities.contains(event.getEntity().getUniqueId())) event.setDamage(event.getDamage()*1.2);
    }

}