package com.pfaeff_and_cdkrot.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;

import java.nio.charset.Charset;


/**
 * Basic packet abstraction
 */
public abstract class BasicPacket
{
	/**
	 * Encode the packet data into the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
	 *
	 * @param ctx    channel context
	 * @param buffer the buffer to encode into
	 */
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer);

	/**
	 * Decode the packet data from the ByteBuf stream. Complex data sets may need specific data handlers (See @link{cpw.mods.fml.common.network.ByteBuffUtils})
	 *
	 * @param ctx    channel context
	 * @param buffer the buffer to decode from
	 */
	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer);

	/**
	 * Handle a packet on the client side. Note this occurs after decoding has completed.
	 *
	 */
	public abstract void handleClientSide();

	/**
	 * Handle a packet on the server side. Note this occurs after decoding has completed.
	 *
	 * @param player the player reference
	 */
	public abstract void handleServerSide(EntityPlayer player);
}