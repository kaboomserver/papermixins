package pw.kaboom.papermixins.pluginmixin.interop;

import org.objectweb.asm.tree.ClassNode;

import java.util.Set;

public record LoadedPluginMixin(String binaryName,
        ClassNode classNode,
        Set<String> targetClasses,
        String configName) {
}
