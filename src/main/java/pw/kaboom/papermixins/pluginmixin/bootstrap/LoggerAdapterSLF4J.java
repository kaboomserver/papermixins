package pw.kaboom.papermixins.pluginmixin.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

import java.net.URLClassLoader;

final class LoggerAdapterSLF4J extends LoggerAdapterAbstract {
    private final Logger logger;

    LoggerAdapterSLF4J(final URLClassLoader classLoader, final String label) {
        super(label);

        String name = Bootstrapper.ID + "$" + classLoader.getName();
        if (!label.equals("mixin")) name += "$" + label;
        
        this.logger = LoggerFactory.getLogger(name);
    }

    @Override
    public String getType() {
        return "SLF4J";
    }

    @Override
    public void catching(final Level level, final Throwable t) {
        switch (level) {
            case ERROR, FATAL -> this.logger.error("Caught error", t);
            case WARN -> this.logger.warn("Caught warn", t);
            case INFO -> this.logger.info("Caught info", t);
            case DEBUG -> this.logger.debug("Caught debug", t);
            case TRACE -> this.logger.trace("Caught trace", t);
        }
    }

    @Override
    public void log(final Level level, final String message, final Object... params) {
        switch (level) {
            case ERROR, FATAL -> this.logger.error(message, params);
            case WARN -> this.logger.warn(message, params);
            case INFO -> this.logger.info(message, params);
            case DEBUG -> this.logger.debug(message, params);
            case TRACE -> this.logger.trace(message, params);
        }
    }

    @Override
    public void log(final Level level, final String message, final Throwable t) {
        switch (level) {
            case ERROR, FATAL -> this.logger.error(message, t);
            case WARN -> this.logger.warn(message, t);
            case INFO -> this.logger.info(message, t);
            case DEBUG -> this.logger.debug(message, t);
            case TRACE -> this.logger.trace(message, t);
        }
    }

    @Override
    public <T extends Throwable> T throwing(final T t) {
        this.logger.error("Thrown", t);
        return t;
    }
}
