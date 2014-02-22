package com.cdkrot.mechanics.net;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class describes an block position in game
 */
public final class GamePosition {
	public final int worldid, x, y, z;

	public GamePosition(int worldid, int x, int y, int z) {
		this.worldid = worldid;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public GamePosition(ByteBuf in) {
		this(in.readInt(), in.readInt(), in.readInt(), in.readInt());
	}

	public static GamePosition readFromStream(DataInputStream dis) throws IOException {
		return new GamePosition(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt());
	}

	public void writeTo(ByteBuf buffer) {
		buffer.writeInt(worldid);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}
}