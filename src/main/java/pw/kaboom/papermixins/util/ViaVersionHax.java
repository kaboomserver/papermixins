package pw.kaboom.papermixins.util;

import com.google.common.base.Suppliers;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class ViaVersionHax {
    private static final Logger LOGGER = LoggerFactory.getLogger("papermixins.viaversionhax");

    private record ViaVersionMethods(
            Method connectionMethod,
            Method getProtocolInfo,
            Method getProtocolVersion,
            Method getProtocolVersionNumber
    ) {
    }

    private record ViaVersionData(boolean isPluginPresent, @Nullable ViaVersionMethods methods) {
    }

    private static final Supplier<ViaVersionData> METHOD_SUPPLIER = Suppliers.memoize(() -> {
        final Plugin viaVersionPlugin = Bukkit.getPluginManager().getPlugin("ViaVersion");
        if (viaVersionPlugin == null) return new ViaVersionData(false, null);
        try {
            final ClassLoader viaClassLoader = viaVersionPlugin.getClass()
                    .getClassLoader();
            final Class<?> encodeHandlerClassInstance = viaClassLoader
                    .loadClass("com.viaversion.viaversion.platform.ViaEncodeHandler");
            final Method connectionMethod = encodeHandlerClassInstance.getMethod("connection");
            final Class<?> userConnectionClassInstance = viaClassLoader
                    .loadClass("com.viaversion.viaversion.api.connection.UserConnection");
            final Method getProtocolInfo = userConnectionClassInstance.getMethod("getProtocolInfo");
            final Class<?> protocolInfoClassInstance = viaClassLoader
                    .loadClass("com.viaversion.viaversion.api.connection.ProtocolInfo");
            final Method getProtocolVersion = protocolInfoClassInstance.getMethod("protocolVersion");
            final Class<?> protocolVersionClassInstance = viaClassLoader.loadClass(
                    "com.viaversion.viaversion.api.protocol.version.ProtocolVersion"
            );
            final Method getProtocolVersionNumber = protocolVersionClassInstance.getMethod("getVersion");
            return new ViaVersionData(
                    true,
                    new ViaVersionMethods(
                            connectionMethod,
                            getProtocolInfo,
                            getProtocolVersion,
                            getProtocolVersionNumber
                    )
            );
        } catch (final Exception e) {
            LOGGER.error("Failed to reflectively access protocol version via ViaVersion, even though the plugin is present", e);
            return new ViaVersionData(true, null);
        }
    });

    private ViaVersionHax() {

    }

    public static int getOriginalVersion(final Channel nettyChannel) {
        final ViaVersionData viaData = METHOD_SUPPLIER.get();
        // ViaVersion isn't present, so assume we only support the latest Minecraft version
        if (!viaData.isPluginPresent) return Integer.MAX_VALUE;
        final ViaVersionMethods methods = viaData.methods;
        // This means that our reflection failed - return -1 since protocol translation may still occur
        if (methods == null) return -1;
        final Object encodeHandler = nettyChannel.pipeline().get("via-encoder");
        if (encodeHandler == null) return -1;

        try {
            final Object userConnection = methods.connectionMethod.invoke(encodeHandler);
            final Object protocolInfo = methods.getProtocolInfo.invoke(userConnection);
            final Object protocolVersion = methods.getProtocolVersion.invoke(protocolInfo);
            return (Integer) methods.getProtocolVersionNumber.invoke(protocolVersion);
        } catch (final Exception e) {
            LOGGER.error("Failed to reflectively access protocol version via ViaVersion, even though the plugin is present", e);
            return -1;
        }
    }
}
