package com.cdkrot.mechanics.gui;

import com.cdkrot.mechanics.tileentity.TileEntityAllocator;

import net.minecraft.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

//package moved; imports added;

public class ContainerAllocator extends Container {

    public ContainerAllocator(IInventory invPlayer, TileEntityAllocator tileentityallocator) {
        allocator = tileentityallocator;

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 8; j++)
                addSlotToContainer(new Slot(tileentityallocator, i * 8 + j, 17 + j * 18, 28 + i * 18));

        for (int i = 0; i < 3; i++)
            for (int k = 0; k < 9; k++)
                addSlotToContainer(new Slot(invPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));

        for (int j = 0; j < 9; j++)
            addSlotToContainer(new Slot(invPlayer, j, 8 + j * 18, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return allocator.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int sl) {
        ItemStack itemstack = null;
        Slot slot = (Slot) inventorySlots.get(sl);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (sl == 0)
                if (!mergeItemStack(itemstack1, 0, 36, true))
                    return null;
                else {
                    Slot filterSlot = (Slot) inventorySlots.get(0);
                    ItemStack filter = filterSlot.getStack();
                    if ((filter == null) || (filterSlot.getStack().stackSize == 0))
                        filterSlot.putStack(slot.decrStackSize(1));
                }

            if (itemstack1.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();

            if (itemstack1.stackSize != itemstack.stackSize)
                slot.onPickupFromSlot(player, itemstack1);
            else
                return null;
        }

        return itemstack;
    }

    private TileEntityAllocator allocator;
}
