package com.pfaeff_and_cdkrot.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

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

	public static GamePosition readFromStream(DataInputStream dis) throws IOException
	{
		return new GamePosition(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt());
	}
	
	public void writeToStream(DataOutputStream dos) throws IOException
	{
		dos.writeInt(worldid);
		dos.writeInt(x);
		dos.writeInt(y);
		dos.writeInt(z);
	}
	
	public TileEntity getTileEntity(MinecraftServer server)
	{
		return server.worldServers[worldid].getBlockTileEntity(x, y, z);
	}
}