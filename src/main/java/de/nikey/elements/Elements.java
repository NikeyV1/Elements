package de.nikey.elements;

import de.nikey.elements.Abilities.FireAbilities;
import de.nikey.elements.Abilities.WaterAbilities;
import de.nikey.elements.Commands.ElementCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Elements extends JavaPlugin {
    private static Elements plugin;

    @Override
    public void onEnable() {
        plugin = this;
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new WaterAbilities(),this);
        manager.registerEvents(new FireAbilities(),this);

        getCommand("element").setExecutor(new ElementCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Elements getPlugin() {
        return plugin;
    }
}
