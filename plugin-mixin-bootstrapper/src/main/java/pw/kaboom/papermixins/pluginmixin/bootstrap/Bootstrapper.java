package pw.kaboom.papermixins.pluginmixin.bootstrap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterJava;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigSource;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
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

@SuppressWarnings({"unchecked", "deprecated"})
public final class Bootstrapper extends MixinServiceAbstract
        implements IContainerHandle, IClassProvider, IClassBytecodeProvider,
        IGlobalPropertyService, IMixinConfigSource, IMixinConfigPlugin,
        IPluginMixinBootstrapper {
    private static final List<String> PLATFORM_AGENTS =
            Collections.singletonList("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    private static final String INJECTED_MIXIN_CONFIG_NAME = "config.json";
    private static final String ID = "papermixins$pluginmixin";
    private static URLClassLoader PARENT;
    private static URLClassLoader UNMODIFIED_PARENT;
    private static final Map<String, ClassNode> CLASS_NODE_CACHE = new ConcurrentHashMap<>();
    private final Map<BlackBoardKey, Object> properties = new ConcurrentHashMap<>();
    private final Map<String, BlackBoardKey> blackBored = new ConcurrentHashMap<>();
    private static Set<String> TARGETS;
    private static final JsonObject CONFIG_OBJECT = new JsonObject();
    private IMixinTransformer mixinTransformer;
    private static Bootstrapper instance;

    static {
        // TODO: Make gradle set the compatibility level here
        CONFIG_OBJECT.addProperty("minVersion", "0.8");
        CONFIG_OBJECT.addProperty("compatibilityLevel", "JAVA_21");
        CONFIG_OBJECT.addProperty("required", true);

        final JsonObject injectors = new JsonObject();
        injectors.addProperty("defaultRequire", 1);

        CONFIG_OBJECT.add("injectors", injectors);
        CONFIG_OBJECT.addProperty("package", "pw.kaboom.papermixins.pluginmixin.mixins");
    }

    @Override
    public String getName() {
        return ID;
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
        TARGETS = new HashSet<>();

        final JsonArray mixinsArray = new JsonArray();
        for (final LoadedPluginMixin mixinNode : mixinNodes) {
            CLASS_NODE_CACHE.put(mixinNode.binaryName(), mixinNode.classNode());
            TARGETS.addAll(mixinNode.targetClasses());
            mixinsArray.add(new JsonPrimitive(mixinNode.configName()));
        }

        CONFIG_OBJECT.add("mixins", mixinsArray);

        MixinBootstrap.init();
        MixinExtrasBootstrap.init();
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

    @Override
    public Collection<String> getPlatformAgents() {
        return PLATFORM_AGENTS;
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return this;
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
    public String getAttribute(final String name) {
        return null;
    }

    @Override
    public Collection<IContainerHandle> getNestedContainers() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDescription() {
        return ID;
    }

    @Override
    public URL[] getClassPath() {
        return new URL[0];
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

    private static InputStream findClassBytesRecursive(String name) {
        ClassLoader loader = UNMODIFIED_PARENT;
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

        final InputStream stream = findClassBytesRecursive(resourceName);
        if (stream == null) throw new ClassNotFoundException(binaryName);

        node = readClassBytes(stream.readAllBytes());
        stream.close();

        CLASS_NODE_CACHE.put(binaryName, node);
        return node;
    }

    @Override
    public byte[] transformClassBytes(final String classBinaryName, final byte[] originalBytes) {
        final boolean runTransform = TARGETS.contains(classBinaryName);
        final byte[] transformedBytes = runTransform ?
                this.mixinTransformer.transformClass(
                        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT),
                        classBinaryName,
                        originalBytes)
                : originalBytes;

        if (runTransform || !CLASS_NODE_CACHE.containsKey(classBinaryName)) {
            CLASS_NODE_CACHE.put(classBinaryName, readClassBytes(transformedBytes));
        }

        return transformedBytes;
    }

    @Override
    public void onLoad(final String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass,
                         final String mixinClassName, final IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass,
                          final String mixinClassName, final IMixinInfo mixinInfo) {

    }

    private static final class BlackBoardKey implements IPropertyKey {

    }

    @Override
    public IPropertyKey resolveKey(final String name) {
        return this.blackBored.computeIfAbsent(name, key -> new BlackBoardKey());
    }

    @Override
    public <T> T getProperty(final IPropertyKey key) {
        return (T) this.properties.get(key);
    }

    @Override
    public void setProperty(final IPropertyKey key, final Object value) {
        this.properties.put((BlackBoardKey) key, value);
    }

    @Override
    public <T> T getProperty(final IPropertyKey key, final T defaultValue) {
        if (key == null) return defaultValue;
        return (T) this.properties.getOrDefault((BlackBoardKey) key, defaultValue);
    }

    @Override
    public String getPropertyString(final IPropertyKey key, final String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    @Override
    protected ILogger createLogger(final String name) {
        return new LoggerAdapterJava(name);
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
}
