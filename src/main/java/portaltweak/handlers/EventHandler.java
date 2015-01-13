package portaltweak.handlers;

import java.lang.reflect.Method;
import portaltweak.core.JTTC_Settings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;


public class EventHandler
{
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event)
	{
		if(event.action == Action.RIGHT_CLICK_BLOCK)
		{
			ItemStack stack = event.entityPlayer.getHeldItem();
			int dimension = event.entityPlayer.worldObj.provider.dimensionId;
			Block block = event.world.getBlock(event.x, event.y, event.z);
			int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
			Item itemBlock = Item.getItemFromBlock(block);
			
			if(block == null || block == Blocks.air)
			{
				return;
			}
			
			if((Block.blockRegistry.getNameForObject(block).equals(JTTC_Settings.escapeBlock) || (itemBlock != null && Item.itemRegistry.getNameForObject(itemBlock).equals(JTTC_Settings.escapeBlock))) && (meta == JTTC_Settings.escapeMeta || JTTC_Settings.escapeMeta < 0) && dimension == -1)
			{
				if(!event.world.isRemote)
				{
					for(int i = event.x - 2; i <= event.x + 2; i++)
					{
						for(int k = event.z - 2; k <= event.z + 2; k++)
						{
							event.world.setBlock(i, event.y, k, Blocks.bedrock, 0, 2); // Portal frame
						}
					}
					
					for(int i = event.x - 2; i <= event.x + 2; i++)
					{
						for(int k = event.z - 2; k <= event.z + 2; k++)
						{
							event.world.setBlock(i, event.y - 1, k, Blocks.bedrock, 0, 2); // Portal base
						}
					}
					
					BlockEndPortal.field_149948_a = true;
					for(int i = event.x - 1; i <= event.x + 1; i++)
					{
						for(int k = event.z - 1; k <= event.z + 1; k++)
						{
							event.world.setBlock(i, event.y, k, Blocks.end_portal, 0, 2); // Changed the escape block to make a portal when right clicked
						}
					}
					BlockEndPortal.field_149948_a = false;
				}
				//event.setCanceled(true);
				return;
			} else if(stack != null && stack.getItem() == Items.flint_and_steel && block == Blocks.obsidian && dimension != JTTC_Settings.deepDarkID)
			{
				event.setCanceled(true);
				return;
			} else if(Block.blockRegistry.getNameForObject(block).equals("enviromine:elevator") && dimension != 0)
			{
				event.setCanceled(true);
				return;
			} else if(Block.blockRegistry.getNameForObject(block).equals("ExtraUtilities:dark_portal") && dimension != JTTC_Settings.caveDimID)
			{
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onLiving(LivingUpdateEvent event)
	{
		ItemStack stack = event.entityLiving.getHeldItem();
		Item key = (Item)Item.itemRegistry.getObject(JTTC_Settings.portalKey);
		boolean hasKey = stack != null && key != null && stack.getItem() == key && (JTTC_Settings.keyMeta < 0 || stack.getItemDamage() == JTTC_Settings.keyMeta);
		
		if((event.entityLiving.worldObj.provider.dimensionId != JTTC_Settings.deepDarkID || !hasKey) && event.entityLiving.isInsideOfMaterial(Material.portal))
		{
			event.entityLiving.timeUntilPortal = event.entityLiving.getPortalCooldown();
		}
		
		if(event.entityLiving instanceof EntityPlayer && !event.entityLiving.isEntityAlive())
		{
			event.entityLiving.getEntityData().setInteger("Death_Dimension", event.entityLiving.worldObj.provider.dimensionId);
		} else if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			ChunkCoordinates coords = player.getBedLocation(event.entityLiving.worldObj.provider.dimensionId);
			
			if(coords == null)
			{
				player.setSpawnChunk(player.playerLocation, true, event.entityLiving.worldObj.provider.dimensionId);
			}
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerRespawnEvent event)
	{
		if(!event.player.getEntityData().hasKey("Death_Dimension") || !(event.player instanceof EntityPlayerMP))
		{
			return;
		}
		
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		MinecraftServer server = MinecraftServer.getServer();
		
		int respawnDim = player.getEntityData().getInteger("Death_Dimension");
		
        World world = server.worldServerForDimension(respawnDim);
        
        if (world == null || world == player.worldObj)
        {
            return;
        }

        player.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(player);
        player.getServerForPlayer().getEntityTracker().removeEntityFromAllTrackingPlayers(player);
        player.getServerForPlayer().getPlayerManager().removePlayer(player);
        server.getConfigurationManager().playerEntityList.remove(player);
        server.worldServerForDimension(player.dimension).removePlayerEntityDangerously(player);
        ChunkCoordinates chunkcoordinates = player.getBedLocation(respawnDim);
        boolean flag1 = player.isSpawnForced(respawnDim);
        player.dimension = respawnDim;
        Object object;

        if (server.isDemo())
        {
            object = new DemoWorldManager(server.worldServerForDimension(player.dimension));
        }
        else
        {
            object = new ItemInWorldManager(server.worldServerForDimension(player.dimension));
        }

        EntityPlayerMP entityplayermp1 = new EntityPlayerMP(server, server.worldServerForDimension(player.dimension), player.getGameProfile(), (ItemInWorldManager)object);
        entityplayermp1.playerNetServerHandler = player.playerNetServerHandler;
        entityplayermp1.clonePlayer(player, false);
        entityplayermp1.dimension = respawnDim;
        entityplayermp1.setEntityId(player.getEntityId());
        WorldServer worldserver = server.worldServerForDimension(player.dimension);
        
        try
        {
        	Method meth = ServerConfigurationManager.class.getDeclaredMethod("func_72381_a", EntityPlayerMP.class, EntityPlayerMP.class, World.class);
        	meth.setAccessible(true);
        	meth.invoke(server.getConfigurationManager(), entityplayermp1, player, worldserver);
        	//server.getConfigurationManager().func_72381_a(entityplayermp1, player, worldserver);
        } catch(Exception e)
        {
        	e.printStackTrace();
        	return;
        }
        ChunkCoordinates chunkcoordinates1;

        if (chunkcoordinates != null)
        {
            chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(server.worldServerForDimension(player.dimension), chunkcoordinates, flag1);

            if (chunkcoordinates1 != null)
            {
                entityplayermp1.setLocationAndAngles((double)((float)chunkcoordinates1.posX + 0.5F), (double)((float)chunkcoordinates1.posY + 0.1F), (double)((float)chunkcoordinates1.posZ + 0.5F), 0.0F, 0.0F);
                entityplayermp1.setSpawnChunk(chunkcoordinates, flag1);
            }
            else
            {
                entityplayermp1.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        worldserver.theChunkProviderServer.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);

        while (!worldserver.getCollidingBoundingBoxes(entityplayermp1, entityplayermp1.boundingBox).isEmpty())
        {
            entityplayermp1.setPosition(entityplayermp1.posX, entityplayermp1.posY + 1.0D, entityplayermp1.posZ);
        }

        entityplayermp1.playerNetServerHandler.sendPacket(new S07PacketRespawn(entityplayermp1.dimension, entityplayermp1.worldObj.difficultySetting, entityplayermp1.worldObj.getWorldInfo().getTerrainType(), entityplayermp1.theItemInWorldManager.getGameType()));
        chunkcoordinates1 = worldserver.getSpawnPoint();
        entityplayermp1.playerNetServerHandler.setPlayerLocation(entityplayermp1.posX, entityplayermp1.posY, entityplayermp1.posZ, entityplayermp1.rotationYaw, entityplayermp1.rotationPitch);
        entityplayermp1.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(chunkcoordinates1.posX, chunkcoordinates1.posY, chunkcoordinates1.posZ));
        entityplayermp1.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityplayermp1.experience, entityplayermp1.experienceTotal, entityplayermp1.experienceLevel));
        server.getConfigurationManager().updateTimeAndWeatherForPlayer(entityplayermp1, worldserver);
        worldserver.getPlayerManager().addPlayer(entityplayermp1);
        worldserver.spawnEntityInWorld(entityplayermp1);
        server.getConfigurationManager().playerEntityList.add(entityplayermp1);
        entityplayermp1.addSelfToInternalCraftingInventory();
        entityplayermp1.setHealth(entityplayermp1.getHealth());
	}
}
