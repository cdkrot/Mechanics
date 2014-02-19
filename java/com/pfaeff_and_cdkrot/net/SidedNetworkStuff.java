package com.pfaeff_and_cdkrot.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.pfaeff_and_cdkrot.gui.GuiBenchmark;
import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

//Sided network code
public class SidedNetworkStuff
{

	@SideOnly(Side.CLIENT)
	public static void openBenchmarkGUI(GamePosition position, String text)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiBenchmark(position, text));
	}
	
	@SideOnly(Side.CLIENT)
	public static void requestBenchmarkGUI(World w, int x, int y, int z)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try
		{
			dos.writeInt(w.provider.dimensionId);
			dos.writeInt(x); dos.writeInt(y); dos.writeInt(z);
		} catch (IOException e)
		{
			;
		}
		Minecraft.getMinecraft().getNetHandler()
		.addToSendQueue(new Packet250CustomPayload("mechanics|2", baos.toByteArray()));
	}
	
}
