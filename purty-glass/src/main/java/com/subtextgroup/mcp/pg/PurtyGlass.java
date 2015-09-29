package com.subtextgroup.mcp.pg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class PurtyGlass extends JavaPlugin {
    public static final byte[] STAINED_GLASS_COLORS = { (byte) 0, // White
                                                                  // Stained
                                                                  // Glass
            (byte) 1, // Orange Stained Glass
            (byte) 2, // Magenta Stained Glass
            (byte) 3, // Light Blue Stained Glass
            (byte) 4, // Yellow Stained Glass
            (byte) 5, // Lime Stained Glass
            (byte) 6, // Pink Stained Glass
            (byte) 7, // Gray Stained Glass
            (byte) 8, // Light Gray Stained Glass
            (byte) 9, // Cyan Stained Glass
            (byte) 10, // Purple Stained Glass
            (byte) 11, // Blue Stained Glass
            (byte) 12, // Brown Stained Glass
            (byte) 13, // Green Stained Glass
            (byte) 14, // Red Stained Glass
            (byte) 15 // Black Stained Glass
    };
    private List<PurtyGlassBlock> activePurtyGlassBlocks = new CopyOnWriteArrayList<>();
    
    private static class RangeException extends Exception {
    }

    private boolean singlePurtyGlassBlock(CommandSender sender, Command command, Long ticks) {
        Player player = (Player) sender;
        Block target = player.getTargetBlock((Set) null, 10);
        if (target == null) {
            return false;
        }
        makePurtyGlassBlock(target, ticks);
        return true;
    }

    private boolean singlePurtyGlassPane(CommandSender sender, Command command, Long ticks) {
        return false;
    }

    private boolean selectionPurtyGlassBlock(CommandSender sender, Command command, Long ticks) {

        List<Block> blocks = getWorldEditBlocks(sender);
        if (blocks != null && blocks.size() > 0) {
            for (Block block : blocks) {
                makePurtyGlassBlock(block, ticks);
            }
            return true;
        }
        return false;
    }

    private boolean selectionPurtyGlassPane(CommandSender sender, Command command, Long ticks) {
        return false;
    }

    private List<Block> getWorldEditBlocks(CommandSender sender) {
        try {
            WorldEditIntegration wei = new WorldEditIntegration();
            return wei.getSelectedBlocks((Player) sender);
        } catch (ClassNotFoundException cnfe) {
            sender.sendMessage("WorldEdit not available");
            return null;
        }
    }

    private void makePurtyGlassBlock(Block block, Long ticks) {
        List<PurtyGlassBlock> purtyGlassBlocks = (List<PurtyGlassBlock>) getConfig().getList("purty-glass-blocks", new ArrayList<PurtyGlassBlock>());
        if (!containsLocation(purtyGlassBlocks, block.getLocation())) {
            PurtyGlassBlock pgb = new PurtyGlassBlock(block.getLocation(), ticks);
            purtyGlassBlocks.add(pgb);
            block.setType(Material.STAINED_GLASS);
            //scheduleBlock(block, ticks);
            activePurtyGlassBlocks.add(pgb);
            getConfig().set("purty-glass-blocks", purtyGlassBlocks);
            // saveConfig();
        }
    }

    static Random r = new Random();

    private static final int getRandomColorIdx() {
        return r.nextInt(STAINED_GLASS_COLORS.length);
    }

    private void scheduleBlock(final Block block, Long ticks) {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            int lastColorIdx = getRandomColorIdx();

            public void run() {
                block.setData(STAINED_GLASS_COLORS[lastColorIdx]);
                lastColorIdx++;
                if (lastColorIdx == STAINED_GLASS_COLORS.length) {
                    lastColorIdx = 0;
                }
            }
        }, 0, ticks);
    }

    private boolean containsLocation(List<PurtyGlassBlock> pgbs, Location loc) {
        for (PurtyGlassBlock pgb : pgbs) {
            if (pgb.getLoc().equals(loc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Long ticks = 10L;
        if (args.length == 1) {
            try {
                ticks = Long.parseLong(args[0]);
                if (ticks < 10) {
                    throw new RangeException();
                }
            } catch (NumberFormatException | RangeException e) {
                sender.sendMessage("Not a valid number of ticks: " + args[0]);
                return false;
            }
        } else if (args.length > 1) {
            sender.sendMessage(command.getUsage());
            return false;
        }

        if (command.getName().equalsIgnoreCase("purtyglassblock")) {
            return singlePurtyGlassBlock(sender, command, ticks);
        } else if (command.getName().equalsIgnoreCase("selpurtyglassblock")) {
            return selectionPurtyGlassBlock(sender, command, ticks);
        } else if (command.getName().equalsIgnoreCase("purtyglasspane")) {
            return singlePurtyGlassPane(sender, command, ticks);
        } else if (command.getName().equalsIgnoreCase("selpurtyglasspane")) {
            return selectionPurtyGlassPane(sender, command, ticks);
        }
        return false;
    }

    BreakListener listener = null;

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(PurtyGlassBlock.class);
        this.listener = new BreakListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        loadPurtyGlassBlocks();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                saveConfig();
            }
        }, 0, 100);
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            Map<PurtyGlassBlock, TickMeta> tickMap = new HashMap<>();
            Long currentTicks = 0L;
            public void run() {
                Iterator<PurtyGlassBlock> iter = activePurtyGlassBlocks.iterator();
                while(iter.hasNext()) {
                    PurtyGlassBlock pgb = iter.next();
                    TickMeta meta = tickMap.get(pgb);
                    if(meta == null) {
                        meta = new TickMeta();
                        meta.schedule = pgb.getTicks();
                        meta.lastColorIdx = getRandomColorIdx();
                        tickMap.put(pgb, meta);
                    }
                    if(currentTicks - meta.lastRun >= meta.schedule) {
                        meta.lastRun = currentTicks;
                        meta.lastColorIdx = meta.lastColorIdx == STAINED_GLASS_COLORS.length - 1 ? 0 : meta.lastColorIdx + 1;
                        pgb.getLoc().getBlock().setData(STAINED_GLASS_COLORS[meta.lastColorIdx]);
                    }
                }
                currentTicks++;
            }
        }, 30, 10);
        
        getServer().broadcastMessage("PurtyGlass enabled!");
    }
    private static class TickMeta {
        public Long schedule = 10L;
        public Long lastRun = 0L;
        public Integer lastColorIdx = 0;
    }
    
    private void loadPurtyGlassBlocks() {
        List<PurtyGlassBlock> purtyGlassBlocks = (List<PurtyGlassBlock>) getConfig().getList("purty-glass-blocks", new ArrayList<PurtyGlassBlock>());
        Iterator<PurtyGlassBlock> iter = purtyGlassBlocks.iterator();
        boolean updated = false;
        while (iter.hasNext()) {
            PurtyGlassBlock pgb = iter.next();
            Block target = pgb.getLoc().getBlock();
            if (target != null && (Material.STAINED_GLASS == target.getType() || Material.STAINED_GLASS_PANE == target.getType())) {
                target.setMetadata("purty-glass", new FixedMetadataValue(this, pgb.getTicks()));
                //scheduleBlock(target, pgb.getTicks());
            } else {
                iter.remove();
                updated = true;
            }
        }
        activePurtyGlassBlocks.addAll(purtyGlassBlocks);
        if (updated) {
            getConfig().set("purty-glass-blocks", purtyGlassBlocks);
            // saveConfig();
        }
    }

    protected void removePurtyGlass(Location loc) {
        List<PurtyGlassBlock> purtyGlassBlocks = (List<PurtyGlassBlock>) getConfig().getList("purty-glass-blocks", new ArrayList<PurtyGlassBlock>());
        Iterator<PurtyGlassBlock> iter = purtyGlassBlocks.iterator();
        while (iter.hasNext()) {
            if (iter.next().getLoc().equals(loc)) {
                iter.remove();
            }
        }
        activePurtyGlassBlocks.retainAll(purtyGlassBlocks);
        getConfig().set("purty-glass-blocks", purtyGlassBlocks);
        // saveConfig();
    }
}
