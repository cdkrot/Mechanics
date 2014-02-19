package com.pfaeff_and_cdkrot.api.allocator;

import net.minecraft.world.World;


public interface IInventoryProvider
{
	/**
	 * 
	 * @param w World
	 * @param x X coord
	 * @param y Y coord
	 * @param z Z coord
	 * @return inventory or null.
	 */
	IInventoryEX createIInventory(World w, int x, int y, int z, int id);
}
