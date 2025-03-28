package pw.kaboom.papermixins.pluginmixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.util.asm.ASM;
import pw.kaboom.papermixins.pluginmixin.interop.LoadedPluginMixin;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PluginMixinLoader {
    public static final Logger LOGGER = LoggerFactory.getLogger("papermixins$pluginmixin");
    private static Map<String, List<LoadedPluginMixin>> PLUGIN_MIXINS = new HashMap<>();
    private static final String GRAND_FATHERED_URL_SEARCH_PATH = "META-INF/jars";
    public static final URL[] GRAND_FATHERED_URLS;
    private static final Object LOCK = new Object();
    private static boolean APPLIED = false;

    private PluginMixinLoader() {

    }

    private static byte[] hash(final URL url) {
        try {
            try (final InputStream inputStream = url.openStream()) {
                return MessageDigest.getInstance("MD5").digest(inputStream.readAllBytes());
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to hash " + url, e);
        }
    }

    static {
        final String packageName = PluginMixinLoader.class.getPackageName();
        final String mixinPackagePath = packageName.replace('.', '/')
                + "/mixins/";
        final String absoluteFile = PluginMixinLoader.class.getTypeName().replace('.', '/') + ".class";
        final ClassLoader classLoader = PluginMixinLoader.class.getClassLoader();
        final URL absoluteURL = classLoader.getResource(absoluteFile);
        if (absoluteURL == null) throw new IllegalStateException("Failed to locate myself");
        final URLConnection urlConnection;
        try {
            urlConnection = absoluteURL.openConnection();
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to open a connection to JAR file", e);
        }
        if (!(urlConnection instanceof final JarURLConnection jarURLConnection))
            throw new IllegalStateException("URL connection is not an instance of JarURLConnection");
        final JarFile jarFile;
        try {
            jarFile = jarURLConnection.getJarFile();
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to open mod jar file", e);
        }

        final Iterator<JarEntry> jarEntryIterator = jarFile.stream().iterator();
        final List<URL> grandFatheredUrls = new ObjectArrayList<>();
        int loadedMixinCount = 0;

        while (jarEntryIterator.hasNext()) {
            final JarEntry entry = jarEntryIterator.next();
            final String name = entry.getName();

            if (name.startsWith(GRAND_FATHERED_URL_SEARCH_PATH)) {
                final URL url = classLoader.getResource(name);
                if (url == null) continue;
                if (!(url.getFile().endsWith(".jar"))) continue;

                final byte[] hash = hash(url);
                final Path libraryPath = Path.of("libraries", "papermixins", HexFormat.of().formatHex(hash) + ".jar");
                final URL pathUrl;

                try {
                    pathUrl = libraryPath.toUri().toURL();
                } catch (final MalformedURLException e) {
                    throw new IllegalStateException("Path is not valid for a URL", e);
                }

                if (Files.notExists(libraryPath)) {
                    try {
                        Files.createDirectories(libraryPath.getParent());
                        Files.copy(url.openStream(), libraryPath);
                    } catch (final IOException e) {
                        throw new IllegalStateException("Failed to save library to disk", e);
                    }
                }

                grandFatheredUrls.add(pathUrl);
            } else if (name.startsWith(mixinPackagePath)) {
                if (!name.endsWith(".class")) continue;

                final ClassNode classNode = new ClassNode(ASM.API_VERSION);
                final PluginMixinClassVisitor pluginMixinClassVisitor = new PluginMixinClassVisitor(classNode);

                try (final InputStream entryInputStream = jarFile.getInputStream(entry)) {
                    new ClassReader(entryInputStream).accept(pluginMixinClassVisitor, 0);

                    final String targetPlugin = pluginMixinClassVisitor.getTargetPlugin();
                    if (targetPlugin == null) {
                        LOGGER.info("Class {} isn't a plugin mixin. Discarding!", classNode.name);
                        continue;
                    }

                    final Set<String> targetClasses = pluginMixinClassVisitor.targetClasses;
                    if (targetClasses.isEmpty()) {
                        LOGGER.info("Mixin {} doesn't target any classes. Discarding!", classNode.name);
                        continue;
                    }

                    LOGGER.info("Mixin {} targets {}", classNode.name, targetClasses);
                    loadedMixinCount++;
                    final String binaryName = classNode.name.replace('/', '.');
                    PLUGIN_MIXINS.computeIfAbsent(targetPlugin, key -> new ObjectArrayList<>())
                            .add(new LoadedPluginMixin(binaryName,
                                    classNode,
                                    targetClasses,
                                    binaryName.substring(mixinPackagePath.length())
                            ));
                } catch (final IOException e) {
                    throw new IllegalStateException("Couldn't open entry in zip" + e);
                }
            }
        }

        GRAND_FATHERED_URLS = grandFatheredUrls.toArray(URL[]::new);

        LOGGER.info("Loaded {} grand-fathered dependencies", GRAND_FATHERED_URLS.length);
        LOGGER.info("Loaded {} plugin mixins, targeting {} unique plugins.", loadedMixinCount, PLUGIN_MIXINS.size());
    }

    public static @Nullable List<LoadedPluginMixin> getPluginMixins(final String pluginName) {
        synchronized (LOCK) {
            if (APPLIED) {
                LOGGER.error("Uh-oh! Mixins were requested for plugin {}, but we already applied mixins...", pluginName);
                return null;
            }

            return PLUGIN_MIXINS.remove(pluginName);
        }
    }

    public static void finishedApplying() {
        synchronized (LOCK) {
            APPLIED = true;
            PLUGIN_MIXINS = null;
        }
    }
}
