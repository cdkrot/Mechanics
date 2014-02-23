package com.pfaeff_and_cdkrot.api.allocator;

import net.minecraft.block.Block;
import net.minecraft.world.World;


public interface IInventoryProvider
{
	//block here only for speed.
	IInventoryEX createIInventory(World w, int x, int y, int z, Block b);
}
