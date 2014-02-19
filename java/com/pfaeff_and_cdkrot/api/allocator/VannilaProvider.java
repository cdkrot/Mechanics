package com.pfaeff_and_cdkrot.api.allocator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukeBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityRecordPlayer;
import net.minecraft.world.World;

public class VannilaProvider implements IInventoryProvider
{
	@Override
	public IInventoryEX createIInventory(World w, int x, int y, int z, int id)
	{
		TileEntity tile = w.getBlockTileEntity(x, y, z);
		if (tile instanceof TileEntityRecordPlayer)
			return new JukeBoxInventory((TileEntityRecordPlayer)tile);	
		
		if (tile instanceof TileEntityFurnace)
			return new IInventoryWrapper((TileEntityFurnace)tile, 2, 2, 0, 1);
		
		
		//I hate those morally depraved persons in "Oracle" or "Sun" who
		//decided that nobody should use goto's if i will find you, i will
		//tell you all i am thinking of you, your company, and java language.
		
		int cblockID = w.getBlockId(x, y, z);

		if (w.getBlockId(x + 1, y, z) == cblockID)
		{
			TileEntity chest2 = (w.getBlockTileEntity(x + 1, y, z));
			if (chest2 instanceof IInventory)
				return IInventoryWrapper.createDefault
						(new InventoryLargeChest("", (IInventory)tile, (IInventory) chest2));
		}
		if (w.getBlockId(x - 1, y, z) == cblockID)
		{
			TileEntity chest2 = (w.getBlockTileEntity(x - 1, y, z));
			if (chest2 instanceof IInventory)
				return IInventoryWrapper.createDefault
					(new InventoryLargeChest("", (IInventory)chest2, (IInventory) tile));
		}
		if (w.getBlockId(x, y, z + 1) == cblockID)
		{
			TileEntity chest2 = (w.getBlockTileEntity(x, y, z + 1));
			if (chest2 instanceof IInventory)
				return IInventoryWrapper.createDefault
						(new InventoryLargeChest("", (IInventory) tile, (IInventory) chest2));
		}
		if (w.getBlockId(x, y, z - 1) == cblockID)
		{
			TileEntity chest2 = (w.getBlockTileEntity(x, y, z - 1));
			if (chest2 instanceof IInventory)
				return IInventoryWrapper.createDefault
						(new InventoryLargeChest("", (IInventory) chest2,(IInventory) tile));
		}
		return null;
	}
	
	public static class JukeBoxInventory implements IInventoryEX, IInventory
	{
		private TileEntityRecordPlayer te;
		
