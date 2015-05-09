package portaltweak;

import java.util.Random;
import portaltweak.core.JTTC_Settings;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class WorldGenDeepCaves implements IWorldGenerator
{
	boolean alreadyGenerating = false;
	MapGenModifiedCaves deepDarkCaveGen;
	MapGenModifiedRavine deepDarkRavineGen;
	
	public WorldGenDeepCaves()
	{
		deepDarkCaveGen = new MapGenModifiedCaves();
		deepDarkRavineGen = new MapGenModifiedRavine();
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(world.isRemote)
		{
			return;
		}
		
		if(world.provider.dimensionId == JTTC_Settings.deepDarkID && JTTC_Settings.deepDarkCaves > 0 && !alreadyGenerating)
		{
			alreadyGenerating = true;
			
			if(deepDarkCaveGen == null)
			{
				deepDarkCaveGen = new MapGenModifiedCaves();
			}
			
			deepDarkCaveGen.func_151539_a(chunkGenerator, world, chunkX, chunkZ, new Block[]{});
			
			if(deepDarkRavineGen == null)
			{
				deepDarkRavineGen = new MapGenModifiedRavine();
			}
			
			deepDarkRavineGen.func_151539_a(chunkGenerator, world, chunkX, chunkZ, new Block[]{});
			
			alreadyGenerating = false;
		} else if(world.provider.dimensionId == 1 && JTTC_Settings.coreLavaY > 0)
		{
			ObfuscationReflectionHelper.setPrivateValue(Block.class, Blocks.lava, Material.water, "field_149764_J", "blockMaterial"); // If this isn't changed, the liquid will start calling block updates on neighboring chunks
			
			for(int x = 0; x < 16; x++)
			{
				for(int y = 0; y < JTTC_Settings.coreLavaY; y++)
				{
					for(int z = 0; z < 16; z++)
					{
						if(world.isAirBlock(x + (chunkX * 16), y, z + (chunkZ * 16)))
						{
							world.setBlock(x + (chunkX * 16), y, z + (chunkZ * 16), Blocks.lava, 0, 2);
						}
					}
				}
			}
			
			ObfuscationReflectionHelper.setPrivateValue(Block.class, Blocks.lava, Material.lava, "field_149764_J", "blockMaterial"); // Change back now that we're done placing all the lava
		}
	}
}
