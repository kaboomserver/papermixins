package pw.kaboom.papermixins.pluginmixin.bootstrap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.*;
import pw.kaboom.papermixins.pluginmixin.interop.IPluginMixinBootstrapper;
import pw.kaboom.papermixins.pluginmixin.interop.LoadedPluginMixin;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"deprecated"})
public final class Bootstrapper extends MixinServiceAbstract
        implements IClassProvider, IClassBytecodeProvider, IPluginMixinBootstrapper {
    private static final IContainerHandle PRIMARY_CONTAINER = new EmptyContainerHandle();
    private static final List<String> PLATFORM_AGENTS =
            Collections.singletonList("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    private static final String INJECTED_MIXIN_CONFIG_NAME = "config.json";
    public static final String ID = "papermixins$pluginmixin";

    private static final ThreadLocal<String> CURRENT_TRANSFORM_TARGET = new ThreadLocal<>();
    private static final Map<String, ClassNode> CLASS_NODE_CACHE = new ConcurrentHashMap<>();
    private static final Set<String> TARGETS = new HashSet<>();
    private static final JsonObject CONFIG_OBJECT = new JsonObject();

    private static URLClassLoader PARENT;
    private static URLClassLoader UNMODIFIED_PARENT;

    private static Bootstrapper instance;

    private IMixinTransformer mixinTransformer;

    static {
        // TODO: Make gradle set the compatibility level here
        CONFIG_OBJECT.addProperty("minVersion", "0.8");
        CONFIG_OBJECT.addProperty("compatibilityLevel", "JAVA_21");
        CONFIG_OBJECT.addProperty("required", true);
        CONFIG_OBJECT.addProperty("plugin", "pw.kaboom.papermixins.pluginmixin.bootstrap.MixinExtrasConfigPlugin");

        final JsonObject injectors = new JsonObject();
        injectors.addProperty("defaultRequire", 1);

        CONFIG_OBJECT.add("injectors", injectors);
        CONFIG_OBJECT.addProperty("package", "pw.kaboom.papermixins.pluginmixin.mixins");
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public static IPluginMixinBootstrapper init(final URLClassLoader parent,
                                                final URL parentUrl,
                                                final List<LoadedPluginMixin> mixinNodes) {
        UNMODIFIED_PARENT = new URLClassLoader(new URL[]{parentUrl}, parent.getParent());
        PARENT = parent;

        final JsonArray mixinsArray = new JsonArray();
        for (final LoadedPluginMixin mixinNode : mixinNodes) {
            CLASS_NODE_CACHE.put(mixinNode.binaryName(), mixinNode.classNode());
            TARGETS.addAll(mixinNode.targetClasses());
            mixinsArray.add(new JsonPrimitive(mixinNode.configName()));
        }

        CONFIG_OBJECT.add("mixins", mixinsArray);

        MixinBootstrap.init();
        Mixins.addConfiguration(INJECTED_MIXIN_CONFIG_NAME);
        return instance;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return PLATFORM_AGENTS;
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return PRIMARY_CONTAINER;
    }

    @Override
    public InputStream getResourceAsStream(final String name) {
        if (name.equals(INJECTED_MIXIN_CONFIG_NAME)) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
            new Gson().toJson(CONFIG_OBJECT, outputStreamWriter);

            try {
                outputStreamWriter.flush();
            } catch (IOException e) {
                throw new AssertionError();
            }

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        return PARENT.getResourceAsStream(name);
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        return this.getClass().getClassLoader().loadClass(name);
    }

    @Override
    public Class<?> findClass(final String name, final boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, PARENT);
    }

    @Override
    public Class<?> findAgentClass(final String name, final boolean initialize) throws ClassNotFoundException {
        throw new ClassNotFoundException("Agent class loading not supported");
    }

    @Override
    public ClassNode getClassNode(final String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, false);
    }

    @Override
    public ClassNode getClassNode(final String name, final boolean runTransformers) throws ClassNotFoundException, IOException {
        return getClassNode(name, runTransformers, 0);
    }

    private static InputStream findClassBytesRecursive(ClassLoader loader, final String name) {
        InputStream stream = null;
        while (loader != null && (stream = loader.getResourceAsStream(name)) == null) {
            loader = loader.getParent();
        }
        return stream;
    }

    private static ClassNode readClassBytes(final byte[] classBytes) {
        final ClassNode node = new ClassNode();
        new ClassReader(classBytes).accept(node, 0);
        return node;
    }

    @Override
    public ClassNode getClassNode(final String name, final boolean runTransformers, final int readerFlags)
            throws ClassNotFoundException, IOException {
        final String binaryName = name.replace('/', '.');
        final String resourceName = name.replace('.', '/') + ".class";

        ClassNode node = CLASS_NODE_CACHE.get(binaryName);
        if (node != null) return node;

        final ClassLoader targetLoader = TARGETS.contains(binaryName) && !Objects.equals(CURRENT_TRANSFORM_TARGET.get(), binaryName)
                ?
                PARENT
                :
                UNMODIFIED_PARENT;

        final InputStream stream = findClassBytesRecursive(targetLoader, resourceName);
        if (stream == null) throw new ClassNotFoundException(binaryName);

        node = readClassBytes(stream.readAllBytes());
        stream.close();

        CLASS_NODE_CACHE.put(binaryName, node);
        return node;
    }

    @Override
    public byte[] transformClassBytes(final String classBinaryName, final byte[] originalBytes) {
        final boolean runTransform = TARGETS.contains(classBinaryName);
        if (runTransform) CURRENT_TRANSFORM_TARGET.set(classBinaryName);
        final byte[] transformedBytes = runTransform ?
                this.mixinTransformer.transformClass(
                        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT),
                        classBinaryName,
                        originalBytes)
                : originalBytes;
        if (runTransform) CURRENT_TRANSFORM_TARGET.remove();

        if (runTransform || !CLASS_NODE_CACHE.containsKey(classBinaryName)) {
            CLASS_NODE_CACHE.put(classBinaryName, readClassBytes(transformedBytes));
        }

        return transformedBytes;
    }

    @Override
    protected ILogger createLogger(final String name) {
        return new LoggerAdapterSLF4J(PARENT, name);
    }

    @Override
    public void init() {
        instance = this;
        super.init();
    }

    @Override
    public void offer(final IMixinInternal internal) {
        if (internal instanceof final IMixinTransformerFactory transformerFactory) {
            this.mixinTransformer = transformerFactory.createTransformer();
        }
    }

    @Override
    public String getName() {
        return ID;
    }

    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }
}