		public JukeBoxInventory(TileEntityRecordPlayer tile) {this.te = tile;}
		@Override public int getSizeInventory() {return 1;}
		@Override public ItemStack getStackInSlot(int i) {return (i==0)?te.func_96097_a():null;}
		@Override
		public ItemStack decrStackSize(int i, int j)
		{
			if (i!=0 && j>0)
				return null;
			ItemStack stack = te.func_96097_a();
			te.func_96098_a(null);
			return stack;
		}
		@Override public ItemStack getStackInSlotOnClosing(int i) {return getStackInSlot(i);}
		@Override public void setInventorySlotContents(int i, ItemStack itemstack)
		{
			if (i==0)
				te.func_96098_a(itemstack);
		}
		@Override public String getInvName(){return "";}
		@Override public boolean isInvNameLocalized(){return false;}
		@Override public int getInventoryStackLimit(){return 1;}
		@Override public void onInventoryChanged() {te.onInventoryChanged();}
		@Override public boolean isUseableByPlayer(EntityPlayer entityplayer) {return true;}
		@Override public void openChest(){}
		@Override public void closeChest(){}
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack)
		{
			return (i==0)&&(itemstack.getItem() instanceof ItemRecord)&&(te.func_96097_a()==null);
		}
		@Override public int getInventoryInputBegin() {return 0;}
		@Override public int getInventoryInputEnd() {return 0;}
		@Override public int getInventoryOutputBegin() {return 0;}
		@Override public int getInventoryOutputEnd() {return 0;}
		@Override public IInventory asIInventory() {return this;}
		@Override
		public void onOutputSuccessful(int slot, ItemStack left)
		{
			te.worldObj.playAuxSFX(1005, te.xCoord, te.yCoord, te.zCoord, 0);
			te.worldObj.playRecord(null, te.xCoord, te.yCoord, te.zCoord);
			te.func_96098_a(null);
			te.onInventoryChanged();
			te.worldObj.setBlockMetadataWithNotify(te.xCoord, te.yCoord, te.zCoord, 0, 4);
		}
		public void onInputSuccessful(int slot, ItemStack stack)
		{
			ItemRecord record = (ItemRecord) stack.getItem();
			record.onItemUse(stack, null, te.worldObj, te.xCoord, te.yCoord, te.zCoord, 0, 0, 0, 0);
			((BlockJukeBox)Block.jukebox).insertRecord(te.worldObj, te.xCoord, te.yCoord, te.zCoord, stack);
		}
		@Override
		public Object acceptableItems()
		{
			return null;
		}
	}
	public static class ItemStacksInventory implements IInventory, IInventoryEX
	{
		private EntityItem stacks[];
		
		public ItemStacksInventory(EntityItem arr[]) {stacks = arr;}
		
		@Override
		public int getInventoryOutputBegin()
		{
			return 0;
		}

		@Override
		public int getInventoryOutputEnd()
		{
			return stacks.length - 1;
		}

		@Override
		public int getInventoryInputBegin()
		{
			return 0;
		}

		@Override
		public int getInventoryInputEnd()
		{
			return stacks.length - 1;
		}

		@Override
		public IInventory asIInventory()
		{
			return this;
		}

		@Override
		public void onOutputSuccessful(int slot, ItemStack left)
		{
			if (left == null)
				stacks[slot].setDead();
			else
				stacks[slot].setEntityItemStack(left);
		}

		@Override
		public int getSizeInventory()
		{
			return stacks.length;
		}

		@Override
		public ItemStack getStackInSlot(int i)
		{
			return stacks[i].getEntityItem();
		}

		@Override
		public ItemStack decrStackSize(int i, int j)
		{
			if (j >= stacks[i].getEntityItem().stackSize)
			{
				ItemStack stack = stacks[i].getEntityItem();
				stacks[i].setDead();
				return stack;
			}
			//else
			ItemStack stack = stacks[i].getEntityItem();
			ItemStack ret = stack.splitStack(j);
			stacks[i].setEntityItemStack(stack);
			return ret;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int i)
		{
			return getStackInSlot(i);
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack)
		{
			if (itemstack == null)
				stacks[i].setDead();
			else
				stacks[i].setEntityItemStack(itemstack);
		}

		@Override
		public String getInvName() {return "";}

		@Override
		public boolean isInvNameLocalized() {return false;}

		@Override
		public int getInventoryStackLimit() {return 64;}

		/**
		 * Possibly not to be used, but anyway could be helpful somehow
		 */
		@Override
		public void onInventoryChanged()
		{
			List<EntityItem> list = new ArrayList<EntityItem>();
			for (EntityItem i: stacks)
				if (i!=null && !i.isDead)
					list.add(i);
			stacks = list.toArray(new EntityItem[list.size()]);
		}

		@Override public boolean isUseableByPlayer(EntityPlayer entityplayer){return true;}

		@Override public void openChest(){}
		@Override public void closeChest(){}

		@Override public boolean isItemValidForSlot(int i, ItemStack itemstack)
			{return true;/*any itemstack is valid*/}


		@Override
		public void onInputSuccessful(int slot, ItemStack stack)
		{
			throw new IllegalArgumentException("NO INPUT HERE!");
		}

		@Override
		public Object acceptableItems()
		{
			return null;
		}
	}
}