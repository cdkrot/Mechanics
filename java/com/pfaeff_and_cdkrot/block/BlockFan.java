package com.pfaeff_and_cdkrot.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.tileentity.TileEntityFanON;
import com.pfaeff_and_cdkrot.util.Utility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFan extends BlockContainer
{
	//own implementation of Fan idea, bad implementation, in fact.
    // Icons
	@SideOnly(Side.CLIENT)
    private IIcon icons[];//[zero for inactive] [one for active]

	public BlockFan()
	{
		super(Material.rock);
		this.setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("mechanics::cdkrotFan");
	}
    
    @Override
    public boolean hasTileEntity(int meta)
    {
    	return (meta&8)!=0;
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random nullrandom)
    {
    	this.updatePowered(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block b)
    {
    	this.updatePowered(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack unused)
    {
    	int suggested = Utility.getMetadataForBlockAnyPlaced(world, x, y, z, entity);
    	boolean ispow = world.isBlockIndirectlyGettingPowered(x, y, z);
    	world.setBlockMetadataWithNotify(x, y, z, ispow ? suggested|8 : suggested, 2);//side+8 : side
    }
    


	@Override
	public TileEntity createNewTileEntity(World world, int i)
	{
		return createNewTileEntity(world);
	}

	public TileEntity createNewTileEntity(World w)
	{
		return new TileEntityFanON();
	}


    public TileEntity createTileEntity(World world, int metadata)
    {   
    	if ((metadata&8)!=0)
    		return createNewTileEntity(world);
        return null;
    }

	
	public BlockFan setCreativeTab(CreativeTabs ctab)
	{
		super.setCreativeTab(ctab); return this;
	}

	public boolean updatePowered(World world, int x, int y, int z)
	{
    	int meta = world.getBlockMetadata(x, y, z); //if 4'th bit set = powered
        if(world.isBlockIndirectlyGettingPowered(x, y, z))
        {
        	if ((meta & 8)==0)
        	{
        		world.setBlockMetadataWithNotify(x, y, z, meta|8, 2);//unpow->pow
        		world.setTileEntity(x, y, z, this.createNewTileEntity(world));
        	}
        	return true;
        }
        else
        {
        	if ((meta & 8)!=0)
        	{
        		world.setBlockMetadataWithNotify(x, y, z, meta&7, 2);
        		world.removeTileEntity(x, y, z);//pow->unpow
        	}
        	return false;
        }
	}
	//-----------TEXTURE------------//
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
	{
		icons = new IIcon[2];
        icons[0] = iconRegister.registerIcon(ForgeMod.modid_lc+":fanfront");
        icons[1] = iconRegister.registerIcon(ForgeMod.modid_lc+":fanfrontactive");
        this.blockIcon = iconRegister.registerIcon(ForgeMod.modid_lc+":pfaeff_topbottom");
    }
	
	//Soooo dirty item view
	//TODO: is changed in 1.7?
    @Override
    @SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int StupidMetaAlwaysZero)
    {
    	return getIconForTerrain(side, 3);
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.getIconForTerrain(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
    
    @SideOnly(Side.CLIENT)
	private IIcon getIconForTerrain(int side, int meta)
	{
		return side==(meta&7) ? ((meta & 8)==0 ? icons[0]:icons[1] ): this.blockIcon;
	}

    public int getMobilityFlag()
    {
    	return 0;
    	//return 2;
    }
}