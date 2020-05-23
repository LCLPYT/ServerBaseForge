package work.lclpnet.serverbase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;

import com.electronwill.nightconfig.core.file.FileConfig;

public class Config {

	private static FileConfig config = null;
	private static Map<String, Object> register = new HashMap<>();
	
	public static final String KEY_SPAWN_PROT_ENABLED = "spawn-prot.enabled",
			KEY_SPAWN_PROT_RANGE = "spawn-prot.range",
			KEY_SPAWN_PROT_DIMENSIONS = "spawn-prot.dimensions",
			KEY_SPAWN_PROT_ICE_MELT = "spawn-prot.rules.ice-melt",
			KEY_SPAWN_PROT_WATER_FREEZE = "spawn-prot.rules.water-freeze",
			KEY_SPAWN_PROT_SNOW_MELT = "spawn-prot.rules.snow-melt",
			KEY_SPAWN_PROT_SNOW_FALL = "spawn-prot.rules.snow-fall",
			KEY_SPAWN_PROT_ALLOW_ARMOR_STAND_INTERACTION = "spawn-prot.rules.allow-armor-stand-interact",
			KEY_SPAWN_PROT_ALLOW_EXPLOSIONS = "spawn-prot.rules.allow-explosions",
			KEY_SPAWN_PROT_ALLOW_MOB_GRIEFING = "spawn-prot.rules.allow-mob-griefing",
			KEY_SPAWN_PROT_ALLOW_ITEM_FRAME_INTERACTION = "spawn-prot.rules.allow-item-frame-interaction",
			KEY_SPAWN_PROT_ALLOW_MOB_SPAWN = "spawn-prot.rules.allow-mob-spawning",
			KEY_SPAWN_PROT_ALLOW_BLOCK_BREAK = "spawn-prot.rules.allow-block-break",
			KEY_SPAWN_PROT_ALLOW_BLOCK_PLACE = "spawn-prot.rules.allow-block-place",
			KEY_SPAWN_PROT_ALLOW_BUCKET_INTERACTION = "spawn-prot.rules.allow-bucket-interaction";
	
	static {
		register.put(KEY_SPAWN_PROT_ENABLED, false);
		register.put(KEY_SPAWN_PROT_RANGE, 400);
		register.put(KEY_SPAWN_PROT_DIMENSIONS, new ArrayList<>());
		register.put(KEY_SPAWN_PROT_ICE_MELT, true);
		register.put(KEY_SPAWN_PROT_WATER_FREEZE, true);
		register.put(KEY_SPAWN_PROT_SNOW_MELT, true);
		register.put(KEY_SPAWN_PROT_SNOW_FALL, true);
		register.put(KEY_SPAWN_PROT_ALLOW_ARMOR_STAND_INTERACTION, true);
		register.put(KEY_SPAWN_PROT_ALLOW_EXPLOSIONS, true);
		register.put(KEY_SPAWN_PROT_ALLOW_MOB_GRIEFING, true);
		register.put(KEY_SPAWN_PROT_ALLOW_ITEM_FRAME_INTERACTION, true);
		register.put(KEY_SPAWN_PROT_ALLOW_MOB_SPAWN, true);
		register.put(KEY_SPAWN_PROT_ALLOW_BLOCK_BREAK, true);
		register.put(KEY_SPAWN_PROT_ALLOW_BLOCK_PLACE, true);
		register.put(KEY_SPAWN_PROT_ALLOW_BUCKET_INTERACTION, true);
	}
	
	public static void load() {
		File configDir = new File("config");
		File configFile = new File(configDir, "serverbase.toml");
		
		if(!configFile.exists()) createConfigFile(configFile);
		config = FileConfig.builder(configFile).build();
		config.load();
		config.close();
		
		populateConfig();
	}
	
	private static void populateConfig() {
		Holder<Boolean> modified = new Holder<>(false);
		
		register.forEach((path, defaultValue) -> {
			if(!config.contains(path)) {
				config.set(path, defaultValue);
				if(!modified.value) modified.value = true;
			}
		});
		
		if(modified.value) save();
	}

