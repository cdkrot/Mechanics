package com.cdkrot.mechanics.api.allocator;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class IInventoryWrapper implements IInventoryEX {
	private IInventory inv;
	private int inputBegin, inputEnd, outputBegin, outputEnd;

	public IInventoryWrapper(IInventory base, int inputBegin, int inputEnd, int outputBegin, int outputEnd) {
		this.inv = base;
		this.inputBegin = inputBegin;
		this.inputEnd = inputEnd;
		this.outputBegin = outputBegin;
		this.outputEnd = outputEnd;
	}

	public static IInventoryWrapper createDefault(IInventory base) {
		int s = base.getSizeInventory();
		return new IInventoryWrapper(base, 0, s - 1, 0, s - 1);
	}

	@Override
	public int getInventoryOutputBegin() {
		return outputBegin;
	}

	@Override
	public int getInventoryOutputEnd() {
		return outputEnd;
	}

	@Override
	public int getInventoryInputBegin() {
		return inputBegin;
	}

	@Override
	public int getInventoryInputEnd() {
		return inputEnd;
	}

	@Override
	public IInventory asIInventory() {
		return inv;
	}

	@Override
	public void onTakenSuccessful(int slot, ItemStack left) {
		inv.setInventorySlotContents(slot, left);
	}

	@Override
	public void onPutSuccessful(int slot, ItemStack stack) {
		inv.setInventorySlotContents(slot, stack);
	}
}
