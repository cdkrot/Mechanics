package com.pfaeff_and_cdkrot.net;


import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;


public class PacketBenchmarkIO extends BasicPacket
{
	public String text;
	public GamePosition pos;

	//this packet contains two things: gameposition and text
	//if it is sent from client to server this is a request to update text in benchmark
	//if it is sent to client this is a response to text request.
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		pos.writeTo(buffer);
		ByteBufUtils.writeUTF8String(buffer, text);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		pos = new GamePosition(buffer);
		text = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player)
	{

	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{

	}
}
