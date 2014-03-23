package com.cdkrot.mechanics.api.allocator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInventoryEX {
    /**
     * See BlockAllocator.getRandomItemStackFromContainer
     * 
     * @return intervasl of output stacks group. If no outputtable slots return
     *         invalid interval.
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
     * Taken succesful some items. (update stack data yourself)
     * 
     * @param slot
     *            slot id
     * @param left
     *            the left stack (can be null)
     */
    void onTakenSuccessful(int slot, ItemStack left);

    /**
     * When put succesful (update stack data yourself)
     * 
     * @param slot
     *            slot id
     * @param stack
     *            the new stack, to replace previous.
     */
    void onPutSuccessful(int slot, ItemStack stack);
}