	private static boolean createConfigFile(File config) {
		try {
			config.getParentFile().mkdirs();
			config.createNewFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static FileConfig getConfig() {
		return config;
	}
	
	private static void set(String path, Object val) {
		config.set(path, val);
		save();
	}
	
	private static <T> T get(String path) {
		if(!config.contains(path)) {
			if(!register.containsKey(path)) throw new IllegalStateException("Path not registered.");
			set(path, register.get(path));
		}
		return config.get(path);
	}

	private static void save() {
		new Thread(() -> {
			FileConfig newConfig = FileConfig.builder(config.getFile()).build();
			newConfig.putAll(config);
			newConfig.save();
			newConfig.close();
		}, "Config Saver").run(); 
	}
	
	public static boolean isSpawnProtEnabled() {
		return get(KEY_SPAWN_PROT_ENABLED);
	}
	
	public static void setSpawnProtEnabled(boolean enabled) {
		set(KEY_SPAWN_PROT_ENABLED, enabled);
	}
	
	public static int getSpawnProtectionRange() {
		return get(KEY_SPAWN_PROT_RANGE);
	}
	
	public static void setSpawnProtectionRange(double range) {
		set(KEY_SPAWN_PROT_RANGE, range);
	}

	public static boolean shouldIceMelt() {
		return get(KEY_SPAWN_PROT_ICE_MELT);
	}
	
	public static void setIceMelt(boolean melt) {
		set(KEY_SPAWN_PROT_ICE_MELT, melt);
	}
	
	public static boolean shouldWaterFreeze() {
		return get(KEY_SPAWN_PROT_WATER_FREEZE);
	}
	
	public static void setWaterFreeze(boolean freeze) {
		set(KEY_SPAWN_PROT_WATER_FREEZE, freeze);
	}
	
	public static boolean shouldSnowMelt() {
		return get(KEY_SPAWN_PROT_SNOW_MELT);
	}
	
	public static void setSnowMelt(boolean melt) {
		set(KEY_SPAWN_PROT_SNOW_MELT, melt);
	}

	public static boolean shouldSnowFall() {
		return get(KEY_SPAWN_PROT_SNOW_FALL);
	}
	
	public static void setSnowFall(boolean fall) {
		set(KEY_SPAWN_PROT_SNOW_FALL, fall);
	}

	public static boolean allowArmorStandInteraction() {
		return get(KEY_SPAWN_PROT_ALLOW_ARMOR_STAND_INTERACTION);
	}
	
	public static void setArmorStandInteraction(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_ARMOR_STAND_INTERACTION, allow);
	}

	public static boolean allowExplosions() {
		return get(KEY_SPAWN_PROT_ALLOW_EXPLOSIONS);
	}
	
	public static void setExplosions(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_EXPLOSIONS, allow);
	}

	public static boolean allowMobGriefing() {
		return get(KEY_SPAWN_PROT_ALLOW_MOB_GRIEFING);
	}
	
	public static void setMobGriefing(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_MOB_GRIEFING, allow);
	}

	public static boolean allowItemFrameInteraction() {
		return get(KEY_SPAWN_PROT_ALLOW_ITEM_FRAME_INTERACTION);
	}
	
	public static void setItemFrameInteraction(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_ITEM_FRAME_INTERACTION, allow);
	}

	public static boolean allowSpawnMonsters() {
		return get(KEY_SPAWN_PROT_ALLOW_MOB_SPAWN);
	}
	
	public static void setSpawnMonsters(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_MOB_SPAWN, allow);
	}

	public static boolean allowBreakBlocks() {
		return get(KEY_SPAWN_PROT_ALLOW_BLOCK_BREAK);
	}
	
	public static void setBreakBlocks(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_BLOCK_BREAK, allow);
	}
	
	public static boolean allowPlaceBlocks() {
		return get(KEY_SPAWN_PROT_ALLOW_BLOCK_PLACE);
	}
	
	public static void setPlaceBlocks(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_BLOCK_PLACE, allow);
	}

	public static boolean allowBucketInteraction() {
		return get(KEY_SPAWN_PROT_ALLOW_BUCKET_INTERACTION);
	}
	
	public static void setBucketInteraction(boolean allow) {
		set(KEY_SPAWN_PROT_ALLOW_BUCKET_INTERACTION, allow);
	}
	
	public static List<String> getSpawnProtectedDimensions() {
		return get(KEY_SPAWN_PROT_DIMENSIONS);
	}
	
	public static void setSpawnProtectedDimensions(List<String> dimensions) {
		set(KEY_SPAWN_PROT_DIMENSIONS, dimensions);
	}

}
