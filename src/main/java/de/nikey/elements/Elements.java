package de.nikey.elements;

import de.nikey.elements.Abilities.FireAbilities;
import de.nikey.elements.Abilities.WaterAbilities;
import de.nikey.elements.Commands.ElementCommand;
import de.nikey.elements.Core.CoreListener;
import de.nikey.elements.Core.CoreManager;
import de.nikey.elements.Listener.JuggernautListener;
import de.nikey.elements.Managers.JuggernautManager;
import de.nikey.elements.Managers.JuggernautStrengthManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Elements extends JavaPlugin {
    private static Elements plugin;
    private CoreManager coreManager;
    private JuggernautManager juggernautManager;
    private JuggernautStrengthManager juggernautStrengthManager;

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("element").setExecutor(new ElementCommand());

        saveDefaultConfig();
        FileConfiguration config = getConfig();

        String worldName = config.getString("core.world", "world");
        double x = config.getDouble("core.x");
        double y = config.getDouble("core.y");
        double z = config.getDouble("core.z");
        int maxHealth = config.getInt("core.maxHealth");

        Location coreLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
        this.coreManager = new CoreManager(coreLocation, maxHealth);

        coreManager.spawnCore();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new WaterAbilities(),this);
        manager.registerEvents(new FireAbilities(),this);
        manager.registerEvents(new CoreListener(coreManager), this);
        manager.registerEvents(new JuggernautListener(),this);

        this.juggernautManager = new JuggernautManager(this);
        juggernautManager.load();

        juggernautStrengthManager = new JuggernautStrengthManager(this);

        Bukkit.getScheduler().runTaskTimer(this, () -> coreManager.updateViewers(), 40L, 50L);
    }

    @Override
    public void onDisable() {
        if (coreManager != null) {
            coreManager.removeCore();
        }
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public JuggernautManager getJuggernautManager() {
        return juggernautManager;
    }

    public JuggernautStrengthManager getJuggernautStrengthManager() {
        return juggernautStrengthManager;
    }

    public static Elements getPlugin() {
        return plugin;
    }
}
