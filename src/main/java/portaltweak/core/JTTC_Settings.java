package portaltweak.core;

import java.util.ArrayList;
import java.util.HashMap;
import portaltweak.handlers.DimSettings;

/**
 * A container for all the configurable settings in the mod
 */
public class JTTC_Settings
{
	public static int deepDarkID = -100;
	public static int caveDimID = -2;
	
	public static String escapeBlock = "ExtraUtilities:dark_portal";
	public static int escapeMeta = 1;
	
	public static String portalKey = "minecraft:ender_eye";
	public static int keyMeta = 0;
	
	public static boolean safeOverworld = true;
	public static boolean reRollSeed = true;
	public static int spawnKillRange = 128;
	public static int deepDarkCaves = 50;
	public static int nightVision = 45;
	public static int coreLavaY = 32;
	public static int dmgLimit = 20;
	public static ArrayList<String> dmgLimitedMobs = new ArrayList<String>();
	public static ArrayList<Integer> bannedEnch = new ArrayList<Integer>();
	
	public static HashMap<Integer, DimSettings> dimSettings = new HashMap<Integer, DimSettings>();
}
