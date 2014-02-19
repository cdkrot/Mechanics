package com.pfaeff_and_cdkrot.api.benchmark;

import com.pfaeff_and_cdkrot.tileentity.TileEntityBenchmark;

import cpw.mods.fml.common.network.Player;

public class BenchmarkRegistry implements INetworkBenchmarkProcessor
{
	public static final int API_VERSION = 3;
	public static final BenchmarkRegistry instance = new BenchmarkRegistry();
	private INetworkBenchmarkProcessor[] processors = null;
	public void register(INetworkBenchmarkProcessor p)
	{
		if (processors == null)
			processors = new INetworkBenchmarkProcessor[]{p};
		INetworkBenchmarkProcessor[] temp = new INetworkBenchmarkProcessor[processors.length+1];
		for (int i = processors.length; i>0; i--)
			temp[i] = processors[i];
		temp[processors.length+1]=p;
		processors = temp;
	}
	
	public boolean onTextChanged(TileEntityBenchmark tile, String newtext, Player p)
	{
		if (processors!=null)
			for (INetworkBenchmarkProcessor proc: processors)
				if (proc.onTextChanged(tile, newtext, p) == false)
					return false;
		return true;
	}
	
	public boolean onBenchmark(TileEntityBenchmark tile, String echotext)
	{
		if (processors!=null)
			for (INetworkBenchmarkProcessor proc: processors)
				if (proc.onBenchmark(tile, echotext) == false)
					return false;
		return true;
	}
	
	public boolean requestEditor(TileEntityBenchmark tile, Player player)
	{
		if (processors!=null)
			for (INetworkBenchmarkProcessor proc: processors)
				if (proc.requestEditor(tile, player) == false)
					return false;
		return true;
	}
}
