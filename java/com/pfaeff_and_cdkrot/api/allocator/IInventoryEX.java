package com.pfaeff_and_cdkrot.api.allocator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInventoryEX
{
	/**
	 * See BlockAllocator.getRandomItemStackFromContainer
	 * @return intervasl of output stacks group.
	 * If no outputtable slots return invalid interval.
	 * 
	 * OUTPUT is where allocator PUTS items
	 */
	int getInventoryOutputBegin();
	int getInventoryOutputEnd();
	/**
	 	Input is interval were allocator CAN take items
	 */
	int getInventoryInputBegin();
	int getInventoryInputEnd();
	
	/**
	 * 
	 * @return IInventory
	 */
	IInventory asIInventory();

	/**
	 * On outputSuccesful - when taken succesful
	 * On input succesful - when put succesful
	 * @param slot
	 * @param left
	 */
	void onOutputSuccessful(int slot, ItemStack left);
	void onInputSuccessful(int slot, ItemStack stack);
	
	/**
	 * Return null to Any item acceptable.
	 * Or something
	 * [Unimplemented Yet] May come soon
	 * @return
	 */
	Object acceptableItems();
}
