package com.pfaeff_and_cdkrot.gui;

import org.lwjgl.opengl.GL11;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.tileentity.TileEntityAllocator;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiAllocator extends GuiContainer
{
	public ResourceLocation texture = new ResourceLocation(ForgeMod.modid_lc+":gui/allocator.png");
    public GuiAllocator(InventoryPlayer invPlayer, TileEntityAllocator tileentityallocator)
    {
        super(new ContainerAllocator(invPlayer, tileentityallocator));
        allocatorInv = tileentityallocator;
    }

    protected void drawGuiContainerForegroundLayer(int i1, int i2)
    {
    	//use allocator name
        this.fontRenderer.drawString(StatCollector.translateToLocal("tile.mechanics::allocator.name"), 60, 6, 0x404040);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);

        int j1 = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j1, k, 0, 0, xSize, ySize);
	}
	
	
	
	private TileEntityAllocator allocatorInv;
}
