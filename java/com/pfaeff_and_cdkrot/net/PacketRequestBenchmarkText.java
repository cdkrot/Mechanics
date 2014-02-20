package com.pfaeff_and_cdkrot.net;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class PacketRequestBenchmarkText extends BasicPacket
{
//this packet sent by CLIENT to SERVER.
	public GamePosition pos;

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		pos.writeTo(buffer);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		pos = new GamePosition(buffer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide()
	{
		throw new UnsupportedOperationException("Wrong side");
	}

	@Override
	@SideOnly(Side.SERVER)
	public void handleServerSide(EntityPlayer player)
	{

	}
}
