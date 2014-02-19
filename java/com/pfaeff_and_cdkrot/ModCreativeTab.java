package com.pfaeff_and_cdkrot;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

class ModCreativeTab extends CreativeTabs
{
    public ModCreativeTab()
    {
        super("Mod_Pfaeff_and_cdkrot");
    }

    public ItemStack getIconItemStack()
    {
        return new ItemStack(ForgeMod.allocator, 1, 0);
    }
}
