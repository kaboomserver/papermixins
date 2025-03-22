# papermixins

Paper Mixins is a Ignite mod that applies various Free-OP related patches to Paper.

The mod is created for the Kaboom server.

## Configuring

The configuration is located in `config/kaboom-paper-mixins.properties`. It is not created automatically.
It uses the java properties format (the same one as server.properties).

Currently, all you can do through the config is toggle different fixes off.

Example:
```properties
# Disable this optimization specifically
perf.command_block_optimization=false

# Disable all new "features"
feat=false

# Except for this one
feat.execute_vanilla_only=true
```

## Compiling

Use [Gradle](https://gradle.org/) to compile the mod.
For security reasons, the gradle wrapper is not provided. You must use a system-wide installation of Gradle.
```bash
gradle build
```
The generated .jar file will be located in the build/libs/ folder.

## License
[Unlicense](https://unlicense.org/)
