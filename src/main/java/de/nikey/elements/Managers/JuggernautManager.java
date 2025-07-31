package de.nikey.elements.Managers;

import de.nikey.elements.Abilities.ElementType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

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

    public void setupJuggernautTeam(List<Player> juggernauts) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName = "juggernauts";

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        } else {
            team.getEntries().forEach(team::removeEntry);
        }

        team.color(NamedTextColor.RED);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);

        for (Player juggernaut : juggernauts) {
            team.addEntry(juggernaut.getName());
        }

        startGlowingRefreshTask(juggernauts);
    }

    private void startGlowingRefreshTask(List<Player> juggernauts) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player juggernaut : juggernauts) {
                if (juggernaut.isOnline()) {
                    juggernaut.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,20*12,0));
                }
            }
        }, 0L, 40L); // 40 Ticks = 2 Sekunden
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

