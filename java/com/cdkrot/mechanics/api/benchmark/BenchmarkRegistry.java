package com.cdkrot.mechanics.api.benchmark;

import com.cdkrot.mechanics.Mechanics;
import net.minecraft.entity.player.EntityPlayerMP;

import com.cdkrot.mechanics.tileentity.TileEntityBenchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkRegistry implements INetworkBenchmarkProcessor {
    public static final int API_VERSION = 3;
    public static final BenchmarkRegistry instance = new BenchmarkRegistry();
    private List<INetworkBenchmarkProcessor> processors = new ArrayList<INetworkBenchmarkProcessor>();

    static {
        instance.register(new BasicBenchmarkSecurity());
    }

    public void register(INetworkBenchmarkProcessor p) {
        processors.add(p);
        Mechanics.modLogger
                .info("[BenchmarkSecurity] Registered security addon "
                        + p.toString());
    }

    @Override
    public boolean onTextChanged(TileEntityBenchmark tile, String newtext,
            EntityPlayerMP p) {
        if (processors != null)
            for (INetworkBenchmarkProcessor proc : processors)
                if (!proc.onTextChanged(tile, newtext, p)) {
                    Mechanics.modLogger
                            .warn("[BenchmarkSecurity] Security addon %s canceled TextChanged event, tile=%s, player=%s, newtext=%s",
                                    proc.toString(), p.toString(), newtext);
                    return false;
                }
        return true;
    }

    @Override
    public boolean onBenchmark(TileEntityBenchmark tile, String echotext) {
        if (processors != null)
            for (INetworkBenchmarkProcessor proc : processors)
                if (!proc.onBenchmark(tile, echotext)) {
                    Mechanics.modLogger
                            .warn("[BenchmarkSecurity] Security addon %s canceled onBenchmark event, tile=%s, echotext=%s",
                                    proc.toString(), echotext);
                    return false;
                }
        return true;
    }

    @Override
    public boolean requestEditor(TileEntityBenchmark tile, EntityPlayerMP p) {
        if (processors != null)
            for (INetworkBenchmarkProcessor proc : processors)
                if (!proc.requestEditor(tile, p)) {
                    Mechanics.modLogger
                            .warn("[BenchmarkSecurity] Security addon %s canceled RequestEditor event, tile=%s, player=%s",
                                    proc.toString(), p.toString());
                    return false;
                }
        return true;
    }
}
