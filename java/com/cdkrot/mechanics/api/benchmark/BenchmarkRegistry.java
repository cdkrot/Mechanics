package com.cdkrot.mechanics.api.benchmark;

import com.cdkrot.mechanics.Mechanics;
import net.minecraft.entity.player.EntityPlayerMP;

import com.cdkrot.mechanics.tileentity.TileEntityBenchmark;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BenchmarkRegistry implements INetworkBenchmarkProcessor
{

	public static final int API_VERSION = 3;
	public static final BenchmarkRegistry instance = new BenchmarkRegistry();
	private INetworkBenchmarkProcessor[] processors = null;

	static
	{
		instance.register(new BasicBenchmarkSecurity());
	}


	public void register(INetworkBenchmarkProcessor p)
	{
		if (processors == null)
			processors = new INetworkBenchmarkProcessor[] { p };
		INetworkBenchmarkProcessor[] temp = new INetworkBenchmarkProcessor[processors.length + 1];
		System.arraycopy(processors, 0, temp, 0, processors.length);
		temp[processors.length] = p;

		processors = temp;
	}

	public boolean onTextChanged(TileEntityBenchmark tile, String newtext, EntityPlayerMP p)
	{
		if (processors != null)
			for (INetworkBenchmarkProcessor proc : processors)
				if (!proc.onTextChanged(tile, newtext, p))
				{
					Mechanics.modLogger.warn
						("[BenchmarkSecurity] Security addon %s canceled TextChanged event, tile=%s, player=%s, newtext=%s",
							proc.toString(), p.toString(), newtext);
					return false;
				}
		return true;
	}

	public boolean onBenchmark(TileEntityBenchmark tile, String echotext)
	{
		if (processors != null)
			for (INetworkBenchmarkProcessor proc : processors)
				if (!proc.onBenchmark(tile, echotext))
				{
					Mechanics.modLogger.warn
							("[BenchmarkSecurity] Security addon %s canceled onBenchmark event, tile=%s, echotext=%s",
									proc.toString(), echotext);
					return false;
				}
		return true;
	}

	public boolean requestEditor(TileEntityBenchmark tile, EntityPlayerMP p)
	{
		if (processors != null)
			for (INetworkBenchmarkProcessor proc : processors)
				if (!proc.requestEditor(tile, p))
				{
					Mechanics.modLogger.warn
							("[BenchmarkSecurity] Security addon %s canceled RequestEditor event, tile=%s, player=%s",
									proc.toString(), p.toString());
					return false;
				}
		return true;
	}
}
