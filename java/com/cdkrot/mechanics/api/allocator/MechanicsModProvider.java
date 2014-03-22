package com.cdkrot.mechanics.api.allocator;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.cdkrot.mechanics.tileentity.TileEntityAllocator;

public class MechanicsModProvider implements IInventoryProvider {
    // TODO: probably we don't need this class. Check if can be removed.
    @Override
    public IInventoryEX createIInventory(World w, int x, int y, int z, Block b) {
        TileEntity tile = w.getTileEntity(x, y, z);
        if (tile instanceof TileEntityAllocator)
            return (TileEntityAllocator) tile;
        return null;
    }
}
