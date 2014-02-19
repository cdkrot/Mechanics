package com.pfaeff_and_cdkrot.api.allocator;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface IInventoryProviderEntity
{
	/**
	 * if Entity recognized return it as IInventory IInventories
	 * and return IInventory array
	 * @param entity
	 * @return
	 */
	IInventoryEX createIInventory(Entity entity);
}
