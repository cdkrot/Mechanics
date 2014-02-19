package com.pfaeff_and_cdkrot.block;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.UP;

import java.util.List;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.util.Utility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockHalfSlab;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockPoweredOre;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

//Stub
public class BlockClock extends Block
{
	
	@SideOnly(Side.CLIENT)
	private Icon iconArray[];


	public BlockClock(int id)
	{
		super(id, Material.rock);
		this.setHardness(2.0f).setUnlocalizedName("mechanics::cdkrotClock");
		//this.setBlockBoundsForMetadata(3);
	}
	/*
	@Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player, int p6, float p7, float p8, float p9)
    {
        if (w.isRemote)
        {
            return player.isSneaking();
        }
        if (!player.isSneaking())
        	return false;
        int meta = w.getBlockMetadata(x, y, z);
        String base="\u00A7A"+LangBase.lang.getValue("mechanics.chat.clock_mode.change");
        if ((meta&8)==0)
        {
        	meta|=8;
            player.addChatMessage
            (base+" \u00A74"+LangBase.lang.getValue("mechanics.chat.clock_mode.realtime")+"\u00A7A.");
        }
        else
        {
        	meta&=7;
            player.addChatMessage
            (base+" \u00A74"+LangBase.lang.getValue("mechanics.chat.clock_mode.suntime")+"\u00A7A.");
        }
        w.setBlockMetadataWithNotify(x, y, z, meta, 2);//send upd.
        return true;
    }
	    
	//===Block Placed===//
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack unused)
    {
    	int suggested = Utility.getMetadataForBlockSidePlaced(entity.rotationYaw);
    	world.setBlockMetadataWithNotify(x, y, z, suggested, 2);//
    }
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
		if (!world.isRemote)
		{
			int meta = Utility.getDefaultDirectionsMeta(world, x, y, z);
			world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		}
    }
	//===Block Bounds===//
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z)
    {
    	setBlockBoundsForMetadata(iba.getBlockMetadata(x, y, z));
    }
    @Override
    public void setBlockBoundsForItemRender()
    {
    	setBlockBoundsForMetadata(3);
    }

    public void setBlockBoundsForMetadata(int meta)
    {
    	switch(meta&7)
    	{
    	case 2: this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f); break;
    	case 3: this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f); break;
    	case 4: this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f); break;
    	case 5: this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f); break;
    	case 0: this.setBlockBounds(0.0f, 0.5f, 0.0f, 1.0f, 1.0f, 1.0f); break;
    	case 1: this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f); break;
    	}
    }
    //===Textures===//
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register)
    {
        this.blockIcon = register.registerIcon("clockface");
        this.iconArray = new Icon[4];
        this.iconArray[0] = register.registerIcon("half_side");
        this.iconArray[1] = register.registerIcon("pfaeff_topbottom");
        this.iconArray[2] = register.registerIcon("half_side_2");
        this.iconArray[3] = register.registerIcon("clockface_2");
    }
	
	//Soooo dirty item view
    @Override
    @SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int StupidMetaIsAlwaysZero)
    {
    	return getIconForTerrain(side, 11);
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return this.getIconForTerrain(par5, par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
    
    
    @SideOnly(Side.CLIENT)
	private Icon getIconForTerrain(int side, int meta)
	{
    	int m = meta&7;
    	if (side==m)
    		return ((meta&8)==0)?iconArray[3]:blockIcon;
    	if (side==Utility.getOppositeSide(m))
    		return iconArray[1];
		if ((side==1||side==0)&&(m==3||m==2))
			return iconArray[2];
    	return iconArray[0];
	}
    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
    {
        int meta = world.getBlockMetadata(x, y, z);
        //if (meta==side.ordinal())
        return (meta == Utility.getOppositeSide(side.ordinal()));
        //return false;
    }
    @Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
	  this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
	  super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

    @Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	*/
}