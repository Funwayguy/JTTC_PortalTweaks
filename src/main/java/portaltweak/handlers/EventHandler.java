package portaltweak.handlers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import portaltweak.core.JTTC_Settings;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

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
		
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			int respawnDim = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getInteger("Death_Dimension");
			
			if(respawnDim != 0 && respawnDim != player.worldObj.provider.dimensionId)
			{
				RespawnHandler.RespawnPlayerInDimension(player, respawnDim);
			}
		}
	}
	
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event)
	{
		if(event.player.isEntityAlive())
		{
			NBTTagCompound pTags = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			pTags.setInteger("Death_Dimension", event.toDim);
			event.player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, pTags);
			System.out.println("Death dimension set to " + event.toDim);
			
			ChunkCoordinates coords = event.player.getBedLocation(event.player.worldObj.provider.dimensionId);
			
			if(coords == null)
			{
				event.player.setSpawnChunk(event.player.getPlayerCoordinates(), true, event.player.worldObj.provider.dimensionId);
				System.out.println("Set spawnpoint");
			}
		}
	}
}
