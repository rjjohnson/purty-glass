package com.subtextgroup.mcp.pg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditIntegration {
	WorldEditPlugin worldEditPlugin;
	public WorldEditIntegration() throws ClassNotFoundException {
		this.worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	}
	public List<Block> getSelectedBlocks(Player player) throws ClassNotFoundException {
		List<Block> result = new ArrayList<>();
		Selection selection = worldEditPlugin.getSelection(player);
		if (selection != null) {
		    Location min = selection.getMinimumPoint();
		    Location max = selection.getMaximumPoint();
		    
			//World world = selection.getWorld();
			return getCuboidBlocks(min, max);
		} else {
		    player.sendMessage("Null selection :(");
			return result;
		}
	}

	private List<Block> getCuboidBlocks(Location p1, Location p2) {

		int xMin = Math.min(p1.getBlockX(), p2.getBlockX());
		int yMin = Math.min(p1.getBlockY(), p2.getBlockY());
		int zMin = Math.min(p1.getBlockZ(), p2.getBlockZ());
		
		int xMax = Math.max(p1.getBlockX(), p2.getBlockX());
		int yMax = Math.max(p1.getBlockY(), p2.getBlockY());
		int zMax = Math.max(p1.getBlockZ(), p2.getBlockZ());

		
		List<Block> blocks = new ArrayList<Block>();
		World w = p1.getWorld();
		
		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					blocks.add(w.getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}
}
