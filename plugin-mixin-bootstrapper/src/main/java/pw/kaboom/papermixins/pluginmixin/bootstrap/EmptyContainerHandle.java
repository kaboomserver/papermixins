package pw.kaboom.papermixins.pluginmixin.bootstrap;

import org.spongepowered.asm.launch.platform.container.IContainerHandle;

import java.util.Collection;
import java.util.Collections;

public final class EmptyContainerHandle implements IContainerHandle {
    @Override
    public String getAttribute(final String name) {
        return null;
    }

    @Override
    public Collection<IContainerHandle> getNestedContainers() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
