package de.nikey.elements.Core;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;

public class CoreStructure {

    private final Location baseCenter;
    private ItemDisplay maceDisplay;
    private final List<Block> structureBlocks = new ArrayList<>();

    public CoreStructure(Location baseCenter) {
        this.baseCenter = baseCenter;
    }

    public void build() {
        World world = baseCenter.getWorld();

        // Unterster Block (Y)
        Block bottom = world.getBlockAt(baseCenter);
        bottom.setType(Material.IRON_BLOCK);
        structureBlocks.add(bottom);

        // ItemDisplay (Y+1)
        Location displayLoc = baseCenter.clone().add(0.5, 2.0, 0.5);
        maceDisplay = world.spawn(displayLoc, ItemDisplay.class, display -> {
            display.setItemStack(new ItemStack(Material.MACE));
            display.setBillboard(Display.Billboard.VERTICAL);
        });
        Transformation transformation = maceDisplay.getTransformation();
        transformation.getScale().set(1.5,1.5,1.5);
        maceDisplay.setTransformation(transformation);

        maceDisplay.setGlowColorOverride(Color.GRAY);
        maceDisplay.setGlowing(true);

        int[][] offsets = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };

        Location loc = baseCenter.clone().subtract(0,1,0);

        for (int yOffset = 1; yOffset <= 4; yOffset++) {
            for (int[] offset : offsets) {
                Block b = world.getBlockAt(loc.clone().add(offset[0], yOffset, offset[1]));
                b.setType(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                structureBlocks.add(b);
            }
        }


        // Oberster Block (Y+3)
        Block top = world.getBlockAt(baseCenter.clone().add(0, 3, 0));
        top.setType(Material.IRON_BLOCK);
        structureBlocks.add(top);
    }

    public void destroy() {
        for (Block b : structureBlocks) {
            b.setType(Material.AIR);
        }
        structureBlocks.clear();

        if (maceDisplay != null && !maceDisplay.isDead()) {
            maceDisplay.remove();
        }

        baseCenter.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, baseCenter.clone().add(0.5, 1.5, 0.5), 1);
        baseCenter.getWorld().playSound(baseCenter, Sound.ENTITY_WITHER_DEATH, 1, 0.6f);
    }

    public Location getCenter() {
        return baseCenter.clone().add(0.5, 1.5, 0.5); // Mitte des Displays
    }

    public Location getBlockLocation() {
        return baseCenter.clone().add(0, 1, 0); // virtuelles "Core-Ziel" auf Y+1
    }

    public List<Location> getStructureBlocks() {
        List<Location> locations = new ArrayList<>();
        for (Block block : structureBlocks) {
            locations.add(block.getLocation());
        }
        return locations;
    }

}
