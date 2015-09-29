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
			//World world = selection.getWorld();
			Location min = selection.getMinimumPoint();
			Location max = selection.getMaximumPoint();
			return getCuboidBlocks(min, max);
		} else {
			return result;
		}
	}

	private List<Block> getCuboidBlocks(Location p1, Location p2) {

		int xMin = p1.getBlockX();
		int yMin = p1.getBlockY();
		int zMin = p1.getBlockZ();
		int xMax = p2.getBlockX();
		int yMax = p2.getBlockY();
		int zMax = p2.getBlockZ();

		List<Block> blocks = new ArrayList<Block>();
		World w = p1.getWorld();

		for (int i = xMin; i < xMax; i++) {
			for (int j = yMin; j < yMax; j++) {
				for (int k = zMin; j < zMax; k++) {
					blocks.add(w.getBlockAt(i, j, k));
				}
			}
		}
		return null;
	}
}
