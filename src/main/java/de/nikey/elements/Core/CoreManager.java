package de.nikey.elements.Core;

import de.nikey.elements.Elements;
import de.nikey.elements.Managers.JuggernautStrengthManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CoreManager {

    private final Location baseLocation;
    private final int maxHealth;
    private int currentHealth;
    private final CoreStructure structure;
    private final BossBar bossBar;
    private final Set<Integer> triggeredPhases = new HashSet<>();
    private JuggernautStrengthManager.RageStage lastStage = null;


    public CoreManager(Location baseLocation, int maxHealth) {
        this.baseLocation = baseLocation;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.structure = new CoreStructure(baseLocation);
        this.bossBar = BossBar.bossBar(
                Component.text("Core Integrity").color(NamedTextColor.GRAY),
                1.0f,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS
        );
        bossBar.addFlag(BossBar.Flag.DARKEN_SCREEN);
        bossBar.addFlag(BossBar.Flag.CREATE_WORLD_FOG);
        this.lastStage = JuggernautStrengthManager.RageStage.CALM;
    }

    public void spawnCore() {
        structure.build();
        showToAll();
    }

    public void removeCore() {
        structure.destroy();
        hideFromAll();
    }

    public void damageCore(Player damager) {
        currentHealth--;
        updateBossBar();

        if (Math.random() < 0.25) {
            damager.getWorld().playSound(damager.getLocation(),Sound.ENTITY_WITHER_HURT,0.8f,1);
        }

        int percentage = (int) (((double) currentHealth / maxHealth) * 100);
        int missingStep = 100 - percentage;

        if (missingStep % 10 == 0 && !triggeredPhases.contains(missingStep)) {
            triggeredPhases.add(missingStep);
            triggerCoreDefenseReaction();
        }

        JuggernautStrengthManager.RageStage newStage = Elements.getPlugin().getJuggernautStrengthManager().getRageStage();
        if (lastStage != newStage) {
            lastStage = newStage;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Elements.getPlugin().getJuggernautManager().isJuggernaut(player.getUniqueId())) {
                    Elements.getPlugin().getJuggernautStrengthManager().applyPermanentBuffs(player);
                }
            }
            notifyJuggernautsStageChange(newStage);
        }

        if (currentHealth <= 0) {
            destroyCore(damager);
        }
    }

    private void updateBossBar() {
        float progress = (float) currentHealth / maxHealth;
        bossBar.progress(Math.max(0f, progress));
    }

    public static ItemStack Live() {
        ItemStack heart = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta itemMeta = heart.getItemMeta();
        itemMeta.customName(Component.text("H ", NamedTextColor.GOLD).decoration(TextDecoration.OBFUSCATED,true).decoration(TextDecoration.ITALIC,false)
                .append(Component.text("Live", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false).decoration(TextDecoration.OBFUSCATED,false))
                .append(Component.text(" H", NamedTextColor.GOLD, TextDecoration.OBFUSCATED).decoration(TextDecoration.ITALIC,false)));
        CustomModelDataComponent dataComponent = itemMeta.getCustomModelDataComponent();
        dataComponent.setStrings(List.of("live"));
        itemMeta.setCustomModelDataComponent(dataComponent);

        itemMeta.getPersistentDataContainer().set(new NamespacedKey(Bukkit.getPluginManager().getPlugin("BuffSMP"),"BuffItem"), PersistentDataType.STRING,"Live");
        heart.setItemMeta(itemMeta);
        return heart;
    }

    private void destroyCore(Player destroyer) {
        Bukkit.broadcast(Component.text(destroyer.getName() + " hat den Core zerstört!"));

        Location baseLocation = structure.getCenter();

        ItemStack[] lootItems = new ItemStack[] {
                new ItemStack(Material.NETHERITE_INGOT, 2),
                new ItemStack(Material.DIAMOND_BLOCK, 6),
                new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1),
                Live(),
                new ItemStack(Material.TRIDENT)
        };

        for (ItemStack item : lootItems) {
            Item dropped = baseLocation.getWorld().dropItemNaturally(baseLocation, item);
            dropped.setGlowing(true);
            dropped.customName(Component.text("Event-Loot").color(NamedTextColor.DARK_AQUA));
            dropped.setCustomNameVisible(true);
        }



        removeCore();
    }

    public boolean isCoreTargetBlock(Location loc) {
        return loc.getBlockX() == structure.getBlockLocation().getBlockX()
                && loc.getBlockY() == structure.getBlockLocation().getBlockY()
                && loc.getBlockZ() == structure.getBlockLocation().getBlockZ();
    }

    public void showToAll() {
        Bukkit.getOnlinePlayers().forEach(bossBar::addViewer);
    }

    public void hideFromAll() {
        Bukkit.getOnlinePlayers().forEach(bossBar::removeViewer);
    }

    public CoreStructure getStructure() {
        return structure;
    }

    public void updateViewers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(baseLocation.getWorld())) {
                bossBar.removeViewer(player);
                continue;
            }
            double distSquared = player.getLocation().distanceSquared(baseLocation);

            if (getCurrentHealth() == 0)continue;

            if (distSquared <= 120 * 120) {
                bossBar.addViewer(player);
            } else {
                bossBar.removeViewer(player);
            }
        }
    }

    private void triggerCoreDefenseReaction() {
        World world = baseLocation.getWorld();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(world)) continue;
            if (Elements.getPlugin().getJuggernautManager().isJuggernaut(player.getUniqueId()))continue;

            if (player.getLocation().distance(baseLocation) <= 30) {
                Vector away = player.getLocation().toVector().subtract(baseLocation.toVector()).normalize().multiply(3);
                away.setY(0.8);
                player.setVelocity(away);

                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 30, 1));
            }
        }

        // Optional: Sound & Partikel
        world.playSound(baseLocation, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, baseLocation.clone().add(0, 2, 0), 1);
    }

    private void notifyJuggernautsStageChange(JuggernautStrengthManager.RageStage stage) {
        TextColor color = switch (stage) {
            case CALM -> NamedTextColor.GREEN;
            case TENSE -> NamedTextColor.YELLOW;
            case ANGRY -> NamedTextColor.GOLD;
            case FURIOUS -> NamedTextColor.RED;
        };

        Component message = Component.text("⚠ Der Tresor ist jetzt im Zustand: ", NamedTextColor.DARK_GRAY)
                .append(Component.text(stage.name(), color));

        for (UUID uuid : Elements.getPlugin().getJuggernautManager().getAllJuggernauts().keySet()) {
            Player juggernaut = Bukkit.getPlayer(uuid);
            if (juggernaut != null && juggernaut.isOnline()) {
                juggernaut.sendMessage(message);
                juggernaut.playSound(juggernaut.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.7f);
                juggernaut.spawnParticle(Particle.LARGE_SMOKE, juggernaut.getLocation().add(0, 1, 0), 12, 0.4, 0.4, 0.4, 0);
            }
        }
    }


    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }
}