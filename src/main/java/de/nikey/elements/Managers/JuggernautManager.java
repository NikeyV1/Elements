package de.nikey.elements.Managers;

import de.nikey.elements.Abilities.ElementType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JuggernautManager {

    private final JavaPlugin plugin;
    private final Map<UUID, ElementType> juggernauts = new HashMap<>();

    public JuggernautManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        juggernauts.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("juggernauts");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ElementType type = ElementType.valueOf(section.getString(key));
                juggernauts.put(uuid, type);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Ungültiger Juggernaut-Eintrag: " + key);
            }
        }
    }

    public void save() {
        plugin.getConfig().set("juggernauts", null); // clear first
        for (Map.Entry<UUID, ElementType> entry : juggernauts.entrySet()) {
            plugin.getConfig().set("juggernauts." + entry.getKey(), entry.getValue().name());
        }
        plugin.saveConfig();
    }

    // API
    public boolean isJuggernaut(UUID uuid) {
        return juggernauts.containsKey(uuid);
    }

    public ElementType getElement(UUID uuid) {
        return juggernauts.get(uuid);
    }

    public void setJuggernaut(UUID uuid, ElementType element) {
        juggernauts.put(uuid, element);
        save();
    }

    public void removeJuggernaut(UUID uuid) {
        juggernauts.remove(uuid);
        save();
    }

    public Map<UUID, ElementType> getAllJuggernauts() {
        return Collections.unmodifiableMap(juggernauts);
    }
}

