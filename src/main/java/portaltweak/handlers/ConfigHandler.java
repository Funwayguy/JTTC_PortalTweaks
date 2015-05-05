package portaltweak.handlers;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import portaltweak.core.JTTC_Settings;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
	public static void ReadEUConfigs()
	{
		File file = new File("config/ExtraUtilities.cfg");
		
		if(!file.exists())
		{
			return;
		}
		
		Configuration config = new Configuration(file, true);
		
		config.load();
		
		JTTC_Settings.deepDarkID = config.get("options", "deepDarkDimensionID", -100).getInt(-100);
		
		config.save();
	}
	
	public static void ReadEMConfigs()
	{
		File file = new File("config/enviromine/CaveDimension.cfg");
		
		if(!file.exists())
		{
			return;
		}
		
		Configuration config = new Configuration(file, true);
		
		config.load();
		
		JTTC_Settings.caveDimID = config.get("Main", "Dimension ID", -2).getInt(-2);
		
		config.save();
	}

	public static void LoadNativeConfig(File file)
	{
		Configuration config = new Configuration(file, true);
		
		config.load();
		
		JTTC_Settings.escapeBlock = config.get("Main", "Escape Block", "ExtraUtilities:dark_portal", "This is the block that the user must right click to return to the overworld.").getString();
		JTTC_Settings.escapeMeta = config.get("Main", "Escape Metadata", 0, "The metadata of the escape block").getInt(0);
		JTTC_Settings.portalKey = config.get("Main", "Nether Key Item", "minecraft:ender_eye", "Item that must be in the players hand for nether portal to work (set blank to disable)").getString();
		JTTC_Settings.keyMeta = config.get("Main", "Key Metadata", 1, "The metadata/damage of the key item").getInt();
		JTTC_Settings.safeOverworld = config.getBoolean("Safe Overworld", "Main", true, "Prevents mobs spawning in the overworld (JTTC starting room)");
		JTTC_Settings.reRollSeed = config.getBoolean("Re-roll Seed", "Main", true, "Re-rolls the world seed (May cause generation issues. Use with caution)");
		JTTC_Settings.spawnKillRange = config.getInt("Spawn Kill Range", "Main", 128, 0, 1024, "The range of mobs that will be deleted upon entering a dimension");
		JTTC_Settings.nightVision = config.getInt("Spawn Nightvison", "Main", 45, 0, 600, "How many seconds the player will have night vision upon entering a dimension");
		JTTC_Settings.deepDarkCaves = config.getInt("Deep Dark Caves", "Main", 50, 0, 100, "Amount of extra caves/ravines in the deep dark");
		
		Set<ConfigCategory> cats = config.getCategory("Dimension Tweaks").getChildren();
		
		if(cats.size() <= 0)
		{
			config.get("Dimension Tweaks.Overworld", "01.Dimension ID", 0).getInt(0);
			config.get("Dimension Tweaks.Overworld", "02.Health Mult", 1.0D).getDouble(1.0D);
			config.get("Dimension Tweaks.Overworld", "03.Damage Mult", 1.0D).getDouble(1.0D);
			config.get("Dimension Tweaks.Overworld", "04.Speed Mult", 1.0D).getDouble(1.0D);
			config.get("Dimension Tweaks.Overworld", "05.Knockback Resistance Mult", 1.0D).getDouble(1.0D);
			cats = config.getCategory("Dimension Tweaks").getChildren();
		}
		
		Iterator<ConfigCategory> iterator = cats.iterator();
		
		while(iterator.hasNext())
		{
			ConfigCategory cat = iterator.next();
			if(cat.getChildren().size() <= 0)
			{
				int dimID = config.get(cat.getQualifiedName(), "01.Dimension ID", 0).getInt(0);
				double hpMult = config.get(cat.getQualifiedName(), "02.Health Mult", 1.0D).getDouble(1.0D);
				double dmgMult = config.get(cat.getQualifiedName(), "03.Damage Mult", 1.0D).getDouble(1.0D);
				double spdMult = config.get(cat.getQualifiedName(), "04.Speed Mult", 1.0D).getDouble(1.0D);
				double knockResist = config.get(cat.getQualifiedName(), "05.Knockback Resistance Mult", 1.0D).getDouble(1.0D);
				
				DimSettings dimSet = new DimSettings(hpMult, dmgMult, spdMult, knockResist);
				JTTC_Settings.dimSettings.put(dimID, dimSet);
			}
		}
		
		config.save();
	}
}
