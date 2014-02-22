package com.pfaeff_and_cdkrot.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

/**
 * Class describes an block position in game
 */
public final class GamePosition
{
	public final int worldid;
	public final int x;
	public final int y;
	public final int z;
	
	public GamePosition(int worldid, int x, int y, int z)
	{
		this.worldid=worldid;
		this.x=x;
		this.y=y;
		this.z=z;
	}

	public GamePosition(ByteBuf in)
	{
		this(in.readInt(), in.readInt(), in.readInt(), in.readInt());
	}

	public static GamePosition readFromStream(DataInputStream dis) throws IOException
	{
		return new GamePosition(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt());
	}

	public void writeTo(ByteBuf buffer)
	{
		buffer.writeInt(worldid);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}
}