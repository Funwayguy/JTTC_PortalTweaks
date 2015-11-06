package portaltweak.handlers;

import java.lang.reflect.Field;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class RespawnHandler
{
	public static void RespawnPlayerInDimension(EntityPlayer player, int dimension)
	{
    	if(player instanceof EntityPlayerMP)
    	{
    		RespawnPlayerMP((EntityPlayerMP)player, dimension);
    		return;
    	}
    	
        if (!player.worldObj.isRemote && !player.isDead)
        {
        	player.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.getServer();
            int j = player.dimension;
            WorldServer worldserver = minecraftserver.worldServerForDimension(j);
            WorldServer worldserver1 = minecraftserver.worldServerForDimension(dimension);
            player.dimension = dimension;

            player.worldObj.removeEntity(player);
            player.isDead = false;
            player.worldObj.theProfiler.startSection("reposition");
            //minecraftserver.getConfigurationManager().transferEntityToWorld(player, j, worldserver, worldserver1);
            transferEntityToWorld(player, j, worldserver, worldserver1);
            player.worldObj.theProfiler.endStartSection("reloading");
            Entity entity = EntityList.createEntityByName(EntityList.getEntityString(player), worldserver1);

            if (entity != null)
            {
                entity.copyDataFrom(player, true);
                
                ChunkCoordinates chunkcoordinates = worldserver1.getSpawnPoint();
                
                if(player.getBedLocation(dimension) != null)
                {
                	chunkcoordinates = player.getBedLocation(dimension);
                }
                
                entity.setLocationAndAngles((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, entity.rotationYaw, entity.rotationPitch);

                worldserver1.spawnEntityInWorld(entity);
            }

            player.isDead = true;
            player.worldObj.theProfiler.endSection();
            worldserver.resetUpdateEntityTick();
            worldserver1.resetUpdateEntityTick();
            player.worldObj.theProfiler.endSection();
        }
	}
	
	private static void RespawnPlayerMP(EntityPlayerMP player, int dimension)
	{
		ChunkCoordinates chunkcoordinates;
		
		try
		{
			chunkcoordinates = player.mcServer.worldServerForDimension(dimension).getEntrancePortalLocation();
		} catch(Exception e)
		{
			return;
		}
        
        if(player.getBedLocation(dimension) != null)
        {
        	chunkcoordinates = player.getBedLocation(dimension);
        }

        if (chunkcoordinates != null)
        {
        	player.playerNetServerHandler.setPlayerLocation((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ, 0.0F, 0.0F);
        }

        transferPlayerToDimension(player, dimension);
        resetPlayerMPStats(player);
	}

    public static void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2)
    {
        transferPlayerToDimension(par1EntityPlayerMP, par2, par1EntityPlayerMP.mcServer.worldServerForDimension(par2).getDefaultTeleporter());
    }

    private static void transferPlayerToDimension(EntityPlayerMP par1EntityPlayerMP, int par2, Teleporter teleporter)
    {
    	ServerConfigurationManager configManager = par1EntityPlayerMP.mcServer.getConfigurationManager();
        int j = par1EntityPlayerMP.dimension;
        WorldServer worldserver = par1EntityPlayerMP.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.dimension = par2;
        WorldServer worldserver1 = par1EntityPlayerMP.mcServer.worldServerForDimension(par1EntityPlayerMP.dimension);
        par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S07PacketRespawn(par1EntityPlayerMP.dimension, par1EntityPlayerMP.worldObj.difficultySetting, par1EntityPlayerMP.worldObj.getWorldInfo().getTerrainType(), par1EntityPlayerMP.theItemInWorldManager.getGameType()));
        worldserver.removePlayerEntityDangerously(par1EntityPlayerMP);
        par1EntityPlayerMP.isDead = false;
        //configManager.transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter);
        transferEntityToWorld(par1EntityPlayerMP, j, worldserver, worldserver1, teleporter);
        configManager.func_72375_a(par1EntityPlayerMP, worldserver);
        par1EntityPlayerMP.playerNetServerHandler.setPlayerLocation(par1EntityPlayerMP.posX, par1EntityPlayerMP.posY, par1EntityPlayerMP.posZ, par1EntityPlayerMP.rotationYaw, par1EntityPlayerMP.rotationPitch);
        par1EntityPlayerMP.theItemInWorldManager.setWorld(worldserver1);
        configManager.updateTimeAndWeatherForPlayer(par1EntityPlayerMP, worldserver1);
        configManager.syncPlayerInventory(par1EntityPlayerMP);
        Iterator<?> iterator = par1EntityPlayerMP.getActivePotionEffects().iterator();

        while (iterator.hasNext())
        {
            PotionEffect potioneffect = (PotionEffect)iterator.next();
            par1EntityPlayerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(par1EntityPlayerMP.getEntityId(), potioneffect));
        }

        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(par1EntityPlayerMP, j, par2);
    }

    /**
     * Transfers an entity from a world to another world.
     */
    private static void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer)
    {
        transferEntityToWorld(par1Entity, par2, par3WorldServer, par4WorldServer, par4WorldServer.getDefaultTeleporter());
    }
	
	private static void transferEntityToWorld(Entity par1Entity, int par2, WorldServer par3WorldServer, WorldServer par4WorldServer, Teleporter teleporter)
    {
        double d3 = par1Entity.posX;
        double d4 = par1Entity.posY;
        double d5 = par1Entity.posZ;
        par3WorldServer.theProfiler.startSection("moving");
        

        d3 = (double)MathHelper.clamp_int((int)d3, -29999872, 29999872);
        d5 = (double)MathHelper.clamp_int((int)d5, -29999872, 29999872);

        if (par1Entity.isEntityAlive())
        {
            par4WorldServer.spawnEntityInWorld(par1Entity);
            par1Entity.setLocationAndAngles(d3, MathHelper.floor_double(d4), d5, par1Entity.rotationYaw, par1Entity.rotationPitch);
            par4WorldServer.updateEntityWithOptionalForce(par1Entity, false);
        }

        par3WorldServer.theProfiler.endSection();

        par1Entity.setWorld(par4WorldServer);
    }
	
	public static void resetPlayerMPStats(EntityPlayerMP player)
	{
		Field fXP = null;
		Field fHP = null;
		Field fFD = null;
		try
		{
			fXP = EntityPlayerMP.class.getDeclaredField("lastExperience");
			fHP = EntityPlayerMP.class.getDeclaredField("lastHealth");
			fFD = EntityPlayerMP.class.getDeclaredField("lastFoodLevel");
		} catch(Exception e)
		{
			try
			{
				fXP = EntityPlayerMP.class.getDeclaredField("field_71144_ck");
				fHP = EntityPlayerMP.class.getDeclaredField("field_71149_ch");
				fFD = EntityPlayerMP.class.getDeclaredField("field_71146_ci");
			} catch(Exception e1)
			{
				e.printStackTrace();
				e1.printStackTrace();
				return;
			}
		}
		
		fXP.setAccessible(true);
		fHP.setAccessible(true);
		fFD.setAccessible(true);
		
		try
		{
			fXP.setInt(player, -1);
			fHP.setFloat(player, -1.0F);
			fFD.setInt(player, -1);
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		return;
	}
}
