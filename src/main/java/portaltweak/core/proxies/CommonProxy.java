package portaltweak.core.proxies;

import net.minecraftforge.common.MinecraftForge;
import portaltweak.handlers.EventHandler;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
}
