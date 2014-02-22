package com.pfaeff_and_cdkrot.net;

import com.pfaeff_and_cdkrot.ForgeMod;
import com.pfaeff_and_cdkrot.api.benchmark.BenchmarkRegistry;
import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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
		throw new UnsupportedOperationException("This packet is unapplicable on client side");
	}

	@Override
	@SideOnly(Side.SERVER)
	public void handleServerSide(EntityPlayerMP player)
	{
		TileEntityBenchmark tile = (TileEntityBenchmark)MinecraftServer.getServer().worldServers[pos.worldid].getTileEntity(pos.x, pos.y, pos.z);
		if (BenchmarkRegistry.instance.requestEditor(tile, player))
		{
			PacketBenchmarkIO packet = new PacketBenchmarkIO();
			packet.pos = pos;
			packet.text = tile.s;

			ForgeMod.networkHandler.sendTo(packet, player);
		}
	}
}
