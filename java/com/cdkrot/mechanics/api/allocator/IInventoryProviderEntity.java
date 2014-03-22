package com.cdkrot.mechanics.api.allocator;

import net.minecraft.entity.Entity;

public interface IInventoryProviderEntity {
    /**
     * If Entity recognized return it as IInventory array
     * 
     * @param entity
     * @return IInventory[]
     */
    IInventoryEX createIInventory(Entity entity);
}
