package pw.kaboom.papermixins.pluginmixin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface PluginMixin {
    String value();
}
