package com.cdkrot.mechanics.tileentity;

import java.util.List;
import java.util.Random;

import com.cdkrot.mechanics.BehaviourDispenseItemStack;
import com.cdkrot.mechanics.Mechanics;
import com.cdkrot.mechanics.api.allocator.AllocatorRegistry;
import com.cdkrot.mechanics.api.allocator.IInventoryEX;
import com.cdkrot.mechanics.api.allocator.IInventoryWrapper;
import com.cdkrot.mechanics.util.DirectionalVecs;
import com.cdkrot.mechanics.util.FakeIInventory;
import com.cdkrot.mechanics.util.VecI3Base;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityAllocator extends TileEntity implements IInventory, IInventoryEX {
    public ItemStack allocatorFilterItems[] = new ItemStack[16];
    public FakeIInventory transfer = new FakeIInventory(1);

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
            allocatorFilterItems[i] = ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i));// TODO:
                                                                                                // experimental
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
        Mechanics.modLogger.info("Marked dirty");
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

    // Imported from BlockAllocator, it is much easier to deal with it in an
    // instance of the TileENtity, we don't have to reget an instance.

    private final BehaviourDispenseItemStack dispenser = new BehaviourDispenseItemStack();

    /**
     * Returns true, if the item is allowed to pass (Filter is array filled with
     * filter items. Lists filled with null will not pass correctly.
     */
    private boolean passesFilter(ItemStack item, ItemStack[] filter) {
        if (filter == null)
            return true;
        boolean t = true;
        for (ItemStack filter_ : filter) {
            if (filter_ == null)
                continue;
            t = false;
            if ((item.getItem() == filter_.getItem()) && (item.getItemDamage() == filter_.getItemDamage()))
                if (ItemStack.areItemStackTagsEqual(item, filter_))
                    return true;
        }
        return t;
    }

    /**
     * Returns the container (IIventory) at position (x,y,z) if it exists.
     */
    protected IInventoryEX containerAtPos(World world, int x, int y, int z) {
        IInventoryEX inv = AllocatorRegistry.instance.getIInventoryFor(world, x, y, z);

        if (inv != null)
            return inv;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IInventoryEX)
            return (IInventoryEX) tile;
        else if (tile instanceof IInventory)
            return IInventoryWrapper.createDefault((IInventory) tile);
        else
            return null;
    }

    /**
     * INPUT! Returns a random item (index) from the container, using the same
     * rule as the dispenser
     */
    public int getRandomItemIndexFromContainer(IInventoryEX inventory, Random rand, ItemStack[] filter) {
        if (inventory == null)
            return -1;
        IInventory base = inventory.asIInventory();
        if (base == null)
            return -1;
        int ret = -1, j = 1;

        for (int k = inventory.getInventoryInputBegin(); k <= inventory.getInventoryInputEnd(); k++) {
            ItemStack s = base.getStackInSlot(k);
            if ((s != null) && passesFilter(s, filter) && (rand.nextInt(j) == 0)) {
                ret = k;
                j++;
            }
        }
        return ret;
    }

    /**
     * Spits out an item (like the dropper, but the whole stack)
     */
    protected void dispense(World world, int i, int j, int k, ItemStack item) {
        BlockSourceImpl blockImpl = new BlockSourceImpl(world, i, j, k);
        // Inventory fetcher for hopper, probably not needed
        //
        // int meta = world.getBlockMetadata(i, j, k) & 7;
        // IInventory hopper = TileEntityHopper.func_145893_b(world,
        // (double) (i + Facing.offsetsXForSide[meta]),
        // (double) (j + Facing.offsetsYForSide[meta]),
        // (double) (k + Facing.offsetsZForSide[meta]));

        // TODO: use stack
        ItemStack stack = dispenser.dispense(blockImpl, item);
        // if (stack != null && stack.stackSize == 0)
        // stack = null;
        // removed this, because the code above did nothing anyways. so
        // strange code
    }

    /**
     * Handles the item output. Returns true, if item was successfully put out.
     */
    @SuppressWarnings({ "unchecked" })
    private boolean outputItem(World world, int x, int y, int z, VecI3Base dir, ItemStack item, Random random) {
        int xoff = x + dir.x, yoff = y + dir.y, zoff = z + dir.z;
        IInventoryEX output = containerAtPos(world, xoff, yoff, zoff);

        if (output == null) {
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xoff, yoff, zoff, xoff + 1, yoff + 1, zoff));
            List<IInventoryEX> invs = AllocatorRegistry.instance.getIInventoryAllInFor(entities, false);
            if (invs.size() > 0)
                output = invs.get(random.nextInt(invs.size()));
            else if (!(world.getBlock(xoff, yoff, zoff).isOpaqueCube())) {
                dispense(world, x, y, z, item);
                return true;
            } else
                return false;
        }
        IInventory base = output.asIInventory();

        for (int l = output.getInventoryOutputBegin(); l < output.getInventoryOutputEnd(); l++) {
            ItemStack baseStack = base.getStackInSlot(l);
            if (baseStack == null)
                if (item.stackSize <= base.getInventoryStackLimit() && base.isItemValidForSlot(l, item)) {
                    output.onPutSuccessful(l, item);
                    return true;
                } else {
                    return false;
                }
            else if (((baseStack.isStackable()) && (item.isStackable())) && (baseStack.getItem() == item.getItem()) && (baseStack.getItemDamage() == item.getItemDamage()) && (baseStack.stackSize + item.stackSize <= Math.min(base.getInventoryStackLimit(), item.getMaxStackSize()))) {
                // item is valid for stack
                baseStack = baseStack.copy();// should copy a stack
                baseStack.stackSize += item.stackSize;
                output.onPutSuccessful(l, baseStack);
                return true;
            }
        }
        return false;
    }

    /**
     * Handles all the item input/output
     */
    @SuppressWarnings("unchecked")
    // TODO: remove the need for suppression
    public void allocateItems(World world, int x, int y, int z, Random random) {
        VecI3Base d = DirectionalVecs.list[world.getBlockMetadata(x, y, z)];
        ItemStack[] filter = allocatorFilterItems;
        IInventoryEX input = containerAtPos(world, x - d.x, y - d.y, z - d.z);

        if (input == null) {
            List<Entity> entities = (List<Entity>) world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double) (x - d.x), (double) y - d.y, (double) (z - d.z), (double) (x - d.x + 1), (double) (y - d.y + 1), (float) (z - d.z + 1)));
            List<IInventoryEX> invs = AllocatorRegistry.instance.getIInventoryAllInFor(entities, true);
            // TODO: entity inventories should be caught by other way
            // [REFACTORING].

            if (invs.size() > 0)
                input = invs.get(random.nextInt(invs.size()));
            else
                return;// no input.
        }
        // TODO: should be inlined here.

        int itemIndex = getRandomItemIndexFromContainer(input, random, filter);

        if (itemIndex < 0) {
            // no item
            return;
        }

        IInventory iinventory = input.asIInventory();
        ItemStack stack = iinventory.getStackInSlot(itemIndex).copy();

        if (stack == null) {
            // no stack in slot
            return;
        }

        // correct this if you need to: I assumed that our direction meta is
        // exactly like hoppers
        if (couldMoveStack(this, stack, Facing.oppositeSide[BlockHopper.getDirectionFromMetadata(getBlockMetadata())]) && outputItem(world, x, y, z, d, stack, random)) {
            input.onTakenSuccessful(itemIndex, null);
            iinventory.setInventorySlotContents(itemIndex, stack);
        } else {

        }
        transfer.setInventorySlotContents(0, null);
    }

    private static boolean couldMoveStack(IInventory inv, ItemStack stack, int side) {
        boolean yes = false;
        if (inv instanceof ISidedInventory && side > -1) {
            ISidedInventory isidedinventory = (ISidedInventory) inv;
            int[] slots = isidedinventory.getAccessibleSlotsFromSide(side);

            for (int l = 0; l < slots.length && stack != null && stack.stackSize > 0; ++l) {
                yes = canTryStackMove(inv, stack, slots[l], side);
            }
        } else {
            int size = inv.getSizeInventory();

            for (int k = 0; k < size && stack != null && stack.stackSize > 0; ++k) {
                yes = canTryStackMove(inv, stack, k, side);
            }
        }

        return yes;
    }

    private static boolean canTryStackMove(IInventory inv, ItemStack stack, int slot, int side) {
        ItemStack stackInSlot = inv.getStackInSlot(slot);
        boolean yes = false;

        if (canInsertIntoInventoryAtSlot(inv, stack, slot, side)) {
            if (stackInSlot == null) {
                yes = true;
            } else if (canStackItems(stackInSlot, stack)) {
                int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
                if (max > stackInSlot.stackSize) {
                    yes = true;
                }
            }
        }
        return yes;
    }

    /**
     * Unused until we stop with IInventoryEx, if ever.
     */
    @SuppressWarnings("unused")
    private static ItemStack moveStackToInv(IInventory inv, ItemStack stack, int side) {
        if (inv instanceof ISidedInventory && side > -1) {
            ISidedInventory isidedinventory = (ISidedInventory) inv;
            int[] slots = isidedinventory.getAccessibleSlotsFromSide(side);

            for (int l = 0; l < slots.length && stack != null && stack.stackSize > 0; ++l) {
                stack = tryStackMove(inv, stack, slots[l], side);
            }
        } else {
            int size = inv.getSizeInventory();

            for (int k = 0; k < size && stack != null && stack.stackSize > 0; ++k) {
                stack = tryStackMove(inv, stack, k, side);
            }
        }

        if (stack != null && stack.stackSize == 0) {
            stack = null;
        }

        return stack;
    }

    /**
     * Attempts to move the given stack into the given inventory at the given
     * slot and side.
     */
    private static ItemStack tryStackMove(IInventory inv, ItemStack stack, int slot, int side) {
        ItemStack stackInSlot = inv.getStackInSlot(slot);

        if (canInsertIntoInventoryAtSlot(inv, stack, slot, side)) {
            boolean save = false;

            if (stackInSlot == null) {
                int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
                if (max >= stack.stackSize) {
                    inv.setInventorySlotContents(slot, stack);
                    stack = null;
                } else {
                    inv.setInventorySlotContents(slot, stack.splitStack(max));
                }
                save = true;
            } else if (canStackItems(stackInSlot, stack)) {
                int max = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
                if (max > stackInSlot.stackSize) {
                    int l = Math.min(stack.stackSize, max - stackInSlot.stackSize);
                    stack.stackSize -= l;
                    stackInSlot.stackSize += l;
                    save = l > 0;
                }
            }

            if (save) {
                inv.markDirty();
            }
        }

        return stack;
    }

    /**
     * Checks for insertion into the given inventory at the slot specified, and,
     * if inv implements ISidedInventory, the given side.
     */
    private static boolean canInsertIntoInventoryAtSlot(IInventory inv, ItemStack stack, int slot, int side) {
        return !inv.isItemValidForSlot(slot, stack) ? false : !(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canInsertItem(slot, stack, side);
    }

    /**
     * This compares two item stacks, and ensures that a's size is under it's
     * max and that it's item data (id, meta, tags) matches b's.
     */
    private static boolean canStackItems(ItemStack a, ItemStack b) {
        // old code, I optimized it so it isn't so obfuscated
        // a.getItem() != b.getItem() ? false : (a.getItemDamage() !=
        // b.getItemDamage() ? false : (a.stackSize > a.getMaxStackSize() ?
        // false : ItemStack.areItemStackTagsEqual(a, b)))
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage() && a.stackSize <= a.getMaxStackSize() && ItemStack.areItemStackTagsEqual(a, b);
    }

}
