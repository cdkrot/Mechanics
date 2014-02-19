package com.pfaeff_and_cdkrot.block;

import java.io.IOException;
import java.util.List;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.MechanicsHelpCommand;
import com.pfaeff_and_cdkrot.api.benchmark.BenchmarkRegistry;
import com.pfaeff_and_cdkrot.gui.GuiBenchmark;
import com.pfaeff_and_cdkrot.net.SidedNetworkStuff;
import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;
import com.pfaeff_and_cdkrot.util.Utility;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;

public class BlockBenchmark extends BlockContainer
{

	@SideOnly(Side.CLIENT)
	private Icon[] icons;
	public static int radius;
	public static String def;
	public BlockBenchmark(int id)
	{
		super(id, Material.rock);
		this.setHardness(2.0F).setUnlocalizedName("mechanics::benchmark");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		this.blockIcon = ir.registerIcon(ForgeMod.modid_lc + ":benchmark_block_top");
		icons = new Icon[]
			{
				ir.registerIcon(ForgeMod.modid_lc + ":benchmark_block_bottom"),
				ir.registerIcon(ForgeMod.modid_lc+":benchmark_block_side")
			};
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int s, int m)
	{
		if (s == 1)
			return blockIcon;
		if (s == 0)
			return icons[0]; 
		return icons[1];
	}

    public void onNeighborBlockChange(World w, int x, int y, int z, int nid)
    {
        if (!w.isRemote)
        	updatePowerState(w, x, y, z);
    }
    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        	updatePowerState(world, x, y, z);
    }
    
    public void updatePowerState(World world, int x, int y, int z)
    {
    	boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
    	boolean waspowered = world.getBlockMetadata(x, y, z) == 1;
    	if (waspowered == powered)
    		return;
    	if (waspowered == false/* && powered == true*/)
    	{
    		TileEntityBenchmark tile = (TileEntityBenchmark)world.getBlockTileEntity(x, y, z);
    		String string = tile.getCurText();
    		float mx = x+0.5f;
    		float my = y+0.5f;
    		float mz = z+0.5f;
    		
    		if (!BenchmarkRegistry.instance.onBenchmark(tile, string))
    			return;
    		//do the stuff
    		List<ICommandSender> list = world.getEntitiesWithinAABB(ICommandSender.class, AxisAlignedBB.
    			getBoundingBox(mx-radius, my-radius, mz-radius,
    					mx+radius, my+radius, mz+radius));
    		for (ICommandSender sender: list)
    			MechanicsHelpCommand.SendLineToPlayer(sender, string);
    		world.setBlockMetadataWithNotify(x, y, z, 1, 4);
    	}
    	else if (waspowered == true /*&& powered == false*/)
    		world.setBlockMetadataWithNotify(x, y, z, 0, 4);
    }

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		TileEntityBenchmark tile = new TileEntityBenchmark();
		tile.setWorldObj(world);
		if (world instanceof WorldServer)
			tile.s = def;
		return tile;
	}
	
    public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int a,float b,float c,float d)
    {
    	if (!(w instanceof WorldServer))
    		SidedNetworkStuff.requestBenchmarkGUI(w, x, y, z);
    	return true;
    }

}
