package com.pfaeff_and_cdkrot;

import com.pfaeff_and_cdkrot.entity.EntityFanParticle;
import com.pfaeff_and_cdkrot.gui.RendererFanParticle;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends BaseProxy
{
	

	public void doInit()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityFanParticle.class, new RendererFanParticle());
	}
}
