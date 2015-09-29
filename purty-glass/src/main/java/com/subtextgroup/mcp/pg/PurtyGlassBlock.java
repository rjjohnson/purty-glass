package com.subtextgroup.mcp.pg;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("purty-glass-block")
public class PurtyGlassBlock implements ConfigurationSerializable{
	private Location loc;
	private Integer ticks;

	public PurtyGlassBlock(Map<String, Object> props) {
		this.loc = Location.deserialize((Map<String, Object>)props.get("loc"));
		this.ticks = (Integer)props.get("ticks");
	}
	public PurtyGlassBlock(Location loc, Integer ticks) {
		this.loc = loc;
		this.ticks = ticks;
		
	}
	
	
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public Integer getTicks() {
		return ticks;
	}
	public void setTicks(Integer ticks) {
		this.ticks = ticks;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new HashMap<>();
		result.put("loc", loc.serialize());
		result.put("ticks", ticks);
		return result;
	}

}
