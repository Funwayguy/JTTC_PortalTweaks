package portaltweak.handlers;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;
import portaltweak.core.JTTC_Settings;
import portaltweak.core.PortalTweak;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class EventHandler
{
	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof IMob && event.entity instanceof EntityLivingBase && !event.world.isRemote)
		{
			EntityLivingBase entityLiving = (EntityLivingBase)event.entity;
			DimSettings dimSet = JTTC_Settings.dimSettings.get(event.world.provider.dimensionId);
			
			if(entityLiving.getEntityData().getBoolean("JTTC_TWEAKED"))
			{
				return;
			} else
			{
				entityLiving.getEntityData().setBoolean("JTTC_TWEAKED", true);
			}
			
			if(dimSet != null)
			{
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("JTTC_TWEAK_1", dimSet.hpMult + (event.world.provider.dimensionId == JTTC_Settings.deepDarkID? 1F : 0F), 1));
					entityLiving.setHealth(entityLiving.getMaxHealth());
				}
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.movementSpeed) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(new AttributeModifier("JTTC_TWEAK_2", dimSet.spdMult, 1));
				}
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.attackDamage) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(new AttributeModifier("JTTC_TWEAK_3", dimSet.dmgMult + (event.world.provider.dimensionId == JTTC_Settings.deepDarkID? 1F : 0F), 1));
				}
				if(entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance) != null)
				{
					entityLiving.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("JTTC_TWEAK_4", dimSet.dmgMult, 1));
				}
			}
		}
	}
	
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
			
			if(block instanceof BlockBed && !event.world.isRemote)
			{
				event.entityPlayer.addChatComponentMessage(new ChatComponentText("Spawn Set"));
				event.entityPlayer.setSpawnChunk(event.entityPlayer.getPlayerCoordinates(), true, event.entityPlayer.dimension);
				event.setCanceled(true);
			}
			
			if(event.entityPlayer.capabilities.isCreativeMode)
			{
				return;
			}
			
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
	public void onEntityInteract(EntityInteractEvent event)
	{
		if(event.target instanceof EntityLiving)
		{
			if(((EntityLiving)event.target).getCustomNameTag().equalsIgnoreCase("Herobrine")) // Disables people's ability to capture Herobrine by hand
			{
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onLiving(LivingUpdateEvent event)
	{
		// Invincible exploit prevention
		PotionEffect effect = event.entityLiving.getActivePotionEffect(Potion.resistance);
		
		if(effect != null && effect.getDuration() > 18000)
		{
			event.entityLiving.removePotionEffect(Potion.resistance.id);
		}
		
		effect = event.entityLiving.getActivePotionEffect(Potion.regeneration);
		
		if(effect != null && effect.getDuration() > 18000)
		{
			event.entityLiving.removePotionEffect(Potion.regeneration.id);
		}
		
		effect = event.entityLiving.getActivePotionEffect(Potion.heal);
		
		if(effect != null && effect.getDuration() > 20)
		{
			event.entityLiving.removePotionEffect(Potion.heal.id);
		}
		
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
				event.entityLiving.timeUntilPortal = event.entityLiving.getPortalCooldown();
				RespawnHandler.RespawnPlayerInDimension(player, respawnDim);
			} else if(!player.capabilities.isCreativeMode && player.posY >= 255) // Nerf living above any dimension
			{
				player.attackEntityFrom(DamageSource.outOfWorld, 2F);
			} else if(player.getBedLocation(player.dimension) != null && !player.isSpawnForced(player.dimension)) // Force player spawns even on broken beds. Prevents re-spawning on top of the world
			{
				player.setSpawnChunk(player.getBedLocation(player.dimension), true, player.dimension);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingAttacked(LivingHurtEvent event)
	{
		String ID = EntityList.getEntityString(event.entityLiving);
		
		if(JTTC_Settings.dmgLimitedMobs.contains(ID))
		{
			event.ammount = Math.min(JTTC_Settings.dmgLimit, event.ammount);
		}
	}
	
	@SubscribeEvent
	public void onPlayerCopy(PlayerEvent.Clone event)
	{
		if(event.original.dimension == 0 && event.original.getHealth() > 0 && event.entityPlayer.dimension == 0)
		{
			NBTTagCompound pTags = event.entityPlayer.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			pTags.setInteger("Death_Dimension", 0);
			event.entityPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, pTags);
		}
	}
	
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event)
	{
		if(event.player.getHealth() > 0)
		{
			NBTTagCompound pTags = event.player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			pTags.setInteger("Death_Dimension", event.toDim);
			event.player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, pTags);
			
			ChunkCoordinates coords = event.player.getBedLocation(event.player.worldObj.provider.dimensionId);
			
			if(coords == null)
			{
				event.player.setSpawnChunk(event.player.getPlayerCoordinates(), true, event.player.worldObj.provider.dimensionId);
			}
			
			@SuppressWarnings("unchecked")
			List<IMob> killList = event.player.worldObj.getEntitiesWithinAABB(IMob.class, event.player.boundingBox.expand(JTTC_Settings.spawnKillRange, JTTC_Settings.spawnKillRange, JTTC_Settings.spawnKillRange));
			
			for(IMob mob : killList)
			{
				if(mob instanceof Entity)
				{
					((Entity)mob).setDead();
				}
			}
			
			if(JTTC_Settings.nightVision > 0 && !event.player.worldObj.isRemote)
			{
				event.player.addPotionEffect(new PotionEffect(Potion.nightVision.id, JTTC_Settings.nightVision * 20));
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		if(!(event.entityLiving instanceof EntityPlayer) && (event.y > 255 || event.y < 0))
		{
			event.setResult(Result.DENY);
			return;
		} else if(JTTC_Settings.safeOverworld && event.entityLiving instanceof IMob && event.world.provider.dimensionId == 0)
		{
			event.setResult(Result.DENY);
			return;
		}
	}
	
	ArrayList<World> convertedWorlds = new ArrayList<World>();
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(!JTTC_Settings.reRollSeed || event.world.isRemote || convertedWorlds.contains(event.world))
		{
			return;
		}
		
		File file = MinecraftServer.getServer().getFile((PortalTweak.proxy.isClient()? "saves/" : "") + MinecraftServer.getServer().getFolderName() + "/JTTC_Seed.txt");
		
		if(!file.exists()) // If the seed hasn't previously been set then we can re-roll the seed
		{
			long seed = new Random().nextLong();
			
			for(WorldServer world : MinecraftServer.getServer().worldServers)
			{
				convertedWorlds.add(world);
				ObfuscationReflectionHelper.setPrivateValue(WorldInfo.class, world.getWorldInfo(), seed, "field_76100_a", "randomSeed");
				world.provider.registerWorld(world);
			}
			
			FileWriter fw = null;
			
			try
			{
				file.createNewFile();
				fw = new FileWriter(file);
				fw.write("" + seed);
			} catch(Exception e)
			{
				e.printStackTrace();
			} finally
			{
				if(fw != null)
				{
					try
					{
						fw.close();
					} catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		} else
		{
			convertedWorlds.add(event.world);
		}
	}
}
