package portaltweak.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import portaltweak.core.proxies.CommonProxy;
import portaltweak.handlers.ConfigHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.IEventListener;

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
    	ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = ObfuscationReflectionHelper.getPrivateValue(cpw.mods.fml.common.eventhandler.EventBus.class, MinecraftForge.EVENT_BUS, "listeners");
    	for(Object obj : listeners.keySet())
    	{
    		if(obj.getClass().getCanonicalName().equalsIgnoreCase("com.rwtema.extrautils.worldgen.Underdark.EventHandlerUnderdark"))
    		{
    			MinecraftForge.EVENT_BUS.unregister(obj);
    			logger.log(Level.INFO, "Successfully unregistered ExtraUtils Deep Dark listener");
    			break;
    		}
    	}
    }
}
