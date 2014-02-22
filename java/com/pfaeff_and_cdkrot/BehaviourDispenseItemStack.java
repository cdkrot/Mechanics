package com.pfaeff_and_cdkrot;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.block.BlockDispenser;

//Don't know why it is needed. However moved to the new package; added imports

public class BehaviourDispenseItemStack extends BehaviorDefaultDispenseItem
{

	@Override
    protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) 
	{
        EnumFacing facing = EnumFacing.getFront(blockSource.getBlockMetadata() % 6);
        IPosition position = BlockDispenser.func_149939_a(blockSource);//temporary solution, i suppose
        doDispense(blockSource.getWorld(), itemStack, 6, facing, position);
        return itemStack;
    }
}
