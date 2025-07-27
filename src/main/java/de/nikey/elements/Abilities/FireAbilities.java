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
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FireAbilities implements Listener {
    public static HashMap<UUID, Integer> cooldown = new HashMap<>();
    private static final HashMap<UUID, BossBar> bossbars = new HashMap<>();

    //Ability 1
    private static final List<UUID> hitEntities = new ArrayList<>();

    public static void loderndeWelle(Player player) {
        if (cooldown.containsKey(player.getUniqueId()))return;
        cooldown.put(player.getUniqueId(), 28);

        BossBar bar = BossBar.bossBar(
                Component.text("🔥 Cooldown: " + 28 + "s"),
                1.0f,
                BossBar.Color.RED,
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
                    bossBar.name(Component.text("🔥 Cooldown: " + cooldown.get(player.getUniqueId()) + "s"));
                    bossBar.progress((float) cooldown.get(player.getUniqueId()) / 28);
                }
            }
        }.runTaskTimer(Elements.getPlugin(),0,20);

        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();

        for (int i = 1; i <= 8; i++) {
            Location point = loc.clone().add(direction.clone().multiply(i));
            point.getWorld().spawnParticle(Particle.FLAME, point, 10, 0.3, 0.3, 0.3, 0.01);
            point.getWorld().playSound(point, Sound.BLOCK_FIRE_EXTINGUISH, 0.4f, 2f);

            for (LivingEntity target : point.getWorld().getNearbyLivingEntities(point, 1.8, 1.8, 1.8)) {
                if (!target.equals(player)) {
                    target.setFireTicks(20*10);
                    target.damage(22.0, player);
                    hitEntities.add(target.getUniqueId());
                    target.sendActionBar(Component.text("Your regeneration works less for 10 sek").color(NamedTextColor.GRAY));
                    if (target instanceof Player p) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*10, 0));
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                hitEntities.clear();
            }
        }.runTaskLaterAsynchronously(Elements.getPlugin(),20*10);

        player.sendMessage(Component.text("🔥 Flammenwelle ausgelöst!", NamedTextColor.GOLD));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (hitEntities.contains(event.getEntity().getUniqueId())) {
            event.setAmount(event.getAmount() * 0.7);
        }
    }

    public static void infernoKuppel(Player player) {
        if (cooldown.containsKey(player.getUniqueId()))return;

        cooldown.put(player.getUniqueId(), 26);

        BossBar bar = BossBar.bossBar(
                Component.text("🔥 Cooldown: " + 26 + "s"),
                1.0f,
                BossBar.Color.RED,
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
                    bossBar.name(Component.text("🔥 Cooldown: " + cooldown.get(player.getUniqueId()) + "s"));
                    bossBar.progress((float) cooldown.get(player.getUniqueId()) / 26);
                }
            }
        }.runTaskTimer(Elements.getPlugin(),0,20);

        Location center = player.getLocation();
        World world = player.getWorld();
        double radius = 8;

        for (int angle = 0; angle < 360; angle += 10) {
            double rad = Math.toRadians(angle);
            double x = Math.cos(rad) * radius;
            double z = Math.sin(rad) * radius;
            Location p = center.clone().add(x, 0, z);
            world.spawnParticle(Particle.FLAME, p, 6, 0.2, 1.2, 0.2, 0.01);
        }

        world.playSound(center, Sound.ITEM_FIRECHARGE_USE, 1f, 1f);

        for (LivingEntity le : world.getNearbyLivingEntities(center, radius, 4, radius)) {
            if (!le.equals(player)) {
                Location eloc = le.getLocation();
                if (eloc.distance(center) <= radius) {
                    le.setFireTicks(20*10);
                    if (le instanceof Player target) {
                        target.setCooldown(Material.ENDER_PEARL,20*5);
                    }
                    le.damage(25, player);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*10, 1));
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                world.playSound(center, Sound.BLOCK_FIRE_EXTINGUISH, 0.6f, 0.8f);
                world.spawnParticle(Particle.LARGE_SMOKE, center, 20, 1, 1, 1, 0.05);
            }
        }.runTaskLater(Elements.getPlugin(),20*5);

        player.sendMessage(Component.text("🔥 Inferno-Kuppel ausgelöst!", NamedTextColor.GOLD));
    }


}
