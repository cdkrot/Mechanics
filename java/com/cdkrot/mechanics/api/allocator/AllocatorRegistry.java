package com.cdkrot.mechanics.api.allocator;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public class AllocatorRegistry {
	public static final int API_VERSION = 5;
	public static final AllocatorRegistry instance = new AllocatorRegistry();

	private List<IInventoryProvider> list = new ArrayList<IInventoryProvider>();
	private List<IInventoryProviderEntity> list2 = new ArrayList<IInventoryProviderEntity>();

	public void add(IInventoryProvider p) {
		list.add(p);
	}

	public void add(IInventoryProviderEntity p) {
		list2.add(p);
	}

	public IInventoryEX getIInventoryFor(World w, int x, int y, int z)
	{
		Block b = w.getBlock(x, y, z);
		for (IInventoryProvider provider : list)
		{
			IInventoryEX inv = provider.createIInventory(w, x, y, z, b);
			if (inv != null)
				return inv;
		}
		return null;
	}

	// TODO: Never used.
	public IInventoryEX getIInventoryFor(Entity entity) {
		for (IInventoryProviderEntity provider : list2) {
			IInventoryEX inv = provider.createIInventory(entity);
			if (inv != null)
				return inv;
		}
		return null;
	}

	/**
	 * Recognizes all inventories types: IInventory, IInventoryEX, provider; entityItems;
	 * 
	 * @param entities
	 *            (modifyable list)
	 * @param isInput
	 *            Varifies if there is an input
	 * @return list of IInventories in IInventoryEX format
	 */
	public List<IInventoryEX> getIInventoryAllInFor(List<Entity> entities, boolean isInput) {
		List<IInventoryEX> res = new ArrayList<IInventoryEX>();
		for (IInventoryProviderEntity provider : list2) {
			for (int i = entities.size() - 1; i >= 0; i--) {
				Entity e = entities.get(i);
				IInventoryEX inv = provider.createIInventory(e);
				if (inv != null) {
					res.add(inv);
					entities.remove(i);
				}
			}
		}
		List<EntityItem> items = new ArrayList<EntityItem>();
		for (Entity e : entities) {
			if (e instanceof EntityItem)
				items.add((EntityItem) e);
			else if (e instanceof IInventoryEX)
				res.add((IInventoryEX) e);
			else if (e instanceof IInventory)
				res.add(IInventoryWrapper.createDefault((IInventory) e));
		}
		if (items.size() > 0 && isInput)
			res.add(new VannilaProvider.ItemStacksInventory(items
					.toArray(new EntityItem[items.size()])));
		return res;
	}
}
