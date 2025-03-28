package pw.kaboom.papermixins.pluginmixin.interop;

public interface IPluginMixinBootstrapper {
    byte[] transformClassBytes(String classBinaryName, byte[] originalBytes);
}
