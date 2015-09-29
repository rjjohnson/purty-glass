package com.subtextgroup.mcp.pg;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.MetadataValue;

public class BreakListener implements Listener {
	PurtyGlass plugin;

	public BreakListener(PurtyGlass plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true)
	public void onPurtyGlassBroken(BlockBreakEvent event) {
		List<MetadataValue> meta = event.getBlock().getMetadata("purty-glass");
		if (meta != null && meta.size() > 0) {
			plugin.removePurtyGlass(event.getBlock().getLocation());
		}
	}

}
