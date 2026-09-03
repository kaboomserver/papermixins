package pw.kaboom.papermixins.util;

import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Unique;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class ReconnectPenalty {
    public static final Map<InetAddress, Penalty> PENALTY_MAP = new ConcurrentHashMap<>();

    public static final int BASE_PENALTY = Math.max(0, Math.toIntExact(Bukkit.getServer().getConnectionThrottle() / 50L));
    public static final int MAX_PENALTY = BASE_PENALTY * 3;
    public static final long ENTRY_EXPIRY = ((long) MAX_PENALTY * 2) * 50;

    public record Penalty(AtomicInteger penalty, AtomicLong touched) {

    }

    private ReconnectPenalty() {

    }
}
