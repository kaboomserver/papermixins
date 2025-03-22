package pw.kaboom.papermixins.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import pw.kaboom.papermixins.config.PaperMixinsConfig;

import java.io.File;
import java.util.List;
import java.util.Set;

public final class PaperMixinsMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("kaboom-paper-mixins");
    private String mixinPackage;
    private PaperMixinsConfig config;

    @Override
    public void onLoad(final String mixinPackage) {
        this.mixinPackage = mixinPackage + ".";
        this.config = new PaperMixinsConfig(new File("config/kaboom-paper-mixins.properties"));
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        if (!mixinClassName.startsWith(this.mixinPackage)) {
            LOGGER.warn("Expected mixin '{}' to start with package root '{}', treating as foreign and disabling!",
                    mixinClassName, this.mixinPackage);
            return false;
        }

        final String mixin = mixinClassName.substring(this.mixinPackage.length());
        return this.config.canLoad(mixin);
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName,
                         final IMixinInfo mixinInfo) {
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName,
                          final IMixinInfo mixinInfo) {
    }
}