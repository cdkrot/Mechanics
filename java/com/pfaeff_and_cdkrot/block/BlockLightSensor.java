package com.pfaeff_and_cdkrot.block;

import java.util.Random;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.tileentity.TileEntityLightSensor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.Icon;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.*;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

//package moved; imports added
//finished!

public class BlockLightSensor extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	private Icon[] iconArray;

	public BlockLightSensor(int id)
	{
		super(id, Material.wood);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
		this.setHardness(3.5F);
		this.setStepSound(Block.soundStoneFootstep);
		this.setUnlocalizedName("mechanics::lightsensor");
		this.setTickRandomly(true);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.iconArray = new Icon[2];
		this.iconArray[0] = par1IconRegister.registerIcon(ForgeMod.modid_lc+":lightsensor_top");
		this.iconArray[1] = par1IconRegister
				.registerIcon("minecraft:daylight_detector_side");//UPD: vanilla texture name changed
	}

	public void updateSensorOutput(World world, int x, int y, int z)
	{
		int lightValue = world.getBlockLightValue(x, y, z);
		int redStoneValue = Math.min(Math.max(lightValue, 0), 15);
		world.setBlockMetadataWithNotify(x, y, z, redStoneValue, 5);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int s)
	{
		return blockAccess.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	// Should use "getIcon". (now this method called like this in super-class.
	@Override
	public Icon getIcon(int par1, int par2)
	{
		return par1 == 1 ? this.iconArray[0] : this.iconArray[1];
	}
		
	@Override
	public int tickRate(World w)
	{
		return 1;
	}
	
	@Override
	public void onNeighborBlockChange(World w, int x, int y, int z, int par5)
	{
		this.updateTick(w, x, y, z, null);
		this.updateSensorOutput(w, x, y, z);
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int par5,
			float par6, float par7, float par8, int par9)
	{
		this.updateSensorOutput(world, x, y, z);
		return par9;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityLightSensor();
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random r)
	{
		if (world.getBlockTileEntity(x, y, z)==null)
			world.setBlockTileEntity(x, y, z, this.createNewTileEntity(world));
	}
	
	@Override
	public BlockLightSensor setCreativeTab(CreativeTabs ctab)
	{
		super.setCreativeTab(ctab);
		return this;
	}
	}
