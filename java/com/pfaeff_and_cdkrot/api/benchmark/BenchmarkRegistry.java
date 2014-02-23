package com.pfaeff_and_cdkrot.api.benchmark;

import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;

import net.minecraft.entity.player.EntityPlayerMP;

public class BenchmarkRegistry implements INetworkBenchmarkProcessor
{
	//TODO: implement basic security addon.
	//TODO: (distance check).
	public static final int API_VERSION = 3;
	public static final BenchmarkRegistry instance = new BenchmarkRegistry();
	private INetworkBenchmarkProcessor[] processors = null;
	public void register(INetworkBenchmarkProcessor p)
	{
		if (processors == null)
			processors = new INetworkBenchmarkProcessor[]{p};
		INetworkBenchmarkProcessor[] temp = new INetworkBenchmarkProcessor[processors.length+1];
		System.arraycopy(processors, 0, temp, 0, processors.length);
		temp[processors.length]=p;

		processors = temp;
	}
	
	public boolean onTextChanged(TileEntityBenchmark tile, String newtext, EntityPlayerMP p)
	{
		if (processors!=null)
			for (INetworkBenchmarkProcessor proc: processors)
				if (!proc.onTextChanged(tile, newtext, p))
					return false;
		return true;
	}
	
	public boolean onBenchmark(TileEntityBenchmark tile, String echotext)
	{
		if (processors!=null)
			for (INetworkBenchmarkProcessor proc: processors)
				if (!proc.onBenchmark(tile, echotext))
					return false;
		return true;
	}
	
	public boolean requestEditor(TileEntityBenchmark tile, EntityPlayerMP player)
	{
		if (processors!=null)
			for (INetworkBenchmarkProcessor proc: processors)
				if (!proc.requestEditor(tile, player))
					return false;
		return true;
	}
}
