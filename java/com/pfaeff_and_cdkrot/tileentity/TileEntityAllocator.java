package com.pfaeff_and_cdkrot.tileentity;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.api.allocator.IInventoryEX;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityAllocator extends TileEntity implements IInventory, IInventoryEX
{
	public ItemStack allocatorFilterItems[] = new ItemStack[16];
	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (amount>0)
		{
			ItemStack itemstack = allocatorFilterItems[slot];
			allocatorFilterItems[slot] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack)
	{
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        	itemstack.stackSize = getInventoryStackLimit();
		allocatorFilterItems[slot] = itemstack;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ?
        	false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList items = nbt.getTagList("Items");
		for (int i=0; (i < items.tagCount() && i<getSizeInventory()); i++)
		{
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			short id = item.getShort("id");
			short damage = item.getShort("Damage");
			if (damage < 0)
				damage = 0;
			allocatorFilterItems[i] = new ItemStack(id, 1, damage);
	        
	        if (item.hasKey("tag"))
	            allocatorFilterItems[i].stackTagCompound = item.getCompoundTag("tag");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++)
		{
			if (allocatorFilterItems[i] != null)
			{
				NBTTagCompound item = new NBTTagCompound();
				item.setShort("id", (short) allocatorFilterItems[i].itemID);
				item.setShort("Damage", (short) allocatorFilterItems[i].getItemDamage());
				if (allocatorFilterItems[i].hasTagCompound())
					item.setTag("tag", allocatorFilterItems[i].getTagCompound());
				items.appendTag(item);
			}
			nbttagcompound.setTag("Items", items);
		}
	}
		
	
	@Override
		public ItemStack getStackInSlot(int s){return allocatorFilterItems[s];}
	@Override
		public void openInventory() {}
	@Override
		public void closeInventory() {}
	@Override
		public int getSizeInventory(){return 16;}
	@Override
		public void markDirty(){throw new RuntimeException("Unsuported.");}
	@Override
		public String getInvName(){return ForgeMod.allocator.getLocalizedName();}
	@Override
		public int getInventoryStackLimit(){return 1;}
	@Override
		public boolean isItemValidForSlot(int s, ItemStack is){return true;}
	@Override
		public boolean isInvNameLocalized(){return false;}
	@Override
		public ItemStack getStackInSlotOnClosing(int s){return getStackInSlot(s);}

	@Override
		public int getInventoryOutputBegin(){return 0;}
	@Override
		public int getInventoryOutputEnd(){return 15;}
	@Override
		public int getInventoryInputBegin(){return 0;}
	@Override
		public int getInventoryInputEnd(){return 0;}
	@Override
		public IInventory asIInventory(){return this;}
	@Override
	public void onOutputSuccessful(int slot, ItemStack left)
	{
		//TODO: OOPS
	}

	@Override
	public void onInputSuccessful(int slot, ItemStack stack)
	{
		//TODO: OOPS!
	}

	@Override
	public Object acceptableItems()
	{
		return null;
	}
}
