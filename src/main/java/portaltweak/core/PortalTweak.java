package portaltweak.core;

import org.apache.logging.log4j.Logger;
import portaltweak.core.proxies.CommonProxy;
import portaltweak.handlers.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = PortalTweak.MODID, version = PortalTweak.VERSION, name = PortalTweak.NAME)
public class PortalTweak
{
    public static final String MODID = "portaltweak";
    public static final String VERSION = "PT_VER_KEY";
    public static final String NAME = "JTTC Portal Tweak";
    public static final String PROXY = "portaltweak.core.proxies";
	
	@Instance(MODID)
	public static PortalTweak instance;
	
	@SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	ConfigHandler.ReadEMConfigs();
    	ConfigHandler.ReadEUConfigs();
    	ConfigHandler.LoadNativeConfig(event.getSuggestedConfigurationFile());
    	proxy.registerHandlers();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
