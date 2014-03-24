package com.cdkrot.mechanics.tileentity;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSourceImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

import com.cdkrot.mechanics.BehaviourDispenseItemStack;
import com.cdkrot.mechanics.Mechanics;
import com.cdkrot.mechanics.api.allocator.AllocatorRegistry;
import com.cdkrot.mechanics.util.DirectionalVecs;
import com.cdkrot.mechanics.util.FakeIInventory;
import com.cdkrot.mechanics.util.VecI3Base;

public class TileEntityAllocator extends TileEntity implements IInventory {
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

    // ==========================================================//
    // Start of item io algorithms. //
    // ==========================================================//
    // TODO rewrite

    private final BehaviourDispenseItemStack dispenser = new BehaviourDispenseItemStack();

    /**
     * Returns true, if the item is allowed to pass.
     */
    public boolean passesFilter(ItemStack item) {
        if (allocatorFilterItems == null)
            return true;
        boolean t = true;
        for (ItemStack filter_ : allocatorFilterItems) {
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
    protected IInventory containerAtPos(World world, int x, int y, int z) {
        IInventory inv = AllocatorRegistry.instance.getIInventoryFor(world, x, y, z);

        if (inv != null)
            return inv;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IInventory)
            return (IInventory) tile;
        else
            return null;
    }

    /**
     * INPUT! Returns a random item (index) from the container, using the same
     * rule as the dispenser
     */
    public int getRandomItemIndexFromContainer(IInventory inventory, Random rand) {
        if (inventory == null)
            return -1;

        int ret = -1, j = 1;

        if (inventory instanceof ISidedInventory) {
            // FIXME: id of INPUT
            int list[] = ((ISidedInventory) inventory).getAccessibleSlotsFromSide(0);

            for (int k = 0; k < list.length; k++) {
                ItemStack s = inventory.getStackInSlot(list[k]);
                if ((s != null) && passesFilter(s) && rand.nextInt(j) == 0) {
                    ret = list[k];
                    j++;
                }
            }
        } else {
            for (int k = 0; k < inventory.getSizeInventory(); k++) {
                ItemStack s = inventory.getStackInSlot(k);
                if ((s != null) && passesFilter(s) && (rand.nextInt(j) == 0)) {
                    ret = k;
                    j++;
                }
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
     * Inserts itemstack to given place, partialy or fully
     * 
     * @return stack's reference or null if item was fully put out.
     */
    @SuppressWarnings({ "unchecked" })
    private ItemStack outputItem(World world, int x, int y, int z, VecI3Base dir, ItemStack stack, Random random) {
        int xoff = x + dir.x, yoff = y + dir.y, zoff = z + dir.z;
        IInventory output = containerAtPos(world, xoff, yoff, zoff);

        if (output == null) {
            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xoff, yoff, zoff, xoff + 1, yoff + 1, zoff));
            List<IInventory> invs = AllocatorRegistry.instance.getIInventoryAllInFor(entities, false);
            if (invs.size() > 0)
                output = invs.get(random.nextInt(invs.size()));
            else if (!(world.getBlock(xoff, yoff, zoff).isOpaqueCube())) {
                dispense(world, x, y, z, stack);
                return null;
            } else
                return stack;
        }
        if (output instanceof ISidedInventory) {
            // FIXME: ID OF OUTPUT SIDE
            int list[] = ((ISidedInventory) output).getAccessibleSlotsFromSide(1);
            for (int i = 0; (i < list.length) && stack != null; i++)
                stack = outputItem_do(output, list[i], stack);
        } else
            for (int l = 0; (l < output.getSizeInventory() && stack != null); l++)
                stack = outputItem_do(output, l, stack);

        return stack;
    }

    /**
     * Inserts itemstack to given slot, partialy or fully.
     * 
     * @param inv
     *            inventory to output.
     * @param id
     *            slot id
     * @param stack
     *            modifyable stack(quantity)
     * @return stack's referense, or null if fully taken.
     */
    protected ItemStack outputItem_do(IInventory inv, int id, ItemStack stack) {
        ItemStack baseStack = inv.getStackInSlot(id);
        if (baseStack == null)
            if (stack.stackSize <= inv.getInventoryStackLimit() && inv.isItemValidForSlot(id, stack)) {
                inv.setInventorySlotContents(id, stack);
                return null;
            } else {
                ItemStack stack2 = stack.copy();
                stack2.stackSize = inv.getInventoryStackLimit();
                stack.stackSize -= inv.getInventoryStackLimit();
                inv.setInventorySlotContents(id, stack2);
                return stack;
            }
        else if (((baseStack.isStackable()) && (stack.isStackable())) && (baseStack.getItem() == stack.getItem()) && (baseStack.getItemDamage() == stack.getItemDamage())) {
            // item is valid for stack
            int stack_limit = Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize());
            if (baseStack.stackSize + stack.stackSize <= stack_limit) {
                baseStack.stackSize += stack.stackSize;
                return null;
            } else {
                stack.stackSize -= (stack_limit - baseStack.stackSize);
                baseStack.stackSize = stack_limit;
                return stack;
            }
        } else
            return stack;
    }

    /**
     * Handles all the item input/output
     */
    @SuppressWarnings("unchecked")
    // TODO: remove the need for suppression
    public void allocateItems(World world, int x, int y, int z, Random random) {
        VecI3Base d = DirectionalVecs.list[world.getBlockMetadata(x, y, z)];
        IInventory input = containerAtPos(world, x - d.x, y - d.y, z - d.z);

        if (input == null) {
            List<Entity> entities = (List<Entity>) world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox((double) (x - d.x), (double) y - d.y, (double) (z - d.z), (double) (x - d.x + 1), (double) (y - d.y + 1), (float) (z - d.z + 1)));
            List<IInventory> invs = AllocatorRegistry.instance.getIInventoryAllInFor(entities, true);
            // TODO: entity inventories should be caught by other way
            // [REFACTORING].

            if (invs.size() > 0)
                input = invs.get(random.nextInt(invs.size()));
            else
                return;// no input.
        }
        // TODO: inline
        int itemIndex = getRandomItemIndexFromContainer(input, random);

        if (itemIndex < 0)
            return; // no item

        IInventory iinventory = input;
        ItemStack stack = iinventory.getStackInSlot(itemIndex).copy();

        if (stack == null)
            return;
        if (couldMoveStack(this, stack, Facing.oppositeSide[getBlockMetadata()]))
            iinventory.setInventorySlotContents(itemIndex, outputItem(world, x, y, z, d, stack, random));
        // TODO: the onest place transfer used, is it needed?
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
     * Unused until we stop with IInventory, if ever.
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
        return (inv.isItemValidForSlot(slot, stack) && (!(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canInsertItem(slot, stack, side)));
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
