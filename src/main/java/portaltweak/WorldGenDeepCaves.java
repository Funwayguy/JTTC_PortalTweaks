package portaltweak;

import java.util.Random;
import portaltweak.core.JTTC_Settings;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

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
		}
	}
}
