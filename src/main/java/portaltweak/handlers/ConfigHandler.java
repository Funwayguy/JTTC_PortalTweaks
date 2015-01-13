package portaltweak.handlers;

import java.io.File;
import portaltweak.core.JTTC_Settings;
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
		
		config.save();
	}
}
