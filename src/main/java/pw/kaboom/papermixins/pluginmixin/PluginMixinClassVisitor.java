package pw.kaboom.papermixins.pluginmixin;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.util.asm.ASM;

import java.util.HashSet;
import java.util.Set;

/**
 * Visits a class, removes the PluginMixin annotation and stores the PluginMixin target and target classes
 */
public final class PluginMixinClassVisitor extends ClassVisitor {
    private static final String PLUGIN_MIXIN_DESCRIPTOR = PluginMixin.class.descriptorString();
    private static final String MIXIN_DESCRIPTOR = Mixin.class.descriptorString();

    private String targetPlugin;
    public final Set<String> targetClasses = new HashSet<>();

    public PluginMixinClassVisitor(final ClassVisitor parent) {
        super(ASM.API_VERSION, parent);
    }

    public @Nullable String getTargetPlugin() {
        return this.targetPlugin;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (descriptor.equals(PLUGIN_MIXIN_DESCRIPTOR)) {
            PluginMixinLoader.LOGGER.debug("Found plugin mixin annotation on class");
            return new AnnotationVisitor(this.api, null) {
                @Override
                public void visit(final String name, final Object value) {
                    if (!name.equals("value")) return;
                    targetPlugin = (String) value;
                }
            };
        } else if (descriptor.equals(MIXIN_DESCRIPTOR)) {
            PluginMixinLoader.LOGGER.debug("Found mixin annotation on class, searching for target classes");
            return new AnnotationVisitor(this.api, super.visitAnnotation(descriptor, visible)) {
                public AnnotationVisitor visitArray(final String name) {
                    switch (name) {
                        case "value", "targets" -> {
                            return new AnnotationVisitor(this.api, this.av.visitArray(name)) {
                                @Override
                                public void visit(final String name, final Object value) {
                                    final String target;
                                    if (value instanceof final String stringTarget) {
                                        target = stringTarget;
                                    } else if (value instanceof final Type type) {
                                        final String descriptor = type.getDescriptor();
                                        target = descriptor.substring(1, descriptor.length() - 1);
                                    } else {
                                        throw new IllegalStateException("Value is invalid type");
                                    }

                                    targetClasses.add(target.replace('/', '.'));
                                    super.visit(name, value);
                                }
                            };
                        }
                    }

                    return super.visitArray(name);
                }
            };
        }

        return super.visitAnnotation(descriptor, visible);
    }
}
