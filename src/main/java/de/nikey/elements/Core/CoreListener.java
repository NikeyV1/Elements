package de.nikey.elements.Core;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CoreListener implements Listener {

    private final CoreManager coreManager;

    public CoreListener(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location broken = event.getBlock().getLocation();

        if (!coreManager.getStructure().getStructureBlocks().contains(broken)) return;

        event.setCancelled(true);
        coreManager.damageCore(player);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block ->
                coreManager.getStructure().getStructureBlocks().contains(block.getLocation())
        );
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block ->
                coreManager.getStructure().getStructureBlocks().contains(block.getLocation())
        );
    }
}