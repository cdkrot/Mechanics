package com.cdkrot.mechanics.tileentity;

import com.cdkrot.mechanics.Mechanics;
import com.cdkrot.mechanics.api.allocator.IInventoryEX;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityAllocator extends TileEntity implements IInventory, IInventoryEX {
    public ItemStack allocatorFilterItems[] = new ItemStack[16];

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (amount > 0) {
            ItemStack itemstack = allocatorFilterItems[slot];
            allocatorFilterItems[slot] = null;
            return itemstack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
            itemstack.stackSize = getInventoryStackLimit();
        allocatorFilterItems[slot] = itemstack;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D; // EXperimental
    }

    @Override
    // TODO: rewrite NBT IO code...
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList items = (NBTTagList) nbt.getTag("Items");
        for (int i = 0; (i < items.tagCount() && i < getSizeInventory()); i++) {
            allocatorFilterItems[i] = ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i));// TODO: experimental
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (allocatorFilterItems[i] != null)
                items.appendTag(allocatorFilterItems[i].writeToNBT(new NBTTagCompound()));
            nbttagcompound.setTag("Items", items);
        }
    }

    @Override
    public ItemStack getStackInSlot(int s) {
        return allocatorFilterItems[s];
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public int getSizeInventory() {
        return 16;
    }

    @Override
    public void markDirty() {
        throw new RuntimeException("Unsuported.");
    }

    @Override
    public String getInventoryName() {
        return Mechanics.allocator.getLocalizedName();
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValidForSlot(int s, ItemStack is) {
        return true;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int s) {
        return getStackInSlot(s);
    }

    @Override
    public int getInventoryOutputBegin() {
        return 0;
    }

    @Override
    public int getInventoryOutputEnd() {
        return 15;
    }

    @Override
    public int getInventoryInputBegin() {
        return 0;
    }

    @Override
    public int getInventoryInputEnd() {
        return 0;
    }

    @Override
    public IInventory asIInventory() {
        return this;
    }

    @Override
    public void onTakenSuccessful(int slot, ItemStack left) {
        setInventorySlotContents(slot, left);
    }

    @Override
    public void onPutSuccessful(int slot, ItemStack stack) {
        setInventorySlotContents(slot, stack);
    }

}
