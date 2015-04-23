package portaltweak;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import portaltweak.core.JTTC_Settings;

public class MapGenModifiedRavine extends MapGenBase
{
	private float[] field_75046_d = new float[1024];
	
	protected void genRavine(World world, long seed, int p_151540_3_, int p_151540_4_, double posX, double posY, double posZ, float p_151540_12_, float p_151540_13_, float p_151540_14_, int p_151540_15_, int p_151540_16_, double p_151540_17_)
	{
		Random random = new Random(seed);
		double d4 = (double)(p_151540_3_ * 16 + 8);
		double d5 = (double)(p_151540_4_ * 16 + 8);
		float f3 = 0.0F;
		float f4 = 0.0F;
		
		if(p_151540_16_ <= 0)
		{
			int j1 = this.range * 16 - 16;
			p_151540_16_ = j1 - random.nextInt(j1 / 4);
		}
		
		boolean flag1 = false;
		
		if(p_151540_15_ == -1)
		{
			p_151540_15_ = p_151540_16_ / 2;
			flag1 = true;
		}
		
		float f5 = 1.0F;
		
		for(int k1 = 0; k1 < 256; ++k1)
		{
			if(k1 == 0 || random.nextInt(3) == 0)
			{
				f5 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
			}
			
			this.field_75046_d[k1] = f5 * f5;
		}
		
		for(; p_151540_15_ < p_151540_16_; ++p_151540_15_)
		{
			double d12 = 1.5D + (double)(MathHelper.sin((float)p_151540_15_ * (float)Math.PI / (float)p_151540_16_) * p_151540_12_ * 1.0F);
			double d6 = d12 * p_151540_17_;
			d12 *= (double)random.nextFloat() * 0.25D + 0.75D;
			d6 *= (double)random.nextFloat() * 0.25D + 0.75D;
			float f6 = MathHelper.cos(p_151540_14_);
			float f7 = MathHelper.sin(p_151540_14_);
			posX += (double)(MathHelper.cos(p_151540_13_) * f6);
			posY += (double)f7;
			posZ += (double)(MathHelper.sin(p_151540_13_) * f6);
			p_151540_14_ *= 0.7F;
			p_151540_14_ += f4 * 0.05F;
			p_151540_13_ += f3 * 0.05F;
			f4 *= 0.8F;
			f3 *= 0.5F;
			f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			
			if(flag1 || random.nextInt(4) != 0)
			{
				double d7 = posX - d4;
				double d8 = posZ - d5;
				double d9 = (double)(p_151540_16_ - p_151540_15_);
				double d10 = (double)(p_151540_12_ + 2.0F + 16.0F);
				
				if(d7 * d7 + d8 * d8 - d9 * d9 > d10 * d10)
				{
					return;
				}
				
				if(posX >= d4 - 16.0D - d12 * 2.0D && posZ >= d5 - 16.0D - d12 * 2.0D && posX <= d4 + 16.0D + d12 * 2.0D && posZ <= d5 + 16.0D + d12 * 2.0D)
				{
					int i4 = MathHelper.floor_double(posX - d12) - p_151540_3_ * 16 - 1;
					int l1 = MathHelper.floor_double(posX + d12) - p_151540_3_ * 16 + 1;
					int j4 = MathHelper.floor_double(posY - d6) - 1;
					int i2 = MathHelper.floor_double(posY + d6) + 1;
					int k4 = MathHelper.floor_double(posZ - d12) - p_151540_4_ * 16 - 1;
					int j2 = MathHelper.floor_double(posZ + d12) - p_151540_4_ * 16 + 1;
					
					if(i4 < 0)
					{
						i4 = 0;
					}
					
					if(l1 > 16)
					{
						l1 = 16;
					}
					
					if(j4 < 1)
					{
						j4 = 1;
					}
					
					if(i2 > 248)
					{
						i2 = 248;
					}
					
					if(k4 < 0)
					{
						k4 = 0;
					}
					
					if(j2 > 16)
					{
						j2 = 16;
					}
					
					boolean flag2 = false;
					int k2;
					int j3;
					
					for(k2 = i4; !flag2 && k2 < l1; ++k2)
					{
						for(int l2 = k4; !flag2 && l2 < j2; ++l2)
						{
							for(int i3 = i2 + 1; !flag2 && i3 >= j4 - 1; --i3)
							{
								j3 = (k2 * 16 + l2) * 256 + i3;
								
								if(i3 >= 0 && i3 < 256)
								{
									if(i3 != j4 - 1 && k2 != i4 && k2 != l1 - 1 && l2 != k4 && l2 != j2 - 1)
									{
										i3 = j4;
									}
								}
							}
						}
					}
					
					if(!flag2)
					{
						for(k2 = i4; k2 < l1; ++k2)
						{
							double d13 = ((double)(k2 + p_151540_3_ * 16) + 0.5D - posX) / d12;
							
							for(j3 = k4; j3 < j2; ++j3)
							{
								double d14 = ((double)(j3 + p_151540_4_ * 16) + 0.5D - posZ) / d12;
								int k3 = (k2 * 16 + j3) * 256 + i2;
								
								if(d13 * d13 + d14 * d14 < 1.0D)
								{
									for(int l3 = i2 - 1; l3 >= j4; --l3)
									{
										double d11 = ((double)l3 + 0.5D - posY) / d6;
										
										if((d13 * d13 + d14 * d14) * (double)this.field_75046_d[l3] + d11 * d11 / 6.0D < 1.0D)
										{
											digBlock(world, k3, k2, l3, j3, p_151540_3_, p_151540_4_);
										}
										
										--k3;
									}
								}
							}
						}
						
						if(flag1)
						{
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void func_151538_a(World world, int chunkX, int chunkZ, int p_151538_4_, int p_151538_5_, Block[] blockData)
	{
		if(this.rand.nextInt(100) < JTTC_Settings.deepDarkCaves)
		{
			double d0 = (double)(chunkX * 16 + this.rand.nextInt(16));
			double d1 = (double)(this.rand.nextInt(this.rand.nextInt(168) + 8) + 20);
			double d2 = (double)(chunkZ * 16 + this.rand.nextInt(16));
			byte b0 = 1;
			
			for(int i1 = 0; i1 < b0; ++i1)
			{
				float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
				this.genRavine(world, this.rand.nextLong(), p_151538_4_, p_151538_5_, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
			}
		}
	}
	
	/**
	 * Digs out the current block, default implementation removes stone, filler, and top block
	 * Sets the block to lava if y is less then 10, and air other wise.
	 * If setting to air, it also checks to see if we've broken the surface and if so 
	 * tries to make the floor the biome's top block
	 * 
	 * @param data Block data array
	 * @param index Pre-calculated index into block data
	 * @param x local X position
	 * @param y local Y position
	 * @param z local Z position
	 * @param chunkX Chunk X position
	 * @param chunkZ Chunk Y position
	 * @param foundTop True if we've encountered the biome's top block. Ideally if we've broken the surface.
	 */
	protected void digBlock(World world, int index, int x, int y, int z, int chunkX, int chunkZ)
	{
		int i = x + chunkX * 16;
		int j = y;
		int k = z + chunkZ * 16;
		
		Block block = world.getBlock(i, j, k);
		
		if(block.getMaterial() == Material.ground || block.getMaterial() == Material.rock || block.getMaterial() == Material.grass)
		{
			if(y < 10)
			{
				world.setBlock(i, j, k, Blocks.lava, 0, 2);
			} else
			{
				world.setBlock(i, j, k, Blocks.air, 0, 2);
			}
		}
	}
}