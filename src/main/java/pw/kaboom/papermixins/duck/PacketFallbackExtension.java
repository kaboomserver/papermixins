package pw.kaboom.papermixins.duck;

import net.minecraft.network.protocol.Packet;

public interface PacketFallbackExtension {
    Packet<?> papermixins$getFallback();
}
