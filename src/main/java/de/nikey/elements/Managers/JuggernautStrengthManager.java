package de.nikey.elements.Managers;

import de.nikey.elements.Elements;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class JuggernautStrengthManager {

    private final Elements plugin;

    public JuggernautStrengthManager(Elements plugin) {
        this.plugin = plugin;
    }

    public RageStage getRageStage() {
        int currentHealth = Elements.getPlugin().getCoreManager().getCurrentHealth();
        int maxTresorHp = Elements.getPlugin().getCoreManager().getMaxHealth();

        if (currentHealth <= 0 || maxTresorHp <= 0) return RageStage.FURIOUS;
        double percent = (currentHealth * 1.0 / maxTresorHp) * 100;

        if (percent > 75) return RageStage.CALM;
        if (percent > 50) return RageStage.TENSE;
        if (percent > 25) return RageStage.ANGRY;
        return RageStage.FURIOUS;
    }

    public void applyPermanentBuffs(Player player) {
        if (!plugin.getJuggernautManager().isJuggernaut(player.getUniqueId())) return;

        RageStage stage = getRageStage();

        AttributeInstance movement = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (movement != null) {
            switch (stage) {
                case TENSE -> movement.setBaseValue(0.12);
                case ANGRY -> movement.setBaseValue(0.14);
                case FURIOUS -> movement.setBaseValue(0.17);
            }
        }
    }


    public enum RageStage {
        CALM, TENSE, ANGRY, FURIOUS;
    }
}