package com.pfaeff_and_cdkrot.api.allocator;

import com.pfaeff_and_cdkrot.tileentity.TileEntityAllocator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MechanicsModProvider implements IInventoryProvider
{

	@Override
	public IInventoryEX createIInventory(World w, int x, int y, int z, int id)
	{
		TileEntity tile = w.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityAllocator)
			return (TileEntityAllocator)tile;
		return null;
	}

}
