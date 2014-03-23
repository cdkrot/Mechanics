package com.cdkrot.mechanics.api.allocator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

//TODO: needs checking!
public class VannilaProvider implements IInventoryProvider {
    @Override
    public IInventoryEX createIInventory(World w, int x, int y, int z, Block b) {
        TileEntity tile = w.getTileEntity(x, y, z);
        if (tile instanceof TileEntityJukebox)
            return new JukeBoxInventory((TileEntityJukebox) tile);

        if (tile instanceof TileEntityFurnace)
            return new IInventoryWrapper((TileEntityFurnace) tile, 2, 2, 0, 1);

        // I hate those morally depraved persons in "Oracle" or "Sun" who
        // decided that nobody should use goto's if i will find you, i will
        // tell you all i am thinking of you, your company, and java language.

        Block cblock = w.getBlock(x, y, z);

        if (w.getBlock(x + 1, y, z) == cblock) {
            // using just links compare (like comparing blockids before)
            TileEntity chest2 = (w.getTileEntity(x + 1, y, z));
            if (chest2 instanceof IInventory)
                return IInventoryWrapper.createDefault(new InventoryLargeChest("", (IInventory) tile, (IInventory) chest2));
        }
        if (w.getBlock(x - 1, y, z) == cblock) {
            TileEntity chest2 = (w.getTileEntity(x - 1, y, z));
            if (chest2 instanceof IInventory)
                return IInventoryWrapper.createDefault(new InventoryLargeChest("", (IInventory) chest2, (IInventory) tile));
        }
        if (w.getBlock(x, y, z + 1) == cblock) {
            TileEntity chest2 = (w.getTileEntity(x, y, z + 1));
            if (chest2 instanceof IInventory)
                return IInventoryWrapper.createDefault(new InventoryLargeChest("", (IInventory) tile, (IInventory) chest2));
        }
        if (w.getBlock(x, y, z - 1) == cblock) {
            TileEntity chest2 = (w.getTileEntity(x, y, z - 1));
            if (chest2 instanceof IInventory)
                return IInventoryWrapper.createDefault(new InventoryLargeChest("", (IInventory) chest2, (IInventory) tile));
        }
        return null;
    }

    public static class JukeBoxInventory implements IInventoryEX, IInventory {
        private TileEntityJukebox te;

        public JukeBoxInventory(TileEntityJukebox tile) {
            this.te = tile;
        }

        @Override
        public int getSizeInventory() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return (i == 0) ? te.func_145856_a() : null;
        }

        @Override
        public ItemStack decrStackSize(int i, int j) {
            if (i != 0 && j > 0)
                return null;
            ItemStack stack = te.func_145856_a();
            te.func_145828_a(null);
            return stack;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int i) {
            return getStackInSlot(i);
        }

        @Override
        public void setInventorySlotContents(int i, ItemStack itemstack) {
            if (i == 0)
                te.func_145857_a(itemstack);
        }

        @Override
        public int getInventoryStackLimit() {
            return 1;
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer entityplayer) {
            return true;
        }

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return (i == 0) && (itemstack.getItem() instanceof ItemRecord) && (te.func_145856_a() == null);
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
        public int getInventoryOutputBegin() {
            return 0;
        }

        @Override
        public int getInventoryOutputEnd() {
            return 0;
        }

        @Override
        public IInventory asIInventory() {
            return this;
        }

        @Override
        public void onTakenSuccessful(int slot, ItemStack left) {
            te.getWorldObj().playAuxSFX(1005, te.xCoord, te.yCoord, te.zCoord, 0);
            te.getWorldObj().playRecord(null, te.xCoord, te.yCoord, te.zCoord);
            te.func_145828_a(null);
            te.getWorldObj().setBlockMetadataWithNotify(te.xCoord, te.yCoord, te.zCoord, 0, 4);
        }

        @Override
        public void onPutSuccessful(int slot, ItemStack stack) {
            // TODO: not sure what following code does, needs checking.
            ItemRecord record = (ItemRecord) stack.getItem();
            record.onItemUse(stack, null, te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, 0, 0, 0, 0);
            // ((BlockJukebox) Blocks.jukebox).insertRecord(te.worldObj,
            // te.xCoord, te.yCoord, te.zCoord, stack);
            ((BlockJukebox) Blocks.jukebox).func_149925_e(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
        }

        @Override
        public String getInventoryName() {
            return "";
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public void openInventory() {
        }

        @Override
        public void closeInventory() {
        }
    }

    public static class ItemStacksInventory implements IInventory, IInventoryEX {
        private EntityItem stacks[];

        public ItemStacksInventory(EntityItem arr[]) {
            stacks = arr;
        }

        @Override
        public int getInventoryOutputBegin() {
            return 0;
        }

        @Override
        public int getInventoryOutputEnd() {
            return stacks.length - 1;
        }

        @Override
        public int getInventoryInputBegin() {
            return 0;
        }

        @Override
        public int getInventoryInputEnd() {
            return stacks.length - 1;
        }

        @Override
        public IInventory asIInventory() {
            return this;
        }

        @Override
        public void onTakenSuccessful(int slot, ItemStack left) {
            if (left == null)
                stacks[slot].setDead();
            else
                stacks[slot].setEntityItemStack(left);
        }

        @Override
        public int getSizeInventory() {
            return stacks.length;
        }

        @Override
        public ItemStack getStackInSlot(int i) {
            return stacks[i].getEntityItem();
        }

        @Override
        public ItemStack decrStackSize(int i, int j) {
            if (j >= stacks[i].getEntityItem().stackSize) {
                ItemStack stack = stacks[i].getEntityItem();
                stacks[i].setDead();
                return stack;
            }
            // else
            ItemStack stack = stacks[i].getEntityItem();
            ItemStack ret = stack.splitStack(j);
            stacks[i].setEntityItemStack(stack);
            return ret;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int i) {
            return getStackInSlot(i);
        }

        @Override
        public void setInventorySlotContents(int i, ItemStack itemstack) {
            if (itemstack == null)
                stacks[i].setDead();
            else
                stacks[i].setEntityItemStack(itemstack);
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer entityplayer) {
            return true;
        }

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return true;/* any itemstack is valid */
        }

        @Override
        public void onPutSuccessful(int slot, ItemStack stack) {
            throw new UnsupportedOperationException("NO INPUT HERE!");
        }

        @Override
        public String getInventoryName() {
            return "";
        }

        @Override
        public boolean hasCustomInventoryName() {
            return false;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public void openInventory() {
        }

        @Override
        public void closeInventory() {
        }
    }
}