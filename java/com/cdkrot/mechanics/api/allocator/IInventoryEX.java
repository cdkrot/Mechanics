package com.cdkrot.mechanics.api.allocator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

//TODO: Something wrong here.
public interface IInventoryEX {
	/**
	 * See BlockAllocator.getRandomItemStackFromContainer
	 * 
	 * @return intervasl of output stacks group. If no outputtable slots return invalid interval.
	 * 
	 *         OUTPUT is where the allocator places items
	 */
	int getInventoryOutputBegin();
	int getInventoryOutputEnd();

	/**
	 * INPUT is the interval were the allocator can take items
	 */
	int getInventoryInputBegin();
	int getInventoryInputEnd();

	/**
	 * 
	 * @return IInventory
	 */
	IInventory asIInventory();

	/**
	 * On outputSuccesful - when taken succesful. (update stack data yourself)
	 * On input succesful - when put succesful (update stack data yourself)
	 * 
	 * @param slot
	 * @param left
	 */
	void onOutputSuccessful(int slot, ItemStack left);
	void onInputSuccessful(int slot, ItemStack stack);
}
