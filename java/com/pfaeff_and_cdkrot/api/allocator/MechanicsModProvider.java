package com.pfaeff_and_cdkrot.api.allocator;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.pfaeff_and_cdkrot.tileentity.TileEntityAllocator;

public class MechanicsModProvider implements IInventoryProvider {
	@Override
	public IInventoryEX createIInventory(World w, int x, int y, int z, Block b) {
		TileEntity tile = w.getTileEntity(x, y, z);
		if (tile instanceof TileEntityAllocator)
			return (TileEntityAllocator) tile;
		return null;
	}
}
