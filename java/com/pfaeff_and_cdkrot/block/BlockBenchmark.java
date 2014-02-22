package com.pfaeff_and_cdkrot.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.MechanicsHelpCommand;
import com.pfaeff_and_cdkrot.api.benchmark.BenchmarkRegistry;
import com.pfaeff_and_cdkrot.net.SidedNetworkStuff;
import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBenchmark extends BlockContainer
{

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	public static int radius;
	public static String def;
	public BlockBenchmark()
	{
		super(Material.rock);
		this.setHardness(2.0F).setBlockName("mechanics::benchmark");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister ir)
	{
		this.blockIcon = ir.registerIcon(ForgeMod.modid + ":benchmark_block_top");
		icons = new IIcon[]
			{
				ir.registerIcon(ForgeMod.modid + ":benchmark_block_bottom"),
				ir.registerIcon(ForgeMod.modid+":benchmark_block_side")
			};
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int s, int m)
	{
		if (s == 1)
			return blockIcon;
		if (s == 0)
			return icons[0]; 
		return icons[1];
	}

	@Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block b)
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
    	if (!waspowered)
    	{
    		TileEntityBenchmark tile = (TileEntityBenchmark)world.getTileEntity(x, y, z);
    		String string = tile.getCurText();
    		float mx = x+0.5f;
    		float my = y+0.5f;
    		float mz = z+0.5f;
    		
    		if (!BenchmarkRegistry.instance.onBenchmark(tile, string))
    			return;
    		//do the stuff
    		@SuppressWarnings("unchecked")
			List<ICommandSender> list = (List<ICommandSender>) world.getEntitiesWithinAABB(ICommandSender.class, AxisAlignedBB.getBoundingBox(mx-radius, my-radius, mz-radius, mx+radius, my+radius, mz+radius));
    		for (ICommandSender sender: list)
    			MechanicsHelpCommand.SendLineToPlayer(sender, string);
    		world.setBlockMetadataWithNotify(x, y, z, 1, 4);
    	}
    	else
    		world.setBlockMetadataWithNotify(x, y, z, 0, 4);
    }

	@Override
	public TileEntity createNewTileEntity(World world, int i)
	{
		TileEntityBenchmark tile = new TileEntityBenchmark();
		tile.setWorldObj(world);
		if (world instanceof WorldServer)
			tile.s = def;
		return tile;
	}
	
    public boolean onBlockActivated(World w,int x,int y,int z, EntityPlayer p,int a,float b,float c,float d)
    {
    	if (!(w instanceof WorldServer))
    		SidedNetworkStuff.requestBenchmarkGUI(w, x, y, z);
    	return true;
    }

}
