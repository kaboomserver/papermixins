package pw.kaboom.papermixins.pluginmixin.bootstrap;

import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.Constants;
import org.spongepowered.asm.util.ReEntranceLock;

import java.util.*;

public abstract class SimpleMixinService implements IMixinService {
    private static final IContainerHandle PRIMARY_CONTAINER = new EmptyContainerHandle();

    private final ReEntranceLock lock = new ReEntranceLock(1);
    private final Map<String, ILogger> loggerCache = new HashMap<>();

    protected abstract ILogger createLogger(final String name);

    @Override
    public synchronized ILogger getLogger(final String name) {
        return loggerCache.computeIfAbsent(name, this::createLogger);
    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return this.lock;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return MixinEnvironment.Phase.PREINIT;
    }

    @Override
    public void checkEnv(final Object bootSource) {

    }

    @Override
    public void beginPhase() {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void init() {

    }

    @Override
    public String getSideName() {
        return Constants.SIDE_UNKNOWN;
    }

    // Unsupported
    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.emptyList();
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return Collections.emptyList();
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return null;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return null;
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return PRIMARY_CONTAINER;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }
}
