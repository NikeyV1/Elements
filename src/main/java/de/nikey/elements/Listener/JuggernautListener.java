package de.nikey.elements.Listener;

import de.nikey.elements.Core.CoreManager;
import de.nikey.elements.Elements;
import de.nikey.elements.Managers.JuggernautStrengthManager;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JuggernautListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onJuggernautAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!Elements.getPlugin().getJuggernautManager().isJuggernaut(damager.getUniqueId())) return;

        JuggernautStrengthManager.RageStage stage = Elements.getPlugin().getJuggernautStrengthManager().getRageStage();
        double multiplier = switch (stage) {
            case TENSE -> 1.1;
            case ANGRY -> 1.2;
            case FURIOUS -> 1.3;
            default -> 1.0;
        };

        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler
    public void onJuggernautDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Elements.getPlugin().getJuggernautManager().isJuggernaut(player.getUniqueId())) return;

        JuggernautStrengthManager.RageStage stage = Elements.getPlugin().getJuggernautStrengthManager().getRageStage();
        double reduction = switch (stage) {
            case ANGRY -> 0.1;
            case FURIOUS -> 0.2;
            default -> 0.0;
        };

        event.setDamage(event.getDamage() * (1 - reduction));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Elements.getPlugin().getJuggernautStrengthManager().applyPermanentBuffs(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Elements.getPlugin().getJuggernautManager().isJuggernaut(event.getPlayer().getUniqueId())) {
            event.getPlayer().getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.10000000149011612);
        }
    }
}